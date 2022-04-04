package com.szs.szsrefund.domain.user.api;

import com.szs.szsrefund.domain.user.dto.UserInfoDto;
import com.szs.szsrefund.domain.user.dto.UserLoginDto;
import com.szs.szsrefund.domain.user.dto.UserSignDto;
import com.szs.szsrefund.domain.user.service.UserService;
import com.szs.szsrefund.global.config.common.ResponseResult;
import com.szs.szsrefund.global.config.common.service.ResponseService;
import com.szs.szsrefund.global.security.jwt.JwtUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/szs")
public class UserController {

    private final UserService userService;
    private final ResponseService responseService;

    public UserController(UserService userService, ResponseService responseService) {
        this.userService = userService;
        this.responseService = responseService;
    }

    @ApiOperation(value="사용자 회원가입", notes="환급정보를 조회 하기 위해 회원가입 합니다.")
    @PostMapping("/signup")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseResult<UserSignDto.Response> signUp(@RequestBody @Valid @ApiParam(value = "사용자 회원가입 request") UserSignDto.Request requestDto) throws Exception {
        return responseService.getResponseResult(userService.signUp(requestDto));
    }

    @ApiOperation(value="사용자 로그인", notes="로그인하고 JWT 토큰을 반환합니다.")
    @PostMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseResult<UserLoginDto.Response> login(@RequestBody @Valid @ApiParam(value = "사용자 로그인 request") UserLoginDto.Request requestDto) {
        return responseService.getResponseResult(userService.login(requestDto));
    }

    @ApiOperation(value="사용자 정보보기", notes="header의 토큰정보를 읽어 내정보를 반환합니다.")
    @GetMapping("/me")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseResult<UserInfoDto.Response> me(HttpServletRequest request) throws Exception  {
        String token = JwtUtils.resolveToken(request);
        return responseService.getResponseResult(userService.me(JwtUtils.getSubject(token)));
    }
}
