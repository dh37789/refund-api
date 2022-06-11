package com.tax.refund.refund;

import com.tax.refund.domain.refund.dto.RefundDto;
import com.tax.refund.domain.refund.service.RefundService;
import com.tax.refund.domain.scrap.dto.IncomeDto;
import com.tax.refund.domain.scrap.dto.TaxDto;
import com.tax.refund.domain.scrap.entity.ScrapInfo;
import com.tax.refund.domain.scrap.entity.ScrapResponse;
import com.tax.refund.domain.scrap.entity.ScrapUser;
import com.tax.refund.domain.scrap.repository.ScrapRepository;
import com.tax.refund.domain.user.entity.User;
import com.tax.refund.domain.user.repository.UserRepository;
import com.tax.refund.domain.user.service.UserService;
import com.tax.refund.global.config.common.Constants;
import com.tax.refund.global.config.common.service.ResponseService;
import com.tax.refund.global.config.redis.RedisService;
import com.tax.refund.global.security.jwt.JwtTokenInterceptor;
import com.tax.refund.global.security.jwt.JwtUtils;
import com.tax.refund.global.utill.CrytptoUtils;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
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
    void refund_success_테스트() throws Exception {
        // given
        final ScrapUser scrapUser = createScrapUser();
        final User user = buildUserResponse();
        given(userRepository.findByUserId(any())).willReturn(Optional.of(user));
        given(scrapRepository.findByRegNo(any())).willReturn(Optional.of(scrapUser));

        // when
        RefundDto.Response response = refundService.refund(TEST_TOKEN);

        // then
        assertThat(response.getName()).isEqualTo(user.getName());
    }

    @Test
    @Order(2)
    @DisplayName("유저 환급액조회 3300만원 이하 테스트")
    void refund_3300_under_테스트() {
        // given
        final BigDecimal MONEY = new BigDecimal(32900000);

        // when
        BigDecimal calcMoney = refundService.calculateLimit(MONEY);

        // then
        assertThat(calcMoney).isEqualTo(Constants.LIMIT_MAX);
    }

    @Test
    @Order(3)
    @DisplayName("유저 환급액조회 3300만원 초과 7000만원 이하 환급액 66만원 테스트")
    void refund_3300_over_7000_under_cass_66_테스트() {
        // given 740000 - ((50000000 - 33000000) × 0.008) = 604000 -> 66만원 이하
        final BigDecimal MONEY = new BigDecimal(50000000);

        // when
        BigDecimal calcMoney = refundService.calculateLimit(MONEY);

        // then
        assertThat(calcMoney).isEqualTo(Constants.LIMIT_MID);
    }

    @Test
    @Order(4)
    @DisplayName("유저 환급액조회 3300만원 초과 7000만원 이하 환급액 66만원 이상 테스트")
    void refund_3300_over_7000_under_cass_66_up_테스트() {
        // given 740000 - ((35000000 - 33000000) × 0.008) = 724000 -> 66만원 이상
        final BigDecimal MONEY = new BigDecimal(35000000);

        // when
        BigDecimal calcMoney = refundService.calculateLimit(MONEY);

        // then
        assertThat(calcMoney).isEqualTo(Constants.LIMIT_MAX.subtract((MONEY.subtract(Constants.TOTAL_PAYMENT_MIN)).multiply(Constants.LIMIT_MID_RATE)));
    }

    @Test
    @Order(5)
    @DisplayName("유저 환급액조회 7000만원 초과 환급액 50만원 이상 테스트")
    void refund_7000_over_cass_50_down_테스트() {
        // given 660000 - ((90000000 - 70000000) × 0.5) = -9340000 -> 50만원 이하
        final BigDecimal MONEY = new BigDecimal(90000000);

        // when
        BigDecimal calcMoney = refundService.calculateLimit(MONEY);

        // then
        assertThat(calcMoney).isEqualTo(Constants.LIMIT_MIN);
    }

    @Test
    @Order(5)
    @DisplayName("유저 환급액조회 7000만원 초과 환급액 50만원 이상 테스트")
    void refund_7000_over_cass_50_up_테스트() {
        // given 660000 - ((70000100 - 70000000) × 0.5) = 659950 -> 50만원 이상
        final BigDecimal MONEY = new BigDecimal(70000100);

        // when
        BigDecimal calcMoney = refundService.calculateLimit(MONEY);

        // then
        assertThat(calcMoney).isEqualTo(Constants.LIMIT_MID.subtract((MONEY.subtract(Constants.TOTAL_PAYMENT_MAX)).multiply(Constants.LIMIT_MAX_RATE)));
    }

    @Test
    @Order(6)
    @DisplayName("유저 산출세액 130만원 이하")
    void tax_130_under_테스트() {
        // given 1250000 × 0.55 = 687500
        final BigDecimal TAX = new BigDecimal(1250000);

        // when
        BigDecimal calcTax = refundService.calculateDeduction(TAX);

        // then
        assertThat(calcTax).isEqualTo(TAX.multiply(Constants.TAX_MIN_LATE));
    }

    @Test
    @Order(7)
    @DisplayName("유저 산출세액 130만원 초과")
    void tax_130_over_테스트() {
        // given 715000 + ((1500000-1300000) * 0.3) = 775000 +- 1
        final BigDecimal TAX = new BigDecimal(1500000);

        // when
        BigDecimal calcTax = refundService.calculateDeduction(TAX);

        // then
        assertThat(calcTax).isEqualTo(Constants.TAX_MAX.add(TAX.subtract(Constants.TAX_STANDARD).multiply(Constants.TAX_MAX_LATE)));
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
