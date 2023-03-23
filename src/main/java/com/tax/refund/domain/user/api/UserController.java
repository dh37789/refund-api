package com.tax.refund.domain.user.api;

import com.tax.refund.domain.user.dto.UserInfoDto;
import com.tax.refund.domain.user.dto.UserLoginDto;
import com.tax.refund.domain.user.dto.UserSignDto;
import com.tax.refund.domain.user.service.UserService;
import com.tax.refund.global.config.common.ResponseResult;
import com.tax.refund.global.config.common.service.ResponseService;
import com.tax.refund.global.security.jwt.JwtUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/tax")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value="사용자 회원가입", notes="환급정보를 조회 하기 위해 회원가입 합니다.")
    @PostMapping("/signup")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseResult<UserSignDto.Response> signUp(@RequestBody @Valid @ApiParam(value = "사용자 회원가입 request") UserSignDto.Request requestDto) throws Exception {
        return ResponseService.getResponseResult(userService.signUp(requestDto));
    }

    @ApiOperation(value="사용자 로그인", notes="로그인하고 JWT 토큰을 반환합니다.")
    @PostMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseResult<UserLoginDto.Response> login(@RequestBody @Valid @ApiParam(value = "사용자 로그인 request") UserLoginDto.Request requestDto) {
        return ResponseService.getResponseResult(userService.login(requestDto));
    }

    @ApiOperation(value="사용자 정보보기", notes="header의 토큰정보를 읽어 내정보를 반환합니다.")
    @GetMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseResult<UserInfoDto.Response> me(HttpServletRequest request) throws Exception  {
        String token = JwtUtils.resolveToken(request);
        return ResponseService.getResponseResult(userService.me(JwtUtils.getSubject(token)));
    }
}
