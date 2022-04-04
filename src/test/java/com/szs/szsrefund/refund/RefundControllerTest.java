package com.szs.szsrefund.refund;

import com.szs.szsrefund.domain.refund.api.RefundController;
import com.szs.szsrefund.domain.refund.dto.RefundDto;
import com.szs.szsrefund.domain.refund.service.RefundService;
import com.szs.szsrefund.domain.scrap.api.ScrapController;
import com.szs.szsrefund.domain.scrap.service.ScrapService;
import com.szs.szsrefund.global.config.common.service.ResponseService;
import com.szs.szsrefund.global.error.StatusCode;
import com.szs.szsrefund.global.error.scrap.ScrapExcpetionHandler;
import com.szs.szsrefund.global.security.jwt.JwtTokenInterceptor;
import com.szs.szsrefund.global.security.jwt.JwtUtils;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("dev")
public class RefundControllerTest {

    @InjectMocks
    private RefundController refundController;

    @Mock
    private RefundService refundService;

    private MockMvc mockMvc;

    @Mock
    private ResponseService responseService;

    @Mock
    private JwtTokenInterceptor jwtTokenInterceptor;

    @MockBean
    private JwtUtils jwtUtils;

    private final String TEST_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYmNkMTIzIiwiaWF0IjoxNjQ4ODg0MzcxLCJleHAiOjE2NTA2ODQzNzEsIm5hbWUiOiLtmY3quLjrj5kifQ.JxeVIigpxMynxpx-9OHQp3ZXpJV-sKWTMg6D_AmeP6Y";

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(refundController)
                .setControllerAdvice(new ScrapExcpetionHandler(responseService))
                .build();

        Key key = Keys.hmacShaKeyFor("aabbccddeeffgghhiijjkkll11223344556677889900".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(JwtUtils.class, "key", key);
    }

    @Test
    @Order(1)
    @DisplayName("유저 환급액 조회 성공 테스트")
    void user_refund_api_테스트() throws Exception {
        // given
        RefundDto.Response responseDto = createRefundResposeDto();
        given(refundService.refund(any())).willReturn(responseDto);

        // when
        ResultActions resultActions = requestRefund(TEST_TOKEN);

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @DisplayName("잘못된 refund HTTP 요청 실패 테스트")
    void refund_is_method_not_allow_fail_테스트() throws Exception {
        // given
        RefundDto.Response responseDto = createRefundResposeDto();
        given(refundService.refund(any())).willReturn(responseDto);

        // when
        ResultActions resultActions = requestPostRefund(TEST_TOKEN);

        // then
        resultActions.andExpect(status().isMethodNotAllowed());
    }

    private RefundDto.Response createRefundResposeDto() {
        return RefundDto.Response.builder()
                .name("홍길동")
                .limit("68만 4천원")
                .deduction("92만 5천원")
                .refund("68만 4천원")
                .build();
    }

    private ResultActions requestRefund(String token) throws Exception {
        return mockMvc.perform(get("/szs/refund")
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    private ResultActions requestPostRefund(String token) throws Exception {
        return mockMvc.perform(post("/szs/refund")
                .header(HttpHeaders.AUTHORIZATION, token));
    }
}
