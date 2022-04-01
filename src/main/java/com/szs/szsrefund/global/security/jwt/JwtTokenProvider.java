package com.szs.szsrefund.global.security.jwt;

import com.szs.szsrefund.global.config.common.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    private static String secretKey; /* token의 secretKey */

    private static long accessTokenValidityInMilliseconds; /* access토큰의 만료시간 */

    private static Key key; /* Key객체로 해시한 secreyKey  */

    public JwtTokenProvider(
            @Value("${spring.jwt.secret}") String secretKey,
            @Value("${spring.jwt.access-token-validity-in-seconds}") long accessTokenValidityInMilliseconds) {
        this.secretKey = secretKey;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = secretKey.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public static String issueToken(String userId, String name) {
        return createToken(userId, name);
    }

    private static String createToken(String userId, String name) {
        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaim(userId, name))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static Map<String, Object> createHeader() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
        return headers;
    }

    private static Map<String, Object> createClaim(String userId, String name) {
        Date now = new Date();

        Claims claims = Jwts.claims()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidityInMilliseconds));

        claims.put(Constants.NAME_KEY, name);

        return claims;
    }



}
