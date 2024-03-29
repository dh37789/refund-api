package com.tax.refund.global.security.jwt;

import com.tax.refund.global.config.common.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
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

    /**
     * secret키를 byte 형식으로 변환하여 해싱
     */
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = secretKey.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰 발급
     * @param userId
     * @param name
     * @return
     */
    public static String issueToken(String userId, String name) {
        return createToken(userId, name);
    }

    /**
     * jwt토큰 생성
     * @param userId
     * @param name
     * @return
     */
    private static String createToken(String userId, String name) {
        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaim(userId, name))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * jwtoken의 header생성
     * @return
     */
    private static Map<String, Object> createHeader() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
        return headers;
    }

    /**
     * jwtToken의 claim생성
     * @param userId
     * @param name
     * @return
     */
    private static Map<String, Object> createClaim(String userId, String name) {
        Date now = new Date();

        Claims claims = getClaims(userId, now);

        claims.put(Constants.NAME_KEY, name);

        return claims;
    }

    private static Claims getClaims(String userId, Date now) {
        return Jwts.claims()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidityInMilliseconds));
    }


}
