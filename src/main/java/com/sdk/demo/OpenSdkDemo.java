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
        String result ="ok";
        String accountUid = "171";
        String openUid="7e92616c0d0d4c1987e38bb80a6258c0";
        String accountToken= "eyJhbGciOiJIUzI1NiJ9.eyJ0ZWxlZ3JhbUlkIjoiMzM0ODAyMDk2In0.aKDrqbmGmKVaADCC08M1NjiIw9lcSwPK40PBaEpKc-Q";
        String orderSn="HVcS3x6xB1qf0vcOxx";
        String totalFee="1";
        String appUid ="34342fsdfsdf";
        String body="测试";
        String notifyUrl="www.jd.com";
        String exchange = "true";
        String feeType = "RMB";
        String outTradeNo = "HVcS3x6xB1qf0vcOxx";
        String payAt = "2018-07-02 18:12:14";
        String timestamp="2018-07-02 18:12:14";
        String scode ="a20a53cd48bf42499cf5446882374353";
        String code ="c15RR4E8Ft5DMsXS";
        String sign="11";
        //初始化SDK
        OpenSDk openSDk = new OpenSDk();

        //******************************注释*********************************************
        //*serveraddress 初始化服务器地址                                                 *
        //*openUid    用户OpenId                                                         *
        //*appUid    第三方应用APP_ID                                                    *
        //*appSecret  密钥                                                               *
        //*******************************************************************************
        openSDk.init("http://127.0.0.1:8086","34342fsdfsdf","eyJhbGciOiJIUzI1NiJ9.eyJ0ZWxlZ3JhbUlkIjoiMzM0ODAyMDk2In0.aKDrqbmGmKVaADCC08M1NjiIw9lcSwPK40PBaEpKc-Q","6aae383b54f5d3503097991a60575bd7");

        //******************************注释*********************************************
        //*获取用户登陆凭证                                                              *
        //*appUid       第三方应用APP_ID                                                 *
        //*accountUid   App账号ID                                                        *
        //*accountToken App账号当前Token                                                 *
        //*******************************************************************************
        result = openSDk.queryCode(accountUid);
        System.out.println("result:"+result);
        JSONObject jsonObject=JSONObject.parseObject(result);
        if("0".equals(jsonObject.get("code"))){
            code = jsonObject.getJSONObject("data").getString("code");
            System.out.println("statusCode=" + jsonObject.get("code") +" statusMsg="+jsonObject.get("msg") +"  code:"+code);
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
        result = openSDk.queryOpenid(code);
        System.out.println("result:"+result);

        //******************************注释*********************************************
        //*创建订单                                                                      *
        //*orderSn    订单号                                                             *
        //*totalFee   价格                                                               *
        //*body       描述                                                               *
        //*notifyUrl  推送回掉地址                                                        *
        //*appSecret  密钥  9                                                              *
        //*******************************************************************************
        result = openSDk.createOrder(openUid,orderSn,totalFee,exchange,body,notifyUrl,feeType);
        System.out.println("result:"+result);

        //******************************注释*********************************************
        //*商户订单数据校验                                                               *
        //*outTradeNo    订单号                                                          *
        //*totalFee      价格                                                            *
        //*feeType       币种                                                            *
        //*payAt         支付时间                                                        *
        //*result                                                                        *
        //*timestamp     时间戳                                                          *
        //*scode                                                                         *
        //*sign          签名                                                            *
        //*******************************************************************************
        boolean flag = openSDk.notifyValidate(outTradeNo,totalFee.toString(),feeType,payAt,result,timestamp, scode, sign);
        System.out.println("flag:"+flag);
    }
}
