package com.tax.refund.scrap;

import com.tax.refund.domain.scrap.dto.IncomeDto;
import com.tax.refund.domain.scrap.dto.TaxDto;
import com.tax.refund.domain.scrap.entity.ScrapInfo;
import com.tax.refund.domain.scrap.entity.ScrapResponse;
import com.tax.refund.domain.scrap.entity.ScrapUser;
import com.tax.refund.domain.scrap.repository.ScrapRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class ScrapRepositoryTest {

    @Mock
    private ScrapRepository scrapRepository;

    @Test
    @Order(1)
    @DisplayName("findByRegNo 테스트")
    void findByRegNo_테스트() {
        // given
        ScrapUser scrapUser = createScrapUser();

        // when
        when(scrapRepository.findByRegNo(any())).thenReturn(Optional.of(scrapUser));

        // when
        final String regNo = "860824-1655068";
        ScrapUser result = scrapRepository.findByRegNo(regNo).get();

        // then
        assertThat(result.getName()).isEqualTo(scrapUser.getName());

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
                .hostNm("tax-codetest")
                .workerResDt(LocalDateTime.now())
                .workerReqDt(LocalDateTime.now())
                .build();
    }

    private ScrapResponse createResponse() {
        return ScrapResponse.builder()
                .errMsg("")
                .company("회사명")
                .svcCd("test01")
                .userId("1")
                .build();
    }
}
