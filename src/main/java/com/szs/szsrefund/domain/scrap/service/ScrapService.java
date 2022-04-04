package com.szs.szsrefund.domain.scrap.service;

import com.szs.szsrefund.domain.scrap.dto.IncomeDto;
import com.szs.szsrefund.domain.scrap.dto.ScrapDto;
import com.szs.szsrefund.domain.scrap.dto.ScrapJsonListDto;
import com.szs.szsrefund.domain.scrap.dto.TaxDto;
import com.szs.szsrefund.domain.scrap.entity.ScrapInfo;
import com.szs.szsrefund.domain.scrap.entity.ScrapResponse;
import com.szs.szsrefund.domain.scrap.entity.ScrapUser;
import com.szs.szsrefund.domain.scrap.exception.NotFoundIncomeException;
import com.szs.szsrefund.domain.scrap.exception.NotFoundTaxException;
import com.szs.szsrefund.domain.scrap.exception.ScrapUserDataNullException;
import com.szs.szsrefund.domain.scrap.repository.*;
import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.domain.user.exception.NotFoundUserException;
import com.szs.szsrefund.domain.user.repository.UserRepository;
import com.szs.szsrefund.global.config.redis.RedisService;
import com.szs.szsrefund.global.error.StatusCode;
import com.szs.szsrefund.global.security.jwt.JwtUtils;
import com.szs.szsrefund.global.utill.CrytptoUtils;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.util.Optional;

import static com.szs.szsrefund.global.config.common.Constants.TOKEN_HEAD_KEY;

@Slf4j
@Service
public class ScrapService {

    @Value("${3o3.uri-path}")
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
        String userId = JwtUtils.getSubject(token);

        /* 60초 이내 재요청 할 시 요청중이라는 상태값 return */
        if(isExistTokenFromRedis(userId, token))
            return StatusCode.API_LODING;

        /* 단기간내 바로 재조회를 할 수 없도록 60초 제한 */
        redisService.setValues(TOKEN_HEAD_KEY+userId, token, 60);

        callScrapApi(requestDto)
                .subscribe(
                        response -> {
                            try {
                                setResponseData(response, requestDto);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
        return StatusCode.CALL_API;
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
        String userId = JwtUtils.getSubject(token);

        User user = userRepository.findByUserId(userId).orElseThrow(NotFoundUserException::new);

        return ScrapDto.Request.builder()
                .name(user.getName())
                .regNo(CrytptoUtils.decrypt(user.getRegNo()))
                .build();
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
        ScrapJsonListDto jsonList = response.getScrapJsonList();

        if (jsonList == null)
            throw new ScrapUserDataNullException();

        IncomeDto incomeDto = findIncomeData(jsonList);
        TaxDto taxDto = findTaxData(jsonList);
        ScrapResponse scrapResponse = findResponseData(jsonList);
        ScrapInfo scrapInfo = findInfoData(response);

        ScrapUser scrapUser = ScrapUser.builder()
                .name(requestDto.getName())
                .regNo(CrytptoUtils.encrypt(requestDto.getRegNo()))
                .incomeDto(incomeDto)
                .taxDto(taxDto)
                .scrapResponse(scrapResponse)
                .scrapInfo(scrapInfo)
                .build();

        Optional<ScrapUser> userResult =  scrapRepository.findByRegNo(scrapUser.getRegNo());

        if(userResult.isPresent()){
            scrapRepository.delete(userResult.get());
        }

        scrapRepository.save(scrapUser);
    }

    private IncomeDto findIncomeData(ScrapJsonListDto jsonList) {
        return jsonList
                .getScrap001()
                .stream()
                .findFirst()
                .orElseThrow(NotFoundIncomeException::new);
    }

    private TaxDto findTaxData(ScrapJsonListDto jsonList) {
        return jsonList
                .getScrap002()
                .stream()
                .findFirst()
                .orElseThrow(NotFoundTaxException::new);
    }

    private ScrapResponse findResponseData(ScrapJsonListDto jsonList) {
        return ScrapResponse.builder()
                .errMsg(jsonList.getErrMsg())
                .company(jsonList.getCompany())
                .svcCd(jsonList.getSvcCd())
                .userId(jsonList.getUserId())
                .build();
    }

    private ScrapInfo findInfoData(ScrapDto.Response response) {
        return ScrapInfo.builder()
                .appVer(response.getAppVer())
                .hostNm(response.getHostNm())
                .workerReqDt(response.getWorkerReqDt())
                .workerResDt(response.getWorkerResDt())
                .build();
    }

}
