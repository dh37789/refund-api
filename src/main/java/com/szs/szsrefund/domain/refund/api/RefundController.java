package com.szs.szsrefund.domain.refund.api;

import com.szs.szsrefund.domain.refund.dto.RefundDto;
import com.szs.szsrefund.domain.refund.service.RefundService;
import com.szs.szsrefund.global.config.common.ResponseResult;
import com.szs.szsrefund.global.config.common.service.ResponseService;
import com.szs.szsrefund.global.security.jwt.JwtUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/szs")
public class RefundController {

    private final RefundService refundService;
    private final ResponseService responseService;

    public RefundController(RefundService refundService, ResponseService responseService) {
        this.refundService = refundService;
        this.responseService = responseService;
    }

    @ApiOperation(value="사용자 환급액 가져오기", notes="유저의 스크랩 정보를 바탕으로 유저의 환급액을 계산하여 반환합니다.")
    @GetMapping("/refund")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseResult<RefundDto.Response> refund(@ApiParam(value = "refund 환급액 request") HttpServletRequest request) {
        String token = JwtUtils.resolveToken(request);
        return responseService.getResponseResult(refundService.refund(token));
    }

}
