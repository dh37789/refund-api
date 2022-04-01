package com.szs.szsrefund.domain.user.service;

import com.szs.szsrefund.domain.user.dto.UserLoginDto;
import com.szs.szsrefund.domain.user.dto.UserSignDto;
import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.domain.user.exception.*;
import com.szs.szsrefund.domain.user.repository.UserRepository;
import com.szs.szsrefund.global.security.jwt.JwtTokenProvider;
import com.szs.szsrefund.global.security.jwt.TokenDto;
import com.szs.szsrefund.global.utill.CrytptoUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final static String REG_NO_REGEX = "\\d{6}\\-[1-4]\\d{6}";

    public UserService (UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserSignDto.Response signUp(UserSignDto.Request requestDto) throws Exception {
        if (isExistsUser(requestDto))
            throw new AlreadyExistsUserException(requestDto.getName());

        if (isExistsUserId(requestDto))
            throw new AlreadyExistsUserIdException(requestDto.getUserId());

        if (!User.isMatchedRegNo(REG_NO_REGEX, requestDto.getRegNo()))
            throw new NotMatchedRegNoException();

        requestDto.encodeRegNo(requestDto.getRegNo());
        requestDto.encodePassword(passwordEncoder.encode(requestDto.getPassword()));

        User user = userRepository.save(requestDto.toEntity());

        return UserSignDto.Response.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

    private boolean isExistsUser(UserSignDto.Request requestDto) throws Exception {
        return userRepository.findByRegNo(CrytptoUtil.encrypt(requestDto.getRegNo())).isPresent();
    }

    private boolean isExistsUserId(UserSignDto.Request requestDto) {
        return userRepository.findByUserId(requestDto.getUserId()).isPresent();
    }

    public UserLoginDto.Response login(UserLoginDto.Request requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId()).orElseThrow(NotFoundUserException::new);

        if(!User.isMatchedPassowrd(passwordEncoder, requestDto.getPassword(), user.getPassword()))
            throw new NotMatchedPasswordException();

        String accessToken = jwtTokenProvider.issueToken(user.getUserId(), user.getName());

        return UserLoginDto.Response.builder()
                .userId(user.getUserId())
                .token(TokenDto.of(accessToken))
                .build();
    }
}
