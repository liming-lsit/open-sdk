package com.sdk;


import com.alibaba.fastjson.JSONObject;
import com.sdk.utils.HttpClient;
import com.sdk.utils.LoggerUtil;
import com.sdk.utils.SignUtil;
import org.apache.commons.lang.RandomStringUtils;
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
    private static final String QueryAccessToken= "/appUsers/access_token";
    private static final String RefreshAccessToken= "/appUsers/refresh_token";
    private static final String QueryCode = "/appUsers/code";
    private static final String QueryUserInfo = "/appUsers/user_info";
    private static final String QueryAppToken = "/app/token";
    private static String APPUID;
    private static String APPSECRET;

    /**
     * 初始化服务地址和端口
     * @param serveraddress  服务器地址
     * @param appUid         APPID
     * @param appSecret      SECRET
     */
    public void init(String serveraddress,String appUid,String appSecret) {
        if (isEmpty(serveraddress)) {
            LoggerUtil.error("初始化异常:serveraddress");
            throw new IllegalArgumentException("必选参数:serveraddress为空");
        }
        if (isEmpty(appSecret)) {
            LoggerUtil.error("初始化异常:appSecret");
            throw new IllegalArgumentException("必选参数:appSecret 为空");
        }
        SERVER_ADDRESS = serveraddress;
        APPUID = appUid;
        APPSECRET = appSecret;
    }



    /**
     * 获取用户信息
     * @param accessToken accessToken
     * @param openId openId
     * @return String
     */
    public String QueryUserInfo(String accessToken,String openId){
        LoggerUtil.info("[QueryUserInfo] openId:{"+openId+"},accessToken:{"+accessToken+"}");
        String ret = "";
        if(isEmpty(openId)) {
            LoggerUtil.error("必选参数:openId 为空");
            throw new IllegalArgumentException("必选参数:openId 为空");
        }
        if(isEmpty(accessToken)) {
            LoggerUtil.error("必选参数:accessToken 为空");
            throw new IllegalArgumentException("必选参数:accessToken 为空");
        }
        Map<String,String> params = queryParams(accessToken,openId);
        String jsonParam= JSONObject.toJSONString(params);
        try{
            ret = HttpClient.postDataJson(SERVER_ADDRESS+QueryUserInfo,jsonParam);
        }catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
            return getMyError("101111", "请求异常");
        }
        return ret;
    }

    /**
     * QueryUserAccessToken
     * @param code      临时登录凭证
     * @return String
     */
    public String QueryUserAccessToken(String code){
        LoggerUtil.info("[QueryUserAccessToken] code:{"+code+"},appUid:{"+APPUID+"}");
        String ret = "";
        if(isEmpty(code)) {
            LoggerUtil.error("必选参数:code 为空");
            throw new IllegalArgumentException("必选参数:code 为空");
        }
        Map<String,String> params = queryParams(code);
        String jsonParam= JSONObject.toJSONString(params);
        try{
            ret = HttpClient.postDataJson(SERVER_ADDRESS+QueryAccessToken,jsonParam);
        }catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
            return getMyError("101111", "请求异常");
        }
        return ret;
    }


    /**
     * 获取App Token
     * @return String
     */
    public String QueryAppAccessToken(){
        LoggerUtil.info("[QueryAppAccessToken] appUid:{"+APPUID+"}");
        String ret = "";
        if(isEmpty(APPUID)) {
            LoggerUtil.error("必选参数:APPUID 为空");
            throw new IllegalArgumentException("必选参数:APPUID 为空");
        }
        Map<String, String> params = new HashMap<String, String>();
        String nonceStr = RandomStringUtils.randomAlphanumeric(8);
        long timeStamp = System.currentTimeMillis();
        params.put("appUid", APPUID);
        params.put("timestamp", String.valueOf(timeStamp));
        params.put("nonceStr", nonceStr);
        String computed_sign =  SignUtil.getSign(params,APPSECRET);
        params.put("sign", computed_sign);
        String jsonParam= JSONObject.toJSONString(params);
        try{
            ret = HttpClient.postDataJson(SERVER_ADDRESS+QueryAppToken,jsonParam);
        }catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
            return getMyError("101111", "请求异常");
        }
        return ret;
    }



    /**
     * 刷新token
     * @param  refreshToken 刷新token
     * @return String
     */
    public String RefreshAccessToken(String refreshToken){
        LoggerUtil.info("[RefreshAccessToken] appUid:{"+APPUID+"},refreshToken:{"+refreshToken+"}");
        String ret = "";
        if(isEmpty(APPUID)) {
            LoggerUtil.error("必选参数:APPUID 为空");
            throw new IllegalArgumentException("必选参数:APPUID 为空");
        }
        if(isEmpty(refreshToken)) {
            LoggerUtil.error("必选参数:refreshToken 为空");
            throw new IllegalArgumentException("必选参数:refreshToken 为空");
        }
        Map<String, String> params = new HashMap<String, String>();
        String nonceStr = RandomStringUtils.randomAlphanumeric(8);
        long timeStamp = System.currentTimeMillis();
        params.put("appId", APPUID);
        params.put("refreshToken", refreshToken);
        String jsonParam= JSONObject.toJSONString(params);
        try{
            ret = HttpClient.postDataJson(SERVER_ADDRESS+RefreshAccessToken,jsonParam);
        }catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
            return getMyError("101111", "请求异常");
        }
        return ret;
    }





    /**
     * 拼接参数
     * @param code code
     * @return Map
     */
    public Map<String,String> queryParams(String code){
        Map<String, String> params = new HashMap<String, String>();
        String nonceStr = RandomStringUtils.randomAlphanumeric(8);
        long timeStamp = System.currentTimeMillis();
        params.put("appUid", APPUID);
        params.put("code", code);
        params.put("timestamp", String.valueOf(timeStamp));
        params.put("nonceStr", nonceStr);
        String computed_sign =  SignUtil.getSign(params,APPSECRET);
        params.put("sign", computed_sign);
        return params;
    }

    /**
     * 拼接参数
     * @param openId 用户OpenId
     * @param accessToken accessToken
     * @return Map
     */
    public Map<String,String> queryParams(String accessToken,String openId){
        Map<String, String> params = new HashMap<String, String>();
        params.put("accessToken", accessToken);
        params.put("openId", openId);
        return params;
    }





    /**
     * 创建订单
     * @param openUid    用户OpenId
     * @param accessToken accessToken
     * @param orderSn    订单号
     * @param totalFee   价格
     * @param body       描述
     * @param exchange   是否支持兑换
     * @param notifyUrl  推送回掉地址
     * @param currency   币种
     * @return String
     */
    public String createOrder(String openUid,String accessToken,
                              String orderSn, String totalFee,String exchange,
                              String body,String notifyUrl,String currency){
        LoggerUtil.info("[createOrder] accessToken:{"+accessToken+"} openUid:{"+openUid+"},appUid:{"+APPUID+"},orderSn{"+orderSn+"},totalFee:{"+totalFee+"},exchange:{"+exchange+"},body:{"+body+"},notifyUrl:{"+notifyUrl+"}");
        Map<String, String> params = new HashMap<String, String>();
        params.put("appUid", APPUID);
        params.put("openUid", openUid);
        params.put("outTradeNo", orderSn);
        long timeStamp = System.currentTimeMillis();
        params.put("timestamp", String.valueOf(timeStamp));
        String nonceStr = RandomStringUtils.randomAlphanumeric(8);
        params.put("totalFee", totalFee);
        params.put("currency", currency);
        params.put("exchange", exchange);
        params.put("nonceStr", nonceStr);
        params.put("accessToken", accessToken);
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
     * @param result       result
     * @param timestamp   时间戳
     * @param scode       支付订单号
     * @param sign        签名
     * @return boolean
     */
    public boolean notifyValidate(String outTradeNo,String totalFee, String currency,String payAt,String result, String timestamp, String scode,String sign){
        LoggerUtil.info("[notifyValidate] outTradeNo:{"+outTradeNo+"},appUid:{"+APPUID+"},result{"+result+"},totalFee:{"+totalFee+"},payAt:{"+payAt+"},scode:{"+scode+"},sign:{"+sign+"}");
        boolean flag = true;
        if(isEmpty(APPUID)) {
            LoggerUtil.error("必选参数:appUid 为空");
            throw new IllegalArgumentException("必选参数:appUid 为空");
        }
        if(isEmpty(outTradeNo)) {
            LoggerUtil.error("必选参数:outTradeNo 为空");
            throw new IllegalArgumentException("必选参数:outTradeNo 为空");
        }
        if(isEmpty(totalFee)) {
            LoggerUtil.error("必选参数:totalFee 为空");
            throw new IllegalArgumentException("必选参数:totalFee 为空");
        }
        if(isEmpty(currency)) {
            LoggerUtil.error("必选参数:currency 为空");
            throw new IllegalArgumentException("必选参数:currency 为空");
        }
        if(isEmpty(payAt)) {
            LoggerUtil.error("必选参数:payAt 为空");
            throw new IllegalArgumentException("必选参数:payAt 为空");
        }
        if(isEmpty(result)) {
            LoggerUtil.error("必选参数:result 为空");
            throw new IllegalArgumentException("必选参数:result 为空");
        }
        if(isEmpty(timestamp)) {
            LoggerUtil.error("必选参数:timestamp 为空");
            throw new IllegalArgumentException("必选参数:timestamp 为空");
        }
        if(isEmpty(scode)) {
            LoggerUtil.error("必选参数:scode 为空");
            throw new IllegalArgumentException("必选参数:scode 为空");
        }
        if(isEmpty(sign)) {
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
     * @param appUid      AppId
     * @param outTradeNo  订单号
     * @param result      result
     * @param totalFee    价格
     * @param currency    币种
     * @param payAt       支付时间
     * @param timestamp   时间戳
     * @param scode       支付订单号
     * @param sign        签名
     * @param appSecret   密钥
     * @return Boolean
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
        map.put("timestamp",timestamp);
        String computedSign =  SignUtil.getSign(map,appSecret);
        return sign.equals(computedSign);
    }

    private void setHttpHeader(AbstractHttpMessage httpMessage) {
        httpMessage.setHeader("Accept", "application/json");
        httpMessage.setHeader("Content-Type", "application/json;charset=utf-8");
    }

    private boolean isEmpty(String str) {
        return (("".equals(str)) || (str == null));
    }
}
