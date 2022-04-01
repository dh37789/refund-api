package com.szs.szsrefund.user;

import com.szs.szsrefund.domain.user.dto.UserLoginDto;
import com.szs.szsrefund.domain.user.dto.UserSignDto;
import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.domain.user.exception.*;
import com.szs.szsrefund.domain.user.repository.UserRepository;
import com.szs.szsrefund.domain.user.service.UserService;
import com.szs.szsrefund.global.security.jwt.JwtTokenProvider;
import com.szs.szsrefund.global.utill.CrytptoUtil;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {CrytptoUtil.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    @Test
    @Order(1)
    @DisplayName("존재하는_회원_테스트")
    void alreadyExistsUserException_테스트 () {
        // given
        final UserSignDto.Request dto = buildSignUpDtoRequest();
        given(userRepository.findByRegNo(any())).willReturn(Optional.of(dto.toEntity()));

        // when
        Assertions.assertThrows(AlreadyExistsUserException.class, () ->{
            userService.signUp(dto);
        });
    }

    @Test
    @Order(2)
    @DisplayName("존재하는_회원ID_테스트")
    void alreadyExistsUserIdException_테스트 () {
        // given
        final UserSignDto.Request dto = buildSignUpDtoRequest();
        given(userRepository.findByUserId(any())).willReturn(Optional.of(dto.toEntity()));

        // when
        Assertions.assertThrows(AlreadyExistsUserIdException.class, () -> {
            userService.signUp(dto);
        });
    }

    @Test
    @Order(3)
    @DisplayName("정규식이_맞지_않는_주민등록번호_테스트")
    void notMatchedRegNoException_테스트 () throws Exception {
        // given
        UserSignDto.Request dto = buildSignUpFailRequest();

        // when
        Assertions.assertThrows(NotMatchedRegNoException.class, () -> {
            userService.signUp(dto);
        });
    }

    @Test
    @Order(4)
    @DisplayName("회원정보_저장_테스트")
    void signUp_테스트 () throws Exception {
        // given
        final UserSignDto.Request dto = buildSignUpDtoRequest();
        given(userRepository.save(any(User.class))).willReturn(dto.toEntity());

        // when
        final UserSignDto.Response user = userService.signUp(dto);

        // then
        verify(userRepository, atLeastOnce()).save(any(User.class));
        assertThatEqual(dto, user);
    }

    @Test
    @Order(5)
    @DisplayName("회원정보_미존재_테스트")
    void notFoundUserException_테스트 () {
        // given
        UserLoginDto.Request dto = buildLoginDtoRequest();
        given(userRepository.findByUserId(any())).willThrow(new NotFoundUserException());

        // when
        Assertions.assertThrows(NotFoundUserException.class, () -> {
            userService.login(dto);
        });
    }

    @Test
    @Order(6)
    @DisplayName("일치하지_않는_비밀번호_테스트")
    void notMatchedPasswordException_테스트 () {
        // given
        UserLoginDto.Request dto = buildLoginDtoRequest();
        given(userRepository.findByUserId(any())).willReturn(Optional.of(dto.toEntity()));

        UserLoginDto.Request invalidDto = buildLoginFailRequest();

        // when
        Assertions.assertThrows(NotMatchedPasswordException.class, () -> {
            userService.login(invalidDto);
        });
    }

    private void assertThatEqual(UserSignDto.Request dto, UserSignDto.Response user) {
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getUserId()).isEqualTo(dto.getUserId());
    }

    private UserSignDto.Request buildSignUpDtoRequest() {
        return UserSignDto.Request.builder()
                .userId("abcd123")
                .password("1234")
                .name("홍길동")
                .regNo("860824-1655068")
                .build();
    }

    private UserSignDto.Request buildSignUpFailRequest() {
        return UserSignDto.Request.builder()
                .userId("abcd123")
                .password("1234")
                .name("홍길동")
                .regNo("aaaaaa-1655068")
                .build();
    }

    private UserLoginDto.Request buildLoginDtoRequest() {
        return UserLoginDto.Request.builder()
                .userId("abcd123")
                .password("1234")
                .build();
    }

    private UserLoginDto.Request buildLoginFailRequest() {
        return UserLoginDto.Request.builder()
                .userId("abcd123")
                .password("12345")
                .build();
    }

}
