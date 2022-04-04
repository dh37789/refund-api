package com.szs.szsrefund.domain.user.service;

import com.szs.szsrefund.domain.user.dto.UserInfoDto;
import com.szs.szsrefund.domain.user.dto.UserLoginDto;
import com.szs.szsrefund.domain.user.dto.UserSignDto;
import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.domain.user.exception.*;
import com.szs.szsrefund.domain.user.repository.UserRepository;
import com.szs.szsrefund.global.config.common.Constants;
import com.szs.szsrefund.global.config.redis.RedisService;
import com.szs.szsrefund.global.security.jwt.JwtTokenProvider;
import com.szs.szsrefund.global.security.jwt.TokenDto;
import com.szs.szsrefund.global.utill.CrytptoUtils;
import com.szs.szsrefund.global.utill.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.szs.szsrefund.global.config.common.Constants.USERINFO_HEAD_KEY;
import static com.szs.szsrefund.global.config.common.Constants.NAME_KEY;
import static com.szs.szsrefund.global.config.common.Constants.REG_NO_KEY;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public UserService (UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    /**
     * 유저 정보를 저장합니다.
     * @param requestDto
     * @return
     * @throws Exception
     */
    @Transactional
    public UserSignDto.Response signUp(UserSignDto.Request requestDto) throws Exception {
        if (isExistsUser(requestDto))
            throw new AlreadyExistsUserException(requestDto.getName());

        if (isExistsUserId(requestDto))
            throw new AlreadyExistsUserIdException(requestDto.getUserId());

        if (!User.isMatchedRegNo(Constants.REG_NO_REGEX, requestDto.getRegNo()))
            throw new NotMatchedRegNoException();

        /* AES/CBC/PKCS5Padding 방식으로 암호화 */
        requestDto.encryptRegNo(requestDto.getRegNo());
        /* 비밀번호 SHA로 해시 암호화 */
        requestDto.encodePassword(passwordEncoder.encode(requestDto.getPassword()));

        User user = userRepository.save(requestDto.toEntity());

        return UserSignDto.Response.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

    /**
     * 유저정보가 존재유무를 확인합니다.
     * @param requestDto
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public boolean isExistsUser(UserSignDto.Request requestDto) throws Exception {
        return userRepository.findByRegNo(CrytptoUtils.encrypt(requestDto.getRegNo())).isPresent();
    }

    /**
     * 중복되는 userId인지 확인합니다.
     * @param requestDto
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public boolean isExistsUserId(UserSignDto.Request requestDto) {
        return userRepository.findByUserId(requestDto.getUserId()).isPresent();
    }

    /**
     * 유저 로그인
     * @param requestDto
     * @return
     */
    @Transactional(readOnly = true)
    public UserLoginDto.Response login(UserLoginDto.Request requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId()).orElseThrow(NotFoundUserException::new);

        if(!User.isMatchedPassowrd(passwordEncoder, requestDto.getPassword(), user.getPassword()))
            throw new NotMatchedPasswordException();

        /* 로그인정보로 jwt 토큰 발급 */
        String accessToken = jwtTokenProvider.issueToken(user.getUserId(), user.getName());

        return UserLoginDto.Response.builder()
                .userId(user.getUserId())
                .token(TokenDto.of(accessToken))
                .build();
    }

    /**
     * 토큰정보를 이용하여 내정보를 조회합니다.
     * @param userId
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public UserInfoDto.Response me(String userId) throws Exception {
        /* redis에 데이터가 존재할시 redis에서 조회 */
        if (isExistsUserFormRedis(userId)) {
            String name = redisService.getValues("userInfo:"+userId+":id");
            String regNo = redisService.getValues("userInfo:"+userId+":id");
            return UserInfoDto.Response.builder()
                    .userId(userId)
                    .name(name)
                    .regNo(StringUtils.maskingRegNo(CrytptoUtils.decrypt(regNo)))
                    .build();
        }

        User user = userRepository.findByUserId(userId).orElseThrow(NotFoundUserException::new);

        /* 내정보 redis에 조회 */
        saveUserToRedis(user);

        return UserInfoDto.Response.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .regNo(StringUtils.maskingRegNo(CrytptoUtils.decrypt(user.getRegNo())))
                .build();
    }

    /**
     * redis에 데이터 유무 확인
     * @param userId
     * @return
     */
    private boolean isExistsUserFormRedis(String userId) {
        return redisService.getValues("userInfo:"+userId+"name") != null;
    }

    /**
     * redis에 user정보 저장
     * @param user
     */
    private void saveUserToRedis(User user) throws Exception {
        Map<String, String> redisMap = new HashMap<>();
        redisMap.put(USERINFO_HEAD_KEY+user.getUserId()+NAME_KEY, user.getName());
        redisMap.put(USERINFO_HEAD_KEY+user.getUserId()+REG_NO_KEY, CrytptoUtils.encrypt(user.getRegNo()));
        redisService.setMultiValues(redisMap);
    }
}
