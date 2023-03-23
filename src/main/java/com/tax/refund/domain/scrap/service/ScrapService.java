package com.tax.refund.domain.scrap.service;

import com.tax.refund.domain.scrap.dto.IncomeDto;
import com.tax.refund.domain.scrap.dto.ScrapDto;
import com.tax.refund.domain.scrap.dto.ScrapJsonListDto;
import com.tax.refund.domain.scrap.dto.TaxDto;
import com.tax.refund.domain.scrap.entity.ScrapInfo;
import com.tax.refund.domain.scrap.entity.ScrapResponse;
import com.tax.refund.domain.scrap.entity.ScrapUser;
import com.tax.refund.domain.scrap.exception.ScrapUserDataNullException;
import com.tax.refund.domain.scrap.repository.ScrapRepository;
import com.tax.refund.domain.user.entity.User;
import com.tax.refund.domain.user.exception.NotFoundUserException;
import com.tax.refund.domain.user.repository.UserRepository;
import com.tax.refund.global.config.redis.RedisService;
import com.tax.refund.global.error.StatusCode;
import com.tax.refund.global.security.jwt.JwtUtils;
import com.tax.refund.global.utill.CrytptoUtils;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.util.Optional;

import static com.tax.refund.global.config.common.Constants.*;

@Slf4j
@Service
public class ScrapService {

    @Value("${api.uri-path}")
    private String scrapUri;

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final RedisService redisService;

    public ScrapService(WebClient webClient, UserRepository userRepository, ScrapRepository scrapRepository, RedisService redisService){
        this.webClient = webClient;
        this.userRepository = userRepository;
        this.scrapRepository = scrapRepository;
        this.redisService = redisService;
    }

    /**
     * reactive non blocking 방식으로 통신하여 데이터를 조회해온다.
     * @param token
     * @return
     * @throws Exception
     */
    public StatusCode saveScrap(String token) throws Exception {
        final ScrapDto.Request requestDto = findUserId(token);
        final String userId = getIdByToken(token);

        // TODO Redission으로 변경
        /* 60초 이내 재요청 할 시 요청중이라는 상태값 return */
        if(isExistTokenFromRedis(userId, token))
            return StatusCode.API_LODING;

        /* 단기간내 바로 재조회를 할 수 없도록 60초 제한 */
        redisService.setValues(TOKEN_HEAD_KEY+userId, token, 60);

        delRefundData(userId);

        scrapCallback(requestDto);

        return StatusCode.CALL_API;
    }

    private void scrapCallback(ScrapDto.Request requestDto) {
        callScrapApi(requestDto)
                .retry(3)
                .subscribe(
                        response -> {
                            try {
                                setResponseData(response, requestDto);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
    }

    // TODO redis DB설정 추가
    private void delRefundData(String userId) {
        redisService.delValues(REFUND_HEAD_KEY+userId+NAME_KEY);
        redisService.delValues(REFUND_HEAD_KEY+userId+LIMIT_KEY);
        redisService.delValues(REFUND_HEAD_KEY+userId+DEDUCTION_KEY);
        redisService.delValues(REFUND_HEAD_KEY+userId+REFUND_KEY);
    }

    /**
     * 토큰정보가 없거나, 기존과 다를 경우에만 데이터 재조회
     * @param userId
     * @param token
     * @return
     */
    private boolean isExistTokenFromRedis(String userId, String token) {
        String tokenFromRedis = redisService.getValues("token:"+userId);
        return tokenFromRedis != null&&token.equals(tokenFromRedis);
    }

    /**
     * token정보의 userId를 조회
     * @param token
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public ScrapDto.Request findUserId(String token) throws Exception {
        User user = userRepository.findByUserId(getIdByToken(token)).orElseThrow(NotFoundUserException::new);

        return ScrapDto.Request.builder()
                .name(user.getName())
                .regNo(CrytptoUtils.decrypt(user.getRegNo()))
                .build();
    }

    private static String getIdByToken(String token) {
        return JwtUtils.getSubject(token);
    }

    /**
     * non blocking webClient 통신으로 데이터 조회
     * @param requestDto
     * @return
     * @throws Exception
     */
    private Mono<ScrapDto.Response> callScrapApi(ScrapDto.Request requestDto) {
        return webClient.mutate()
                .build()
                .post()
                .uri(scrapUri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), ScrapDto.Request.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                .bodyToMono(ScrapDto.Response.class)
                .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"));
    }

    /**
     * 조회해온 데이터를 저장한다.
     * @param response
     * @param requestDto
     * @throws Exception
     */
    @Transactional
    public void setResponseData(ScrapDto.Response response, ScrapDto.Request requestDto) throws Exception {
        final ScrapJsonListDto jsonList = response.getScrapJsonList();

        if (isNotExsitScrapInfo(jsonList))
            throw new ScrapUserDataNullException();

        ScrapUser scrapUser = getScrapUser(response, requestDto, jsonList);

        Optional<ScrapUser> userResult = scrapRepository.findByRegNo(scrapUser.getRegNo());

        userResult.ifPresent(scrapRepository::delete);

        scrapRepository.save(scrapUser);
    }

    private static boolean isNotExsitScrapInfo(ScrapJsonListDto jsonList) {
        return ObjectUtils.isEmpty(jsonList);
    }

    private static ScrapUser getScrapUser(ScrapDto.Response response, ScrapDto.Request requestDto, ScrapJsonListDto jsonList) throws Exception {
        IncomeDto incomeDto = IncomeDto.findIncomeData(jsonList);
        TaxDto taxDto = TaxDto.findTaxData(jsonList);
        ScrapResponse scrapResponse = ScrapResponse.findResponseData(jsonList);
        ScrapInfo scrapInfo = ScrapInfo.findInfoData(response);

        ScrapUser scrapUser = ScrapUser.builder()
                .name(requestDto.getName())
                .regNo(CrytptoUtils.encrypt(requestDto.getRegNo()))
                .incomeDto(incomeDto)
                .taxDto(taxDto)
                .scrapResponse(scrapResponse)
                .scrapInfo(scrapInfo)
                .build();
        return scrapUser;
    }

}
