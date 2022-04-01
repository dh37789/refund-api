package com.szs.szsrefund.global.security;

import com.szs.szsrefund.global.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String[] IGNORING_PATH = {
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };
    private final String[] PERMIT_PATH = {
            "/szs/signup",
            "/szs/login"
    };
    private final String[] PERMIT_AUTH_PATH = {
            "/szs/**"
    };

    @Bean
    public PasswordEncoder getPassWordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(IGNORING_PATH);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // 기본설정 미사용
                .csrf().disable() // csrf 보안 미사용
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // jwt로 인증하므로 세션 미사용
    }


}
