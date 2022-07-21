package com.halead.sso;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * @title: TestJWT
 * @Author ppjjss
 * @Date: 2022/6/29 18:34
 * @Version 1.0
 */
public class TestJWT {
    public static void main(String[] args) {
        String secret="ppjjss";

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("phone","13646738476");
        claims.put("id","2");

        //生成tonken
        String jwt = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, secret).compact();
        System.out.println(jwt);
        //通过token解析数据
        Map<String,Object> body = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
        System.out.println(body);
    }


}
