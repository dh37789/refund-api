package com.szs.szsrefund.scrap;

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
@MockBean(JpaMetamodelMappingContext.class)
@ActiveProfiles("dev")
public class ScrapControllerTest {

    @InjectMocks
    private ScrapController scrapController;

    @Mock
    private ScrapService scrapService;

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
        mockMvc = MockMvcBuilders.standaloneSetup(scrapController)
                .setControllerAdvice(new ScrapExcpetionHandler(responseService))
                .build();

        Key key = Keys.hmacShaKeyFor("aabbccddeeffgghhiijjkkll11223344556677889900".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(JwtUtils.class, "key", key);
    }

    @Test
    @Order(1)
    @DisplayName("유저 스크랩 정보 API 요청 성공 테스트")
    void user_scrap_api_테스트() throws Exception {
        // given
        given(scrapService.saveScrap(any())).willReturn(StatusCode.CALL_API);

        // when
        ResultActions resultActions = requestScrap(TEST_TOKEN);

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @DisplayName("잘못된 scrap HTTP 요청 실패 테스트")
    void scrap_is_method_not_allow_fail_테스트() throws Exception {
        // given
        given(scrapService.saveScrap(any())).willReturn(StatusCode.CALL_API);

        // when
        ResultActions resultActions = requestGetScrap(TEST_TOKEN);

        // then
        resultActions.andExpect(status().isMethodNotAllowed());
    }

    private ResultActions requestScrap(String token) throws Exception {
        return mockMvc.perform(post("/szs/scrap")
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    private ResultActions requestGetScrap(String token) throws Exception {
        return mockMvc.perform(get("/szs/scrap")
                .header(HttpHeaders.AUTHORIZATION, token));
    }

}
