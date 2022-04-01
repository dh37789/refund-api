package com.szs.szsrefund.global.security.jwt;

import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.domain.user.exception.NotFoundUserException;
import com.szs.szsrefund.domain.user.repository.UserRepository;
import com.szs.szsrefund.global.security.jwt.exception.InvalidJwtTokenException;
import com.szs.szsrefund.global.security.jwt.exception.JwtTokenException;
import com.szs.szsrefund.global.security.jwt.exception.JwtTokenExpiredException;
import com.szs.szsrefund.global.security.jwt.exception.NullJwtTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    public JwtTokenInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = JwtUtils.resolveToken(request);

        if (token.isBlank())
            throw new NullJwtTokenException();
        try {
            String userId = JwtUtils.getSubject(token);
            userRepository.findByUserId(userId).orElseThrow(NotFoundUserException::new);
        } catch (MalformedJwtException e) {
            e.printStackTrace();
            throw new InvalidJwtTokenException();
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            throw new JwtTokenExpiredException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new JwtTokenException();
        }

        return true;
    }


}
