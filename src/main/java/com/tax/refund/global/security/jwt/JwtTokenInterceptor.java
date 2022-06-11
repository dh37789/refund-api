package com.tax.refund.global.security.jwt;

import com.tax.refund.domain.user.exception.NotFoundUserException;
import com.tax.refund.domain.user.repository.UserRepository;
import com.tax.refund.global.security.jwt.exception.InvalidJwtTokenException;
import com.tax.refund.global.security.jwt.exception.JwtTokenException;
import com.tax.refund.global.security.jwt.exception.JwtTokenExpiredException;
import com.tax.refund.global.security.jwt.exception.NullJwtTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws NullJwtTokenException {
        String token = JwtUtils.resolveToken(request);

        try {
        if (token == null)
            throw new NullJwtTokenException();

            String userId = JwtUtils.getSubject(token);
            userRepository.findByUserId(userId).orElseThrow(NotFoundUserException::new);
        } catch (MalformedJwtException e) {
            throw new InvalidJwtTokenException();
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException();
        } catch (Exception e) {
            throw new JwtTokenException();
        }

        return true;
    }


}
