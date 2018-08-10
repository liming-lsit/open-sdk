package com.sdk.demo;

import com.alibaba.fastjson.JSONObject;
import com.sdk.OpenSDk;


/**
 * describe:
 *
 * @author LM
 */
public class OpenSdkDemo {

    public static void main(String[] args) {
        String result ="ok";
        String accountUid = "32";
        String openUid="85f90d7d831c4a7ebfcfa8a6f9b38f07";
        String accessToken ="";
        String accountToken= "eyJhbGciOiJIUzI1NiJ9.eyJ0ZWxlZ3JhbUlkIjoiMzM0ODAyMDk2In0.aKDrqbmGmKVaADCC08M1NjiIw9lcSwPK40PBaEpKc-Q";
        String orderSn="HVcS3x6xB1vcOxx";
        String totalFee="1";
        String appUid ="34342fsdfsdf";
        String body="测试";
        String notifyUrl="www.jd.com";
        String exchange = "true";
        String currency = "RMB";
        String outTradeNo = "HVcS3x6xB1q";
        String payAt = "2018-07-02 18:12:14";
        String timestamp="2018-07-02 18:12:14";
        String scode ="a20a53cd48bf42499cf5446882374353";
        String code ="c15RR4E8Ft5DMsXS";
        String sign="11";
        //初始化SDK
        OpenSDk openSDk = new OpenSDk();

        //******************************注释*********************************************
        //*serveraddress 初始化服务器地址                                                 *
        //*appUid    第三方应用APP_ID                                                    *
        //*appSecret  密钥                                                               *
        //*******************************************************************************
        openSDk.init("http://127.0.0.1:8086/open-api","34342fsdfsdfxx","6aae383b54f5d3503097991a60575bd7");

        //******************************注释*********************************************
        //*获取token                                                                    *
        //*code      临时登录凭证                                                        *
        //*******************************************************************************
        result = openSDk.QueryUserAccessToken("fpD3V3u2aV9sASOo");
        System.out.println("result:"+result);
        JSONObject jsonObject=JSONObject.parseObject(result);
        String refreshToken="";
        if("0".equals(jsonObject.get("code"))){
            accessToken = jsonObject.getJSONObject("data").getString("accessToken");
            String openId = jsonObject.getJSONObject("data").getString("openId");
            refreshToken = jsonObject.getJSONObject("data").getString("refreshToken");
            String expiresIn = jsonObject.getJSONObject("data").getString("expiresIn");
            System.out.println("code=" + jsonObject.get("code") +" msg= "+jsonObject.get("msg") +" accessToken:"+code);
            //正常返回
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + jsonObject.get("code") +" 错误信息= "+jsonObject.get("msg"));
        }
        //******************************注释*********************************************
        //*获取用户信息接口                                                              *
        //*appUid    第三方应用APP_ID                                                    *
        //*code      临时登录凭证                                                        *
        //*appSecret 密钥                                                                *
        //*******************************************************************************
        result = openSDk.QueryUserInfo(accessToken,openUid);
        System.out.println("result:"+result);

        //******************************注释*********************************************
        //*刷新token                                                                    *
        //*refreshToken                                                                 *
        //*******************************************************************************
        result = openSDk.RefreshAccessToken(refreshToken);
        System.out.println("result:"+result);
        jsonObject=JSONObject.parseObject(result);
        if("0".equals(jsonObject.get("code"))){
            String refresh_token = jsonObject.getJSONObject("data").getString("refresh_token");
            System.out.println("code=" + jsonObject.get("code") +" msg= "+jsonObject.get("msg") +" code:"+code);
            //正常返回
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + jsonObject.get("code") +" 错误信息= "+jsonObject.get("msg"));
        }

        //******************************注释*********************************************
        //*获取APP Token                                                              *
        //*******************************************************************************
        result =openSDk.QueryAppAccessToken();
        System.out.println("result:"+result);
        jsonObject=JSONObject.parseObject(result);
        if("0".equals(jsonObject.get("code"))){
            accessToken = jsonObject.getJSONObject("data").getString("accessToken");
            System.out.println("code=" + jsonObject.get("code") +" msg= "+jsonObject.get("msg") +" accessToken:"+accessToken);
            //正常返回
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + jsonObject.get("code") +" 错误信息= "+jsonObject.get("msg"));
        }
        //******************************注释*********************************************
        //*创建订单                                                                      *
        //*orderSn    订单号                                                             *
        //*totalFee   价格                                                               *
        //*body       描述                                                               *
        //*notifyUrl  推送回掉地址                                                        *
        //*appSecret  密钥  9                                                              *
        //*******************************************************************************
        result = openSDk.createOrder(openUid,accessToken,orderSn,totalFee,exchange,body,notifyUrl,currency);
        System.out.println("result:"+result);

        //******************************注释*********************************************
        //*商户订单数据校验                                                               *
        //*outTradeNo    订单号                                                          *
        //*totalFee      价格                                                            *
        //*currency      币种                                                            *
        //*payAt         支付时间                                                        *
        //*result                                                                        *
        //*timestamp     时间戳                                                          *
        //*scode                                                                         *
        //*sign          签名                                                            *
        //*******************************************************************************
        boolean flag = openSDk.notifyValidate(outTradeNo,totalFee,currency,payAt,result,timestamp, scode, sign);
        System.out.println("flag:"+flag);
    }
}
