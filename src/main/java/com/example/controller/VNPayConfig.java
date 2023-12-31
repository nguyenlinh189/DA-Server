package com.example.controller;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;


// muốn đăng kí tài khoản vnpay sandbox thì p đổi thành miền ảo
// vào sửa trong đường dẫn sau C:\Windows\System32\drivers\etc\hosts
public class VNPayConfig {
	// là đường đẫn tới trang thanh toán của vnpay
	public static String vnp_PayUrl="https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
	// đường dẫn sau khi thực hiện thanh toán xong(trang thanh toán thành công)
	//public static String vnp_ReturnUrl="https://da-server2-production.up.railway.app/usedService/vnpay_return";
	public static String vnp_ReturnUrl="http://localhost:8081/usedService/vnpay_return";
	
//	public static String vnp_ReturnUrl="https://e5cb-2405-4802-4e19-fad0-f1a9-cf9b-72e1-4c8f.ngrok-free.app/usedService/vnpay_return";	
	//mã website của merchant trên hệ thống của vnpay
	public static String vnp_TmnCode="8O1NHS8R";
	//mã khóa bí mật
	public static String secretKey="XSMBWIAREXTSBGKKJABLGQOFUYSCIKYL";
	public static String vnp_ApiUrl="https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
	
	public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
	public static String hashAllFields(Map fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(secretKey,sb.toString());
    }
	public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }
	// lay random trong chuoi chars
	public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
