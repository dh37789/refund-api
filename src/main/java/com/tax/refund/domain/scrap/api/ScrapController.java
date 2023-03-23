package com.tax.refund.domain.scrap.api;

import com.tax.refund.domain.scrap.service.ScrapService;
import com.tax.refund.global.config.common.Response;
import com.tax.refund.global.config.common.ResponseResult;
import com.tax.refund.global.config.common.service.ResponseService;
import com.tax.refund.global.security.jwt.JwtUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tax")
public class ScrapController {

    private final ScrapService scrapService;

    public ScrapController(ScrapService scrapService) {
        this.scrapService = scrapService;
    }

    @ApiOperation(value="사용자 소득내역 가져오기", notes="인증 토큰을 이용해서 URL 에서 유저의 소득 내역을 가져와 DB에 저장합니다.")
    @PostMapping("/scrap")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseResult<Response> scrap(@ApiParam(value = "Scrap 조회 사용자 request") HttpServletRequest request) throws Exception {
        String token = JwtUtils.resolveToken(request);
        return ResponseService.getResponseResult(scrapService.saveScrap(token));
    }


}
