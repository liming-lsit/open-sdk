package com.sdk;


import com.alibaba.fastjson.JSONObject;
import com.sdk.utils.HttpClient;
import com.sdk.utils.LoggerUtil;
import com.sdk.utils.SignUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.AbstractHttpMessage;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class OpenSDk {


    private static final int Request_Get = 0;


    private static final int Request_Post = 1;
    private static  String SERVER_ADDRESS;
    private static final String PayPrepay = "/payment/prepay";
    private static final String QueryOpenid = "/appUsers/queryOpenId";
    private static final String QueryCode = "/appUsers/queryCode";
    private static String APPUID;
    private static String APPSECRET;

    /**
     * 初始化服务地址和端口
     * @param serveraddress  服务器地址
     * @param appUid         APPID
     * @param appSecret      SECRET
     */
    public void init(String serveraddress,String appUid,String appSecret) {
        if (StringUtils.isEmpty(serveraddress)) {
            LoggerUtil.error("初始化异常:serveraddress");
            throw new IllegalArgumentException("必选参数:serveraddress为空");
        }
        if (StringUtils.isEmpty(appSecret)) {
            LoggerUtil.error("初始化异常:appSecret");
            throw new IllegalArgumentException("必选参数:appSecret 为空");
        }
        SERVER_ADDRESS = serveraddress;
        APPUID = appUid;
        APPSECRET = appSecret;
    }

  /*  *//**
     * 获取用户登陆凭证
     * @param accountUid   App账号ID
     * @return
     *//*
    public String queryCode(String accountUid,String accountToken){
        LoggerUtil.info("[queryCode] accountUid:{"+accountUid+"},appUid:{"+APPUID+"},accountToken:{"+accountToken+"}");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appUid", APPUID);
        params.put("accountUid", accountUid);
        params.put("accountToken", accountToken);
        String ret = "";
        String jsonParam= JSONObject.toJSONString(params);
        try{
            ret = HttpClient.postDataJson(SERVER_ADDRESS+QueryCode,jsonParam);
        }catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
            return getMyError("101111", "请求异常");
        }
        return ret;
    }*/

    /**
     * 获取用户信息接口
     * @param code      临时登录凭证
     * @return
     */
    public String queryOpenid(String code){
        LoggerUtil.info("[queryOpenid] code:{"+code+"},appUid:{"+APPUID+"}");
        String ret = "";
        Map<String,String> params = queryParams(code);
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
    public Map<String,String> queryParams(String code){
        Map<String, String> params = new HashMap<String, String>();
        String nonceStr = RandomStringUtils.randomAlphanumeric(8);
        long timeStamp = System.currentTimeMillis();
        params.put("appUid", APPUID);
        params.put("code", code);
        params.put("timeStamp", String.valueOf(timeStamp));
        params.put("nonceStr", nonceStr);
        String computed_sign =  SignUtil.getSign(params,APPSECRET);
        params.put("appSecret", APPSECRET);
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
                              String orderSn, String totalFee,String exchange,
                              String body,String notifyUrl,String currency){
        LoggerUtil.info("[createOrder] openUid:{"+openUid+"},appUid:{"+APPUID+"},orderSn{"+orderSn+"},totalFee:{"+totalFee+"},exchange:{"+exchange+"},body:{"+body+"},notifyUrl:{"+notifyUrl+"}");
        Map<String, String> params = new HashMap<String, String>();
        params.put("appUid", APPUID);
        params.put("openUid", openUid);
        params.put("outTradeNo", orderSn);
        long timeStamp = System.currentTimeMillis();
        params.put("timeStamp", String.valueOf(timeStamp));
        String nonceStr = RandomStringUtils.randomAlphanumeric(8);
        params.put("totalFee", totalFee);
        params.put("currency", currency);
        params.put("exchange", exchange);
        params.put("nonceStr", nonceStr);
        params.put("body", body);
        params.put("notifyUrl", notifyUrl);
        String sign = SignUtil.getSign(params,APPSECRET);
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
     * 商户订单数据校验
     * @param outTradeNo  订单号
     * @param totalFee    价格
     * @param currency     币种
     * @param payAt       支付时间
     * @param result
     * @param timestamp   时间戳
     * @param scode
     * @param sign
     * @return
     */
    public boolean notifyValidate(String outTradeNo,String totalFee, String currency,String payAt,String result, String timestamp, String scode,String sign){
        LoggerUtil.info("[notifyValidate] outTradeNo:{"+outTradeNo+"},appUid:{"+APPUID+"},result{"+result+"},totalFee:{"+totalFee+"},payAt:{"+payAt+"},scode:{"+scode+"},sign:{"+sign+"}");
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
        if(StringUtils.isEmpty(currency)) {
            LoggerUtil.error("必选参数:currency 为空");
            throw new IllegalArgumentException("必选参数:currency 为空");
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
        if (!verifyNotify(APPUID, outTradeNo,result,
                totalFee, currency, payAt,
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
        stringBuffer.append("code:"+code);
        stringBuffer.append(",");
        stringBuffer.append("msg:"+msg);
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
     * @param currency
     * @param payAt
     * @param timestamp
     * @param scode
     * @param sign
     * @param appSecret
     * @return
     */
    public Boolean verifyNotify(String appUid, String outTradeNo,String result,
                                String totalFee, String currency, String payAt,
                                String timestamp, String scode,
                                String sign, String appSecret) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("appUid",appUid);
        map.put("outTradeNo",outTradeNo);
        map.put("totalFee",totalFee);
        map.put("currency",currency);
        map.put("payAt",payAt);
        map.put("result",result);
        map.put("scode",scode);
        map.put("timeStamp",timestamp);
        String computedSign =  SignUtil.getSign(map,appSecret);
        return sign.equals(computedSign);
    }

    private void setHttpHeader(AbstractHttpMessage httpMessage) {
        httpMessage.setHeader("Accept", "application/json");
        httpMessage.setHeader("Content-Type", "application/json;charset=utf-8");
    }
}
