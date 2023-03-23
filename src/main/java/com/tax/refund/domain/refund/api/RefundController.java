package com.tax.refund.domain.refund.api;

import com.tax.refund.domain.refund.dto.RefundDto;
import com.tax.refund.domain.refund.service.RefundService;
import com.tax.refund.global.config.common.ResponseResult;
import com.tax.refund.global.config.common.service.ResponseService;
import com.tax.refund.global.security.jwt.JwtUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tax")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @ApiOperation(value="사용자 환급액 가져오기", notes="유저의 스크랩 정보를 바탕으로 유저의 환급액을 계산하여 반환합니다.")
    @GetMapping("/refund")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseResult<RefundDto.Response> refund(@ApiParam(value = "refund 환급액 request") HttpServletRequest request) {
        String token = JwtUtils.resolveToken(request);
        return ResponseService.getResponseResult(refundService.refund(token));
    }

}
