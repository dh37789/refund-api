package com.szs.szsrefund.refund;

import com.szs.szsrefund.domain.refund.dto.RefundDto;
import com.szs.szsrefund.domain.refund.service.RefundService;
import com.szs.szsrefund.domain.scrap.dto.IncomeDto;
import com.szs.szsrefund.domain.scrap.dto.TaxDto;
import com.szs.szsrefund.domain.scrap.entity.ScrapInfo;
import com.szs.szsrefund.domain.scrap.entity.ScrapResponse;
import com.szs.szsrefund.domain.scrap.entity.ScrapUser;
import com.szs.szsrefund.domain.scrap.repository.ScrapRepository;
import com.szs.szsrefund.domain.scrap.service.ScrapService;
import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.domain.user.repository.UserRepository;
import com.szs.szsrefund.domain.user.service.UserService;
import com.szs.szsrefund.global.config.common.service.ResponseService;
import com.szs.szsrefund.global.config.redis.RedisService;
import com.szs.szsrefund.global.error.user.UserExceptionHandler;
import com.szs.szsrefund.global.security.jwt.JwtTokenInterceptor;
import com.szs.szsrefund.global.security.jwt.JwtUtils;
import com.szs.szsrefund.global.utill.CrytptoUtils;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {CrytptoUtils.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class RefundServiceTest {

    @InjectMocks
    private RefundService refundService;

    @Mock
    private UserService userService;

    @Mock
    private CrytptoUtils crytptoUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScrapRepository scrapRepository;

    @Mock
    private ResponseService responseService;

    @Mock
    private RedisService redisService;

    @Mock
    private JwtTokenInterceptor jwtTokenInterceptor;

    @MockBean
    private JwtUtils jwtUtils;

    private final String TEST_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYmNkMTIzIiwiaWF0IjoxNjQ4ODg0MzcxLCJleHAiOjE2NTA2ODQzNzEsIm5hbWUiOiLtmY3quLjrj5kifQ.JxeVIigpxMynxpx-9OHQp3ZXpJV-sKWTMg6D_AmeP6Y";

    @BeforeEach
    public void setUp() {
        Key key = Keys.hmacShaKeyFor("aabbccddeeffgghhiijjkkll11223344556677889900".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(JwtUtils.class, "key", key);
    }

    @Test
    @Order(1)
    @DisplayName("유저 환급액조회 성공 테스트")
    void scrap_success_테스트() throws Exception {
        // given
        final ScrapUser scrapUser = createScrapUser();
        final User user = buildUserResponse();
        given(userRepository.findByUserId(any())).willReturn(Optional.of(user));
        given(scrapRepository.findByRegNo(any())).willReturn(Optional.of(scrapUser));

        RefundDto.Response response = refundService.refund(TEST_TOKEN);

        assertThat(response.getName()).isEqualTo(user.getName());
    }

    private User buildUserResponse() throws Exception {
        return User.builder()
                .userId("123")
                .password("1234")
                .name("홍길동")
                .regNo(CrytptoUtils.encrypt("860824-1655068"))
                .build();
    }

    private ScrapUser createScrapUser() {
        return ScrapUser.builder()
                .name("홍길동")
                .regNo("860824-1655068")
                .incomeDto(createIncomeDto())
                .taxDto(createTaxDto())
                .scrapResponse(createResponse())
                .scrapInfo(createScrepInfo())
                .build();
    }

    private IncomeDto createIncomeDto() {
        return IncomeDto.builder()
                .incomeDetail("급여")
                .totalPayment(new BigDecimal(24000000))
                .workStartDate("2020.10.03")
                .companyName("(주)활빈당")
                .name("홍길동")
                .regNo("860824-1655068")
                .paymentDate("2020.11.02")
                .workEndDate("2020.11.02")
                .incomeDivision("근로소득(연간)")
                .businessNumber("012-34-56789")
                .build();
    }

    private TaxDto createTaxDto() {
        return TaxDto.builder()
                .incomeDivision("산출세액")
                .totalUseAmount(new BigDecimal(2000000))
                .build();
    }

    private ScrapInfo createScrepInfo() {
        return ScrapInfo.builder()
                .appVer("2021112501")
                .hostNm("szs-codetest")
                .workerResDt(LocalDateTime.now())
                .workerReqDt(LocalDateTime.now())
                .build();
    }

    private ScrapResponse createResponse() {
        return ScrapResponse.builder()
                .errMsg("")
                .company("삼쩜삼")
                .svcCd("test01")
                .userId("1")
                .build();
    }
}
