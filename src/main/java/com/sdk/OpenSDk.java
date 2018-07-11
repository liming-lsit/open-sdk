package main.java.com.sdk;


import com.alibaba.fastjson.JSONObject;
import com.sdk.utils.HmacSHA256;
import com.sdk.utils.HttpClient;
import main.java.com.sdk.utils.LoggerUtil;
import org.apache.commons.lang.RandomStringUtils;
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
    private static final String QueryCode = "/appUsers/getLoginCode";
    private static final String QueryOpenid = "/appUsers/getOpenid";

    /**
     * 初始化服务地址和端口
     *
     * @param serveraddress
     *            必选参数 服务器地址
     */
    public void init(String serveraddress) {
        if (isEmpty(serveraddress)) {
            LoggerUtil.fatal("初始化异常:serveraddress");
            throw new IllegalArgumentException("必选参数:" + serveraddress +") "+ "为空");
        }
        SERVER_ADDRESS = serveraddress;
    }


    private boolean isEmpty(String str) {
        return (("".equals(str)) || (str == null));
    }


    /**
     * 获取用户登陆凭证
     * @param appUid       第三方应用APP_ID
     * @param accountUid   App账号ID
     * @param accountToken App账号当前Token
     * @return
     */
    public  String  queryCode(String appUid, String accountUid, String accountToken){

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appUid", appUid);
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
    }

    /**
     * 获取用户信息接口
     * @param appUid    第三方应用APP_ID
     * @param code      临时登录凭证
     * @param appSecret AppSecre
     * @return
     */
    public  String QueryOpenid(String appUid, String code,String appSecret){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appUid", appUid);
        params.put("code", code);
        params.put("appSecret", appSecret);
        long timeStamp = System.currentTimeMillis();
        params.put("timeStamp", timeStamp);
        String nonceStr = RandomStringUtils.randomAlphanumeric(8);
        params.put("nonceStr", nonceStr);
        String param = "app_uid=" + appUid + "&code=" + code + "&time_stamp=" + timeStamp + "&nonce_str=" + nonceStr + "&app_secret=" + appSecret;
        String computed_sign = HmacSHA256.sign(param, appSecret);
        params.put("sign", computed_sign);
        String ret = "";
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
     * 创建订单
     * @param appUid     第三方应用APP_ID
     * @param appSecret  密钥
     * @param openUid    用户OpenId
     * @param orderSn    订单号
     * @param totalFee   价格
     * @param body       描述
     * @param notifyUrl  推送回掉地址
     * @return
     */
    public  String createOrder(String appUid, String appSecret, String openUid,
                              String orderSn, BigDecimal totalFee,String exchange,
                              String body,String notifyUrl,String feeType){

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appUid", appUid);
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

        String signString = "app_uid=" + appUid +
                "&open_uid=" + openUid +
                "&out_trade_no=" + orderSn +
                "&time_stamp=" + timeStamp +
                "&total_fee=" + totalFee +
                "&fee_type=" +feeType+
                "&exchange=" +exchange+
                "&nonce_str=" + nonceStr +
                "&app_secret=" + appSecret;
        String sign = HmacSHA256.sign(signString, appSecret);
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



    private void setHttpHeader(AbstractHttpMessage httpMessage) {
        httpMessage.setHeader("Accept", "application/json");
        httpMessage.setHeader("Content-Type", "application/json;charset=utf-8");
    }
}
