//package com.sky.service.impl;
//
//import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class NotifyService {
//    @Autowired
//    private AesUtil aesUtil;
//
//    public String handleNotify(String json) throws Exception {
//
//        String s = aesUtil.decryptToString(
//                "transaction".getBytes(),
//                "nonce".getBytes(),
//                "cipher"
//        );
//        return s;
//    }
//}
