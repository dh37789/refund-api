package com.szs.szsrefund.domain.user.api;

import com.szs.szsrefund.domain.user.dto.UserLoginDto;
import com.szs.szsrefund.domain.user.dto.UserSignDto;
import com.szs.szsrefund.domain.user.service.UserService;
import com.szs.szsrefund.global.config.common.ResponseResult;
import com.szs.szsrefund.global.config.common.service.ResponseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value="", notes="")
    @ApiResponses({
            @ApiResponse(code = 201, message = ""),
            @ApiResponse(code = 400, message = "")
    })
    @PostMapping("/signup")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseResult<UserSignDto.Response> signUp(@RequestBody @Valid UserSignDto.Request requestDto) throws Exception {
        return responseService.getResponseResult(userService.signUp(requestDto));
    }

    @PostMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseResult<UserLoginDto.Response> login(@RequestBody @Valid UserLoginDto.Request requestDto) {
        return responseService.getResponseResult(userService.login(requestDto));
    }
}
