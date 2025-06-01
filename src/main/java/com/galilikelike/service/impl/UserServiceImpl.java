package com.galilikelike.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galilikelike.common.BusinessException;
import com.galilikelike.mapper.UserMapper;
import com.galilikelike.model.dto.ConditionQuery;
import com.galilikelike.model.dto.PageDto;
import com.galilikelike.model.dto.UserDto;
import com.galilikelike.model.dto.UserLoginDto;
import com.galilikelike.model.pojo.User;
import com.galilikelike.model.vo.UserSimpleVo;
import com.galilikelike.model.vo.UserVo;
import com.galilikelike.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import com.galilikelike.contant.UserContant;
/**
* @author galiLikeLike
* @description 针对表【users】的数据库操作Service实现
* @createDate 2025-05-06 17:35:20
*/
@Validated
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private ConcurrentHashMap<String,ShearCaptcha> hashMap = new ConcurrentHashMap<>();

    @Autowired
    private UserMapper userMapper;

    public UserServiceImpl() {
    }

    @Override
    public Long regedit(@Valid UserDto userDto,HttpServletRequest request) {
        String code = userDto.getCode();
        // 判断验证码是否正确 todo
        // 这是根据ip得出
//        ShearCaptcha captcha = (ShearCaptcha) hashMap.get(request.getRemoteAddr());
        // 根据session得出
        ShearCaptcha captcha = (ShearCaptcha) request.getSession().getAttribute("captcha");
        if (Objects.isNull(captcha)) {
            throw new BusinessException("先获取验证码");
        }
        boolean result = captcha.verify(code);
        if (!result) {
            throw new BusinessException("验证码错误");
        }
        String password = userDto.getUserPassword();
        String account = userDto.getUserAccount();
        User user = this.getOne(new QueryWrapper<User>().eq("userAccount",account ));
        if (user != null) {
            throw BusinessException.getUserExists();
        } else {
            String encryptPassword = DigestUtils.md5DigestAsHex((UserContant.SALT+password).getBytes(StandardCharsets.UTF_8));
            User newUser = new User();
            newUser.setUserAccount(account);
            newUser.setUserPassword(encryptPassword);
            boolean res = this.save(newUser);
            if (!res) {
                throw new BusinessException("用户注册失败");
            } else
                return newUser.getId();
        }
    }

    @Override
    public UserVo login(@Valid UserLoginDto userLoginDto, HttpServletRequest request) {
        String userAccount = userLoginDto.getUserAccount();
        User queryUser = this.getOne(new QueryWrapper<User>().eq("userAccount",userAccount));
        if (Objects.isNull(queryUser)) {
            throw BusinessException.getUserNull();
        } else {
            String queryUserPd = queryUser.getUserPassword();
            String inputPassword = userLoginDto.getUserPassword();
            String inputEncrypt = DigestUtils.md5DigestAsHex((UserContant.SALT+inputPassword).getBytes(StandardCharsets.UTF_8));
            if (!queryUserPd.equals(inputEncrypt)) {
                throw new BusinessException("密码错误");
            } else {
                UserVo userVo = User.getUserVo(queryUser);
                HttpSession session = request.getSession();
                LOG.info(session.toString());
                request.getSession().setAttribute(UserContant.LOGIN_STATUS,userVo);
                return userVo;
            }
        }
    }


    @Override
    public Page<UserVo> searchPageUsers(@Valid PageDto pageDto,HttpServletRequest request) {
//        return this.list(new Page<>(pageDto.getCurrent(),pageDto.getPageSize())).stream().map(this::getUserVo).toList();
//        Page<User> userPage = userMapper.selectPage(new Page<>(pageDto.getCurrent(),pageDto.getPageSize()),null);
        Page<User> userPage = userMapper.selectUserPage(new Page<User>(pageDto.getCurrent(), pageDto.getPageSize()));
        List<User> users = userPage.getRecords();
        List<UserVo> userVos = users.stream().map(User::getUserVo).collect(Collectors.toList());
        LOG.info("用户:{}",userVos);
        Page<UserVo> userVoPage = new Page<>(userPage.getCurrent(),userPage.getSize(),userPage.getTotal());
        userVoPage.setRecords(userVos);
        return userVoPage;
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return false;
        } else {
            HttpSession session = request.getSession();
            session.removeAttribute(UserContant.LOGIN_STATUS);
            return true;
        }
    }


    @Override
    public Boolean forbid(String account,HttpServletRequest request) {
        return userMapper.changeStatus(account);
    }

    @Override
    @Transactional(rollbackFor = DataAccessException.class)
    public Boolean resetPassword(UserLoginDto loginDto) {
        String account = loginDto.getUserAccount();
        Boolean result = userMapper.resetPasswordByAccount(account);
        return result;
    }

    @Override
    public String createCode(HttpServletRequest request) {
// 自定义验证码内容为四则运算方式
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 45, 4, 4);
        captcha.setGenerator(new MathGenerator());
// 重新生成code
        captcha.createCode();
        String code = captcha.getCode();
        LOG.info("验证码:{},图像验证码:{}",code,captcha.getImageBase64Data());
        // 根据ip
//        String remoteIp = request.getRemoteAddr();
//        hashMap.put(remoteIp,captcha);
        // 设置session
        request.getSession().setAttribute("captcha",captcha);
        return captcha.getImageBase64Data();
    }

    public Page<UserVo> pageUsers(@Valid PageDto pageDto) {
        return this.searchPageUsers(pageDto,null);
    }

    @Override
    public List<UserSimpleVo> viewsUsers(PageDto pageDto,HttpServletRequest request) {
        return this.list(new Page<>(pageDto.getCurrent(),pageDto.getPageSize())).stream().map(User::getUserSimpleVo).toList();
    }

    @Override
    public List<UserVo> searchUser(String UserAccount) {
        if(StringUtils.isNotEmpty(UserAccount)) {
            throw BusinessException.getUserNull();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("UserAccount",UserAccount);
        List<User> userList = this.list(queryWrapper);
        List<UserVo> userVos = userList.stream().map(User::getUserVo).collect(Collectors.toList());
        return userVos;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        if (Objects.isNull(userId) || userId <= 0) {
            return false;
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("isDelete",1).eq("id",userId);
        return this.update(updateWrapper);
    }

    @Override
    public UserVo getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        LOG.info(session.toString());
        if (session == null) {
            throw BusinessException.getUserNotLogin();
        } else {

            UserVo userVo = (UserVo) session.getAttribute(UserContant.LOGIN_STATUS);
            if (userVo == null) {
                throw BusinessException.getUserNull();
            } else {
                Long userId = userVo.getId();
                User currentUser = this.getById(userId);
                UserVo currentUserVo = User.getUserVo(currentUser);
                LOG.info("user:{}",currentUserVo);
                return currentUserVo;
            }
        }
    }

}




