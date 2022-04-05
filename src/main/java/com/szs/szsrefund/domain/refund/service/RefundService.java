package com.szs.szsrefund.domain.refund.service;

import com.szs.szsrefund.domain.refund.dto.RefundDto;
import com.szs.szsrefund.domain.refund.exception.NotCompleteScrapException;
import com.szs.szsrefund.domain.scrap.entity.ScrapUser;
import com.szs.szsrefund.domain.scrap.exception.NotFoundScrapInfoException;
import com.szs.szsrefund.domain.scrap.repository.ScrapRepository;
import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.domain.user.exception.NotFoundUserException;
import com.szs.szsrefund.domain.user.repository.UserRepository;
import com.szs.szsrefund.global.config.common.Constants;
import com.szs.szsrefund.global.config.redis.RedisService;
import com.szs.szsrefund.global.security.jwt.JwtUtils;
import com.szs.szsrefund.global.utill.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static com.szs.szsrefund.global.config.common.Constants.*;

@Service
public class RefundService {

    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final RedisService redisService;

    public RefundService(UserRepository userRepository, ScrapRepository scrapRepository, RedisService redisService) {
        this.userRepository = userRepository;
        this.scrapRepository = scrapRepository;
        this.redisService = redisService;
    }

    /**
     * scrap한 유저정보를 토대로 환불액을 가져온다.
     * @param token
     * @return
     */
    public RefundDto.Response refund(String token) {
        String userId = JwtUtils.getSubject(token);

        /* redis에 데이터가 있을경우 get 해서 return */
        if (isExistRefundFromRedis(userId)){
            String name = redisService.getValues(REFUND_HEAD_KEY+userId+NAME_KEY);
            String limit = redisService.getValues(REFUND_HEAD_KEY+userId+LIMIT_KEY);
            String deduction = redisService.getValues(REFUND_HEAD_KEY+userId+DEDUCTION_KEY);
            String refund = redisService.getValues(REFUND_HEAD_KEY+userId+REFUND_KEY);

            return RefundDto.Response.builder()
                    .name(name)
                    .limit(limit)
                    .deduction(deduction)
                    .refund(refund)
                    .build();
        }

        User user = userRepository.findByUserId(userId).orElseThrow(NotFoundUserException::new);
        Optional<ScrapUser> scrapUser = scrapRepository.findByRegNo(user.getRegNo());
        String tokenFromRedis = redisService.getValues(TOKEN_HEAD_KEY+user.getUserId());

        /* scrap 데이터가 없지만 redis에 토큰 정보가 있다면 데이터 수집중으로 예외 */
        if (!scrapUser.isPresent())
           if (tokenFromRedis != null) {
               throw new NotCompleteScrapException();
           } else {
               throw new NotFoundScrapInfoException();
           }

        return calculateRefund(scrapUser.get(), userId);
    }

    /**
     * redis에 환불 정보가 있을시 redis에서 가져옴
     * @param userId
     * @return
     */
    private boolean isExistRefundFromRedis(String userId) {
        return redisService.getValues(REFUND_HEAD_KEY+userId+NAME_KEY) != null;
    }

    /**
     * 환불액을 계산해서 return 한다.
     * @param scrapUser
     * @param userId
     * @return
     */
    private RefundDto.Response calculateRefund(ScrapUser scrapUser, String userId) {
        final BigDecimal TOTAL_PAYMENT = scrapUser.getScrapIncome().getTotalPayment(); /* 총급여액 */
        final BigDecimal TAX = scrapUser.getScrapTax().getTotalUseAmount(); /* 산출세액 */
        BigDecimal limitResult = calculateLimit(TOTAL_PAYMENT); /* 한도 */
        BigDecimal deductionResult = calculateDeduction(TAX); /* 공제액 */
        BigDecimal refundResult = limitResult.min(deductionResult); /* 환급액 */

        RefundDto.Response refundDto = RefundDto.Response.builder()
                .name(scrapUser.getName())
                .limit(StringUtils.changeRefundWord(limitResult))
                .deduction(StringUtils.changeRefundWord(deductionResult))
                .refund(StringUtils.changeRefundWord(refundResult))
                .build();

        /* redis에 결과를 10분간 저장한다. */
        saveRefundToRedis(refundDto, userId);

        return refundDto;
    }

    private void saveRefundToRedis(RefundDto.Response refundDto, String userId) {
        redisService.setValues(REFUND_HEAD_KEY+userId+NAME_KEY, refundDto.getName(), 600);
        redisService.setValues(REFUND_HEAD_KEY+userId+LIMIT_KEY, refundDto.getLimit(), 600);
        redisService.setValues(REFUND_HEAD_KEY+userId+DEDUCTION_KEY, refundDto.getDeduction(), 600);
        redisService.setValues(REFUND_HEAD_KEY+userId+REFUND_KEY, refundDto.getRefund(), 600);
    }

    /**
     * 근로소득 세액공제 한도 계산식
     * @param TOTAL_PAYMENT
     * @return
     */
    public BigDecimal calculateLimit(BigDecimal TOTAL_PAYMENT) {
        BigDecimal result;
        if (TOTAL_PAYMENT.compareTo(Constants.TOTAL_PAYMENT_MIN) < 0) {
            result = new BigDecimal(740000);
        } else if (TOTAL_PAYMENT.compareTo(Constants.TOTAL_PAYMENT_MAX) < 0) {
            result = Constants.LIMIT_MAX.subtract((TOTAL_PAYMENT.subtract(Constants.TOTAL_PAYMENT_MIN)).multiply(Constants.LIMIT_MID_RATE));
            result =  result.compareTo(Constants.LIMIT_MID) < 0 ? Constants.LIMIT_MID : result;
        } else {
            result = Constants.LIMIT_MID.subtract((TOTAL_PAYMENT.subtract(Constants.TOTAL_PAYMENT_MAX)).multiply(Constants.LIMIT_MAX_RATE));
            result = result.compareTo(Constants.LIMIT_MIN) < 0 ? Constants.LIMIT_MIN : result;
        }
        return result;
    }

    /**
     * 근로소득 세액공제 계산식
     * @param TAX
     * @return
     */
    public BigDecimal calculateDeduction(BigDecimal TAX) {
        BigDecimal result;
        if (TAX.compareTo(Constants.TAX_STANDARD) <= 0) {
            result = TAX.multiply(Constants.TAX_MIN_LATE);
        } else {
            result = Constants.TAX_MAX.add(TAX.subtract(Constants.TAX_STANDARD).multiply(Constants.TAX_MAX_LATE));
        }
        return result;
    }

}
