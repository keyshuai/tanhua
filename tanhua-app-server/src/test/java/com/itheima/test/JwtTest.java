package com.itheima.test;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class JwtTest {

    @Test
    public void testCreateToken() {
        //生成token
        //1、准备数据
        //2、使用JWT的工具类生成token
        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "itcast") //指定加密算法
                .setClaims(JSONUtil.createObj().set("id", 1).set("mobile", "18888888888")) //写入数据
                .setExpiration(DateTime.now().offset(DateField.DAY_OF_MONTH, 10)) //失效时间
                .compact();
        System.out.println(token);
    }


    /**
     * 解析token
     * SignatureException : token不合法
     * ExpiredJwtException：token已过期
     */
    @Test
    public void testParseToken() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJtb2JpbGUiOiIxODg4ODg4ODg4OCIsImlkIjoxLCJleHAiOjE2MzcyMjEwOTB9.ficcbssAhGv_B2nMUZ5zLCdyqrHUvbnLbNciQ9Qz36Rdo-B4rdaOhLAnG6aoLVEF2uguGPJbyBkfP4ffNMqDcg";
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("itcast")
                    .parseClaimsJws(token)
                    .getBody();
            Object id = claims.get("id");
            Object mobile = claims.get("mobile");
            System.out.println(id + "--" + mobile);
        } catch (ExpiredJwtException e) {
            System.out.println("token已过期");
        } catch (SignatureException e) {
            System.out.println("token不合法");
        }

    }
}
