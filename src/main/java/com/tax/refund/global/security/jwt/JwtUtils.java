package com.tax.refund.global.security.jwt;

import com.tax.refund.global.config.common.Constants;
import com.tax.refund.global.security.jwt.exception.JwtTokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static Key key;

    public JwtUtils(
            @Value("${spring.jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * header의 authorization부분의 토큰을 가져온다.
     * @param request
     * @return
     */
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
            return bearerToken.substring(7);
        return null;
    }

    /**
     * subject에 있는 userId를 조회해 온다.
     * @param token
     * @return
     */
    public static String getSubject(String token) {
        Claims claims = getClaims(token);

        if (claims.getExpiration().before(new Date()))
            throw new JwtTokenExpiredException();

        return claims.getSubject();
    }

    /**
     * jwtToken에 있는 Claim을 추출한다.
     * @param token
     * @return
     */
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
