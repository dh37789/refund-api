package com.tax.refund.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tax.refund.domain.user.api.UserController;
import com.tax.refund.domain.user.dto.UserInfoDto;
import com.tax.refund.domain.user.dto.UserLoginDto;
import com.tax.refund.domain.user.dto.UserSignDto;
import com.tax.refund.domain.user.entity.User;
import com.tax.refund.domain.user.repository.UserRepository;
import com.tax.refund.domain.user.service.UserService;
import com.tax.refund.global.config.common.service.ResponseService;
import com.tax.refund.global.error.user.UserExceptionHandler;
import com.tax.refund.global.security.jwt.JwtTokenInterceptor;
import com.tax.refund.global.security.jwt.JwtUtils;
import com.tax.refund.global.utill.CrytptoUtils;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("dev")
public class UserControllerTest {

    @MockBean
    private UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @MockBean
    private ResponseService responseService;

    @MockBean
    private JwtTokenInterceptor jwtTokenInterceptor;

    @MockBean
    private JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new UserExceptionHandler(responseService))
                .build();

        Key key = Keys.hmacShaKeyFor("aabbccddeeffgghhiijjkkll11223344556677889900".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(JwtUtils.class, "key", key);
    }

    private final String TEST_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYmNkMTIzIiwiaWF0IjoxNjQ4ODg0MzcxLCJleHAiOjE2NTA2ODQzNzEsIm5hbWUiOiLtmY3quLjrj5kifQ.JxeVIigpxMynxpx-9OHQp3ZXpJV-sKWTMg6D_AmeP6Y";



    @Test
    @Order(1)
    @DisplayName("회원가입 성공 테스트")
    public void signUp_success_테스트() throws Exception {
        // given
        final UserSignDto.Request dto = buildSignUpDtoRequest();
        given(userService.signUp(dto)).willReturn(new UserSignDto.Response());

        // when
        ResultActions resultActions = requestSignUp(dto);

        // then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    @DisplayName("회원가입 실패 테스트_userId 미입력")
    public void signUp_fail_empty_userId_테스트() throws Exception {
        // given
        final UserSignDto.Request dto = UserSignDto.Request.builder()
                        .userId("")
                        .name("홍길동")
                        .password("1234")
                        .regNo("860824-1655068")
                        .build();

        given(userService.signUp(any())).willThrow(MalformedParameterizedTypeException.class);

        // when
        ResultActions resultActions = requestSignUp(dto);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("회원가입 실패 테스트_name 미입력")
    public void signUp_fail_empty_name_테스트() throws Exception {
        // given
        final UserSignDto.Request dto = UserSignDto.Request.builder()
                .userId("abc1234")
                .name("")
                .password("1234")
                .regNo("860824-1655068")
                .build();

        given(userService.signUp(any())).willThrow(MalformedParameterizedTypeException.class);

        // when
        ResultActions resultActions = requestSignUp(dto);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @DisplayName("회원가입 실패 테스트_password 미입력")
    public void signUp_fail_empty_password_테스트() throws Exception {
        // given
        final UserSignDto.Request dto = UserSignDto.Request.builder()
                .userId("abc1234")
                .name("홍길동")
                .password("")
                .regNo("860824-1655068")
                .build();

        given(userService.signUp(any())).willThrow(MalformedParameterizedTypeException.class);

        // when
        ResultActions resultActions = requestSignUp(dto);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("회원가입 실패 테스트_regNo 미입력")
    public void signUp_fail_empty_regNo_테스트() throws Exception {
        // given
        final UserSignDto.Request dto = UserSignDto.Request.builder()
                .userId("abc1234")
                .name("홍길동")
                .password("1234")
                .regNo("")
                .build();

        given(userService.signUp(any())).willThrow(MalformedParameterizedTypeException.class);

        // when
        ResultActions resultActions = requestSignUp(dto);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @DisplayName("로그인 성공 테스트")
    public void login_success_테스트() throws Exception {
        // given
        final UserLoginDto.Request dto = buildLoginDtoRequest();
        given(userService.login(dto)).willReturn(new UserLoginDto.Response());

        // when
        ResultActions resultActions = requestLogin(dto);

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @DisplayName("로그인 실패 테스트 userId 미입력")
    public void login_fail_empty_userId_테스트() throws Exception {
        // given
        final UserLoginDto.Request dto = UserLoginDto.Request.builder()
                .userId("")
                .password("1234")
                .build();
        given(userService.login(dto)).willThrow(MalformedParameterizedTypeException.class);

        // when
        ResultActions resultActions = requestLogin(dto);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    @DisplayName("로그인 실패 테스트 password 미입력")
    public void login_fail_empty_password_테스트() throws Exception {
        // given
        final UserLoginDto.Request dto = UserLoginDto.Request.builder()
                .userId("abcd123")
                .password("")
                .build();
        given(userService.login(dto)).willThrow(MalformedParameterizedTypeException.class);

        // when
        ResultActions resultActions = requestLogin(dto);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("내정보 조회 성공 테스트")
    public void me_fail_empty_token_테스트2() throws Exception {
        // given

        // when
        ResultActions resultActions = requestMe(TEST_TOKEN);

        // then
        resultActions.andExpect(status().isOk());
    }

    private ResultActions requestSignUp(UserSignDto.Request dto) throws Exception {
        return mockMvc.perform(post("/tax/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)));
    }

    private ResultActions requestLogin(UserLoginDto.Request dto) throws Exception {
        return mockMvc.perform(post("/tax/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

    }

    private ResultActions requestMe(String token) throws Exception {
        return mockMvc.perform(get("/tax/me")
                .header(HttpHeaders.AUTHORIZATION, token));
    }


    private User buildUserResponse() throws Exception {
        return User.builder()
                .userId("123")
                .password("1234")
                .name("홍길동")
                .regNo(CrytptoUtils.encrypt("860824-1655068"))
                .build();
    }

    private void assertThatEqual(UserSignDto.Request dto, UserSignDto.Response user) {
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getUserId()).isEqualTo(dto.getUserId());
    }

    private void assertThatEqualInfo(User user, UserInfoDto.Response dto) {
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
