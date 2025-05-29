package com.galilikelike.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galilikelike.annos.PreAuth;
import com.galilikelike.common.BusinessException;
import com.galilikelike.common.ErrorCode;
import com.galilikelike.common.Result;
import com.galilikelike.model.dto.PageDto;
import com.galilikelike.model.dto.UserDto;
import com.galilikelike.model.dto.UserLoginDto;
import com.galilikelike.model.pojo.User;
import com.galilikelike.model.vo.UserSimpleVo;
import com.galilikelike.model.vo.UserVo;
import com.galilikelike.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @PostMapping("/regedit")
    public Result regedit(@Valid @RequestBody UserDto userDto, HttpServletRequest request) throws BusinessException {
        Long userId = userService.regedit(userDto,request);
        return Result.success(userId);
    }

    @PostMapping("/code")
    public Result createCode(HttpServletRequest request) {
        String code = userService.createCode(request);
        return Result.success(code);
    }

    @PostMapping("/login")
    public Result login(@Valid @RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        log.info("userLoginDto: {}", userLoginDto);
        UserVo user = userService.login(userLoginDto, request);
        return Result.success(user);
    }

    @GetMapping("/search")
    @PreAuth(userRole = "管理员")
    public Result searchUser(String username,HttpServletRequest request) {
        List<UserVo> userVos = userService.searchUser(username);
        return Result.success(userVos);
    }

    @PostMapping("/page")
    @PreAuth(userRole = "管理员")
    public Result searchPageUsers(@Valid @RequestBody PageDto pageDto,HttpServletRequest request) {
        Page<UserVo> pageUsers = userService.searchPageUsers(pageDto, request);
        return Result.success(pageUsers);
    }

    @PostMapping("/views")
    public Result viewUsers(@Valid @RequestBody PageDto pageDto, HttpServletRequest request) {
        List<UserSimpleVo> userSimpleVos = userService.viewsUsers(pageDto,request);
        return Result.success(userSimpleVos);
    }

    @DeleteMapping("/delete")
    @PreAuth(userRole = "管理员")
    public Result deleteUser(Long userId,HttpServletRequest request) {

        Boolean resultData = userService.deleteUser(userId);
        if (resultData)
            return Result.success(null);
        else
            return Result.fail(ErrorCode.PARAM_ERROR,"用户id为空/用户不存在");
    }

    @GetMapping("/current")
    public Result getCurrentUser(HttpServletRequest httpServletRequest) {
        UserVo currentUser = userService.getCurrentUser(httpServletRequest);
        if (Objects.isNull(currentUser))
            return Result.fail(ErrorCode.NO_AUTH,"未登录");
        return Result.success(currentUser);
    }

    @PostMapping("/reset")
    public Result resetPassword(@Valid @RequestBody UserLoginDto userDto) {
        Boolean resultData = userService.resetPassword(userDto);
        if (!resultData)
            return Result.fail("重置密码错误","重置密码错误");
        return Result.success("重置密码成功","重置密码成功",null);
    }

    @GetMapping("/logout")
    public Result logout(HttpServletRequest httpServletRequest) {
        Boolean res = userService.logout(httpServletRequest);
        if (!res)
            return Result.fail("退出登录失败","退出登录失败");
        return Result.success(null);
    }
}
