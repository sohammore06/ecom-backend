package com.demo.ecommerce_backend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SmileOneUtil {
    public static String generateSignature(Map<String, String> params, String key) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (String k : keys) {
            sb.append(k).append("=").append(params.get(k)).append("&");
        }
        sb.append(key);

        return md5(md5(sb.toString()));
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 error", e);
        }
    }
}
//    <?php
//// Online PHP compiler to run PHP program online
//// Print "Try programiz.pro" message
//    $body['time'] = time();
//    $body['email'] ='ajaybhavsar88@gmail.com';
//    $body['uid'] = '1341228';
//    $body['product']='mobilelegends';
//    $m_key = '655e4bc07fcece4f3328f033763a2f3b';
//
//    ksort($body);
//
//    $str = '';
//    foreach ($body as $k => $v) {
//        $str.=$k.'='.$v.'&';
//    }
//
//    $str = $str.$m_key;
//
//    $sign=md5(md5($str));
//    echo $sign.'time-->'.$body['time'];
//
//?>