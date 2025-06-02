package com.galilikelike.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.galilikelike.model.dto.*;
import com.galilikelike.model.pojo.User;
import com.galilikelike.model.vo.UserSimpleVo;
import com.galilikelike.model.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


/**
* @author Lenovo
* @description 针对表【users】的数据库操作Service
* @createDate 2025-05-06 17:35:20
*/
@Validated
public interface UserService extends IService<User> {

    Long regedit(@Valid UserDto userDto,HttpServletRequest request);

    UserVo login(@Valid UserLoginDto userLoginDto, HttpServletRequest request);

    List<UserVo> searchUser(String username);

    Boolean deleteUser(Long userId);

    UserVo getCurrentUser(HttpServletRequest request);

    Page<UserVo> searchPageUsers(@Valid PageDto pageDto, HttpServletRequest request);

    Boolean logout(HttpServletRequest request);

    Boolean resetPassword(UserLoginDto loginDto);

    String createCode(HttpServletRequest request);

    Page<UserVo> pageUsers(@Valid PageDto pageDto);

    List<UserSimpleVo> viewsUsers(PageDto pageDto,HttpServletRequest request);

    Boolean forbid(String account,HttpServletRequest request);

    UserVo edit(UserBaseDto userBaseDto);

    String showPhone();

    String showEmail();

    Boolean updatePassword(PasswordDto passwordDto);

    Boolean upload(MultipartFile file);
}
