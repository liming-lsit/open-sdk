package com.sdk;


import com.alibaba.fastjson.JSONObject;
import com.sdk.utils.HmacSHA256;
import com.sdk.utils.HttpClient;
import com.sdk.utils.LoggerUtil;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.AbstractHttpMessage;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class OpenSDk {


    private static final int Request_Get = 0;


    private static final int Request_Post = 1;
    private static  String SERVER_ADDRESS;
    private static final String PayPrepay = "/payment/prepay";
    private static final String QueryOpenid = "/appUsers/getOpenid";
    private static String APPUID;
    private static String APPSECRET;
    private static String ACCOUNTTTOKEN;

    /**
     * 初始化服务地址和端口
     * @param serveraddress  服务器地址
     * @param appUid         APPID
     * @param accountToken   TOKEN
     * @param appSecret      SECRET
     */
    public void init(String serveraddress,String appUid,String accountToken,String appSecret) {
        if (StringUtils.isEmpty(serveraddress)) {
            LoggerUtil.error("初始化异常:serveraddress");
            throw new IllegalArgumentException("必选参数:serveraddress为空");
        }
        if (StringUtils.isEmpty(accountToken)) {
            LoggerUtil.error("初始化异常:accountToken");
            throw new IllegalArgumentException("必选参数:accountToken 为空");
        }
        if (StringUtils.isEmpty(accountToken)) {
            LoggerUtil.error("初始化异常:appSecret");
            throw new IllegalArgumentException("必选参数:appSecret 为空");
        }
        SERVER_ADDRESS = serveraddress;
        APPUID = appUid;
        APPSECRET = appSecret;
        ACCOUNTTTOKEN = accountToken;
    }

    /**
     * 获取用户信息接口
     * @param code      临时登录凭证
     * @return
     */
    public String queryOpenid(String code){
        LoggerUtil.info("[queryOpenid] code:{"+code+"},appUid:{"+APPUID+"},accountToken:{"+ACCOUNTTTOKEN+"}");
        String ret = "";
        Map<String,Object> params = queryParams(code);
        String jsonParam= JSONObject.toJSONString(params);
        try{
            ret = HttpClient.postDataJson(SERVER_ADDRESS+QueryOpenid,jsonParam);
        }catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
            return getMyError("101111", "请求异常");
        }
        return ret;
    }

    /**
     * 拼接参数
     * @param code
     * @return
     */
    public Map<String,Object> queryParams(String code){
        Map<String, Object> params = new HashMap<String, Object>();
        String nonceStr = RandomStringUtils.randomAlphanumeric(8);
        long timeStamp = System.currentTimeMillis();
        params.put("appUid", APPUID);
        params.put("code", code);
        params.put("appSecret", APPSECRET);
        params.put("timeStamp", timeStamp);
        params.put("nonceStr", nonceStr);
        String param = "app_uid=" + APPUID + "&code=" + code + "&time_stamp=" + timeStamp + "&nonce_str=" + nonceStr + "&app_secret=" + APPSECRET;
        String computed_sign = HmacSHA256.sign(param, APPSECRET);
        params.put("sign", computed_sign);
        return params;
    }


    /**
     * 创建订单
     * @param openUid    用户OpenId
     * @param orderSn    订单号
     * @param totalFee   价格
     * @param body       描述
     * @param notifyUrl  推送回掉地址
     * @return
     */
    public String createOrder(String openUid,
                              String orderSn, BigDecimal totalFee,String exchange,
                              String body,String notifyUrl,String feeType){
        LoggerUtil.info("[createOrder] openUid:{"+openUid+"},appUid:{"+APPUID+"},accountToken:{"+ACCOUNTTTOKEN+"},orderSn{"+orderSn+"},totalFee:{"+totalFee+"},exchange:{"+exchange+"},body:{"+body+"},notifyUrl:{"+notifyUrl+"}");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appUid", APPUID);
        params.put("openUid", openUid);
        params.put("outTradeNo", orderSn);
        long timeStamp = System.currentTimeMillis();
        params.put("timeStamp", timeStamp);
        String nonceStr = RandomStringUtils.randomAlphanumeric(8);
        params.put("totalFee", totalFee);
        params.put("feeType", feeType);
        params.put("exchange", exchange);
        params.put("nonceStr", nonceStr);
        params.put("body", body);
        params.put("notify_url", notifyUrl);
        String sign = createOrderSign(openUid,orderSn,totalFee,exchange,String.valueOf(timeStamp),nonceStr,feeType);
        params.put("sign", sign);
        String jsonParam=  JSONObject.toJSONString(params);
        String ret = "";
        try{
            ret = HttpClient.postDataJson(SERVER_ADDRESS+PayPrepay,jsonParam);
        }catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
            return getMyError("101111", "请求异常");
        }
        return ret;
    }

    /**
     *  创建订单签名
     * @param openUid    用户OpenId
     * @param orderSn    订单号
     * @param totalFee   价格
     * @param exchange   是否支持币种兑换
     * @param feeType    币种
     * @return
     */
    public String createOrderSign(String openUid,
                                  String orderSn, BigDecimal totalFee,String exchange,
                                  String timeStamp,String nonceStr,String feeType){
        String signString = "app_uid=" + APPUID +
                "&open_uid=" + openUid +
                "&out_trade_no=" + orderSn +
                "&time_stamp=" + timeStamp +
                "&total_fee=" + totalFee +
                "&fee_type=" +feeType+
                "&exchange=" +exchange+
                "&nonce_str=" + nonceStr +
                "&app_secret=" + APPSECRET;
        String sign = HmacSHA256.sign(signString, APPSECRET);
        return sign;
    }

    /**
     * 商户订单数据校验
     * @param outTradeNo  订单号
     * @param totalFee    价格
     * @param feeType     币种
     * @param payAt       支付时间
     * @param result
     * @param timestamp   时间戳
     * @param scode
     * @param sign
     * @return
     */
    public boolean notifyValidate(String outTradeNo,String totalFee, String feeType,String payAt,String result, String timestamp, String scode,String sign){
        LoggerUtil.info("[notifyValidate] outTradeNo:{"+outTradeNo+"},appUid:{"+APPUID+"},accountToken:{"+ACCOUNTTTOKEN+"},result{"+result+"},totalFee:{"+totalFee+"},payAt:{"+payAt+"},scode:{"+scode+"},sign:{"+sign+"}");
        boolean flag = true;
        if(StringUtils.isEmpty(APPUID)) {
            LoggerUtil.error("必选参数:appUid 为空");
            throw new IllegalArgumentException("必选参数:appUid 为空");
        }
        if(StringUtils.isEmpty(outTradeNo)) {
            LoggerUtil.error("必选参数:outTradeNo 为空");
            throw new IllegalArgumentException("必选参数:outTradeNo 为空");
        }
        if(StringUtils.isEmpty(totalFee)) {
            LoggerUtil.error("必选参数:totalFee 为空");
            throw new IllegalArgumentException("必选参数:totalFee 为空");
        }
        if(StringUtils.isEmpty(feeType)) {
            LoggerUtil.error("必选参数:feeType 为空");
            throw new IllegalArgumentException("必选参数:feeType 为空");
        }
        if(StringUtils.isEmpty(payAt)) {
            LoggerUtil.error("必选参数:payAt 为空");
            throw new IllegalArgumentException("必选参数:payAt 为空");
        }
        if(StringUtils.isEmpty(result)) {
            LoggerUtil.error("必选参数:result 为空");
            throw new IllegalArgumentException("必选参数:result 为空");
        }
        if(StringUtils.isEmpty(timestamp)) {
            LoggerUtil.error("必选参数:timestamp 为空");
            throw new IllegalArgumentException("必选参数:timestamp 为空");
        }
        if(StringUtils.isEmpty(scode)) {
            LoggerUtil.error("必选参数:scode 为空");
            throw new IllegalArgumentException("必选参数:scode 为空");
        }
        if(StringUtils.isEmpty(sign)) {
            LoggerUtil.error("必选参数:sign 为空");
            throw new IllegalArgumentException("必选参数:sign 为空");
        }
        if (!verifyNotify(APPUID, outTradeNo,
                totalFee, feeType, payAt,
                timestamp, scode,
                sign, APPSECRET)) {
            LoggerUtil.error("签名验证错误 ");
            flag = false;
        }
        return  flag;
    }


    private static String getMyError(String code, String msg) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{");
        stringBuffer.append("statusCode:"+code);
        stringBuffer.append(",");
        stringBuffer.append("statusMsg:"+msg);
        stringBuffer.append("}");
        return stringBuffer.toString();
    }


    private HttpRequestBase getHttpRequestBase(int get, String url) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        HttpRequestBase mHttpRequestBase = null;
        if (get == Request_Get)
            mHttpRequestBase = new HttpGet(url);
        else if (get == Request_Post)
            mHttpRequestBase = new HttpPost(url);
        setHttpHeader(mHttpRequestBase);
        return mHttpRequestBase;
    }

    /**
     * 订单签名
     * @param appUid
     * @param outTradeNo
     * @param totalFee
     * @param feeType
     * @param payAt
     * @param timestamp
     * @param scode
     * @param sign
     * @param appSecret
     * @return
     */
    public Boolean verifyNotify(String appUid, String outTradeNo,
                                String totalFee, String feeType, String payAt,
                                String timestamp, String scode,
                                String sign, String appSecret) {
        String params = "app_uid=" + appUid +
                "&out_trade_no=" + outTradeNo +
                "&total_fee=" + totalFee +
                "&fee_type=" + feeType +
                "&pay_at=" + payAt +
                "&timestamp=" + timestamp +
                "&scode=" + scode +
                "&app_secret=" + appSecret;
        String computedSign = HmacSHA256.sign(params, appSecret);

        return sign.equals(computedSign);
    }

    private void setHttpHeader(AbstractHttpMessage httpMessage) {
        httpMessage.setHeader("Accept", "application/json");
        httpMessage.setHeader("Content-Type", "application/json;charset=utf-8");
    }
}
