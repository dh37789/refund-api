package com.tax.refund.domain.user.service;

import com.tax.refund.domain.user.dto.UserInfoDto;
import com.tax.refund.domain.user.dto.UserLoginDto;
import com.tax.refund.domain.user.dto.UserSignDto;
import com.tax.refund.domain.user.entity.User;
import com.tax.refund.domain.user.exception.*;
import com.tax.refund.domain.user.repository.UserRepository;
import com.tax.refund.global.config.common.Constants;
import com.tax.refund.global.config.redis.RedisService;
import com.tax.refund.global.security.jwt.JwtTokenProvider;
import com.tax.refund.global.security.jwt.TokenDto;
import com.tax.refund.global.utill.CrytptoUtils;
import com.tax.refund.global.utill.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.tax.refund.global.config.common.Constants.*;

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
        validateUserInfo(requestDto);

        /* AES/CBC/PKCS5Padding 방식으로 암호화 */
        encryptRegNo(requestDto);
        /* 비밀번호 bcrypt로 해시 암호화 */
        encodePassword(requestDto);

        User user = sinUpUser(requestDto);

        return UserSignDto.Response.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

    private User sinUpUser(UserSignDto.Request requestDto) {
        return userRepository.save(requestDto.toEntity());
    }

    public void encodePassword(UserSignDto.Request requestDto) {
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
    }

    public void encryptRegNo(UserSignDto.Request requestDto) throws Exception {
        requestDto.setRegNo(CrytptoUtils.encrypt(requestDto.getRegNo()));
    }

    private void validateUserInfo(UserSignDto.Request requestDto) throws Exception {
        if (isExistsUser(requestDto))
            throw new AlreadyExistsUserException(requestDto.getName());

        if (isExistsUserId(requestDto))
            throw new AlreadyExistsUserIdException(requestDto.getUserId());

        if (!User.isMatchedRegNo(Constants.REG_NO_REGEX, requestDto.getRegNo()))
            throw new NotMatchedRegNoException();
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
        User user = getUserInfo(requestDto);

        return UserLoginDto.Response.builder()
                .userId(user.getUserId())
                .token(TokenDto.of(getAccessToken(user)))
                .build();
    }

    private String getAccessToken(User user) {
        return jwtTokenProvider.issueToken(user.getUserId(), user.getName());
    }

    private User getUserInfo(UserLoginDto.Request requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId()).orElseThrow(NotFoundUserException::new);

        if(!User.isMatchedPassowrd(passwordEncoder, requestDto.getPassword(), user.getPassword()))
            throw new NotMatchedPasswordException();
        return user;
    }

    /**
     * 토큰정보를 이용하여 내정보를 조회합니다.
     * @param userId
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public UserInfoDto.Response me(String userId) throws Exception {
        return isExistsUserFormRedis(userId) ? getUserInfoFromRedis(userId) : getUserInfo(userId);
    }

    private UserInfoDto.Response getUserInfo(String userId) throws Exception {
        User user = userRepository.findByUserId(userId).orElseThrow(NotFoundUserException::new);

        /* 내정보 redis에 save */
        saveUserToRedis(user);

        return UserInfoDto.Response.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .regNo(StringUtils.maskingRegNo(CrytptoUtils.decrypt(user.getRegNo())))
                .build();
    }

    private UserInfoDto.Response getUserInfoFromRedis(String userId) throws Exception {
        String name = redisService.getValues("userInfo:"+ userId +":id");
        String regNo = redisService.getValues("userInfo:"+ userId +":id");
        return UserInfoDto.Response.builder()
                .userId(userId)
                .name(name)
                .regNo(StringUtils.maskingRegNo(CrytptoUtils.decrypt(regNo)))
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
