package com.sdk.demo;

import com.alibaba.fastjson.JSONObject;
import com.sdk.OpenSDk;

import java.math.BigDecimal;


/**
 * describe:
 *
 * @author xxx
 * @date 2018/7/11
 */
public class OpenSdkDemo {

    public static void main(String[] args) {
        String result ="";
        String appUid ="34342fsdfsdf";
        String accountUid = "171";
        String accountToken= "eyJhbGciOiJIUzI1NiJ9.eyJ0ZWxlZ3JhbUlkIjoiMzM0ODAyMDk2In0.aKDrqbmGmKVaADCC08M1NjiIw9lcSwPK40PBaEpKc-Q";
        String appSecret = "6aae383b54f5d3503097991a60575bd7";
        String openUid="7e92616c0d0d4c1987e38bb80a6258c0";
        String orderSn="HVcS3x6xB1qf0vcOxx";
        BigDecimal totalFee=new BigDecimal(0);
        String body="测试";
        String notifyUrl="www.jd.com";
        String exchange = "true";
        String feeType = "RMB";


        //初始化SDK
        OpenSDk openSDk = new OpenSDk();

        //******************************注释*********************************************
        //*初始化服务器地址                                                              *
        //*生产环境（用户应用上线使用）：openSDk.init("http://127.0.0.1:8086");           *
        //*******************************************************************************
        openSDk.init("http://127.0.0.1:8086");
        //******************************注释*********************************************
        //*获取用户登陆凭证                                                              *
        //*appUid       第三方应用APP_ID                                                 *
        //*accountUid   App账号ID                                                        *
        //*accountToken App账号当前Token                                                 *
        //*******************************************************************************
        result = openSDk.queryCode(appUid,accountUid,accountToken);
        System.out.println("result:"+result);
        JSONObject jsonObject=JSONObject.parseObject(result);
        if("0".equals(jsonObject.get("statusCode"))){
            String code = jsonObject.getJSONObject("data").getString("code");
            System.out.println("statusCode=" + jsonObject.get("statusCode") +" statusMsg= "+jsonObject.get("statusMsg") +"code:"+code);
            //正常返回
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + jsonObject.get("statusCode") +" 错误信息= "+jsonObject.get("statusMsg"));
        }
        //******************************注释*********************************************
        //*获取用户信息接口                                                              *
        //*appUid    第三方应用APP_ID                                                    *
        //*code      临时登录凭证                                                        *
        //*appSecret 密钥                                                                *
        //*******************************************************************************
        result = openSDk.QueryOpenid(appUid,jsonObject.getJSONObject("data").getString("code"),appSecret);
        System.out.println("result:"+result);

        //******************************注释*********************************************
        //*创建订单                                                                      *
        //*appUid    第三方应用APP_ID                                                    *
        //*openUid    用户OpenId                                                         *
        //*orderSn    订单号                                                             *
        //*totalFee   价格                                                               *
        //*body       描述                                                               *
        //*notifyUrl  推送回掉地址                                                        *
        //*appSecret  密钥                                                                *
        //*******************************************************************************
        result = openSDk.createOrder(appUid,appSecret,openUid,orderSn,totalFee,exchange,body,notifyUrl,feeType);
        System.out.println("result:"+result);
    }
}
