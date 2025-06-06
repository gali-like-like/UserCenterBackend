package com.galilikelike.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.galilikelike.Utils.CalcFileSum;
import com.galilikelike.Utils.CosUtils;
import com.galilikelike.Utils.UserConvert;
import com.galilikelike.Utils.UserInfoHold;
import com.galilikelike.common.BusinessException;
import com.galilikelike.groups.Login;
import com.galilikelike.groups.Reset;
import com.galilikelike.mapper.UserMapper;
import com.galilikelike.model.dto.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.galilikelike.contant.UserContant;
import org.springframework.web.multipart.MultipartFile;

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
    public UserVo login(@Validated(Login.class) UserLoginDto userLoginDto, HttpServletRequest request) {
        String userAccount = userLoginDto.getUserAccount();
        User queryUser = this.getOne(new QueryWrapper<User>().eq("userAccount",userAccount));
        if (Objects.isNull(queryUser)) {
            throw BusinessException.getUserNull();
        } else {
            if (queryUser.getUserStatus() == 1) {
                throw new BusinessException("用户被封号,无法登录");
            } else if(queryUser.getIsDelete() == (short)1) {
                throw new BusinessException("用户已注销,无法登录");
            }
            String queryUserPd = queryUser.getUserPassword();
            String inputPassword = userLoginDto.getUserPassword();
            String inputEncrypt = DigestUtils.md5DigestAsHex((UserContant.SALT+inputPassword).getBytes(StandardCharsets.UTF_8));
            if (!queryUserPd.equals(inputEncrypt)) {
                throw new BusinessException("密码错误");
            } else {
                UserVo userVo = UserConvert.convertUserVo(queryUser);
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
        List<UserVo> userVos = users.stream().map(UserConvert::convertUserVo).collect(Collectors.toList());
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
    public Boolean resetPassword(@Validated(Reset.class) UserLoginDto loginDto) {
        String account = loginDto.getUserAccount();
        Boolean result = userMapper.resetPasswordByAccount(account);
        return result;
    }

    @Override
    public String createCode(HttpServletRequest request) {
// 自定义验证码内容为四则运算方式
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 40, 4, 4);
        captcha.setGenerator(new MathGenerator());
// 重新生成code
        captcha.createCode();
        String code = captcha.getCode();
        LOG.info("验证码:{},图像验证码:{}",code,captcha.getImageBase64Data());
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
    @Transactional(rollbackFor = Exception.class)
    public UserVo edit(UserVo userBaseDto) {
        User user = UserInfoHold.getUserHold().get();
        // todo 比较
        String userPhone = userBaseDto.getHiddenPhone();
        /**
         * 13245641111
         * */
        Pattern compilePhone = Pattern.compile("^(13[0-9]|14[5-9]|15[0-35-9]|16[56]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
        Matcher matcherPhone = compilePhone.matcher(userPhone);
        LOG.info("phone:{},reg:{}",compilePhone,compilePhone.toString());
        if (!matcherPhone.matches()) {
            throw new BusinessException("手机号格式错误");
        }
        String userEmail = userBaseDto.getHiddenEmail();
        Pattern compileEmail = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Matcher matcherEmail = compileEmail.matcher(userEmail);
        if (!matcherEmail.matches()) {
            throw new BusinessException("邮箱格式错误");
        }
        userMapper.edit(userBaseDto);
        user.setUserName(userBaseDto.getUserName());
        user.setPhone(userBaseDto.getHiddenPhone());
        user.setEmail(userBaseDto.getHiddenEmail());
        return UserConvert.convertUserVo(user);
    };

    public String showPhone() {
        User user = UserInfoHold.getUserHold().get();
        return user.getPhone();
    };

    public String showEmail() {
        User user = UserInfoHold.getUserHold().get();
        return user.getEmail();
    };

    public Boolean updatePassword(@Valid PasswordDto passwordDto) {
        User user = UserInfoHold.getUserHold().get();
        if (!passwordDto.getNewPassword().equals(passwordDto.getTryPassword())) {
            throw new BusinessException("确认密码和密码不一样");
        }
        String updatedPassword = passwordDto.getNewPassword();
        String encryptPassword = DigestUtils.md5DigestAsHex((updatedPassword + UserContant.SALT).getBytes(StandardCharsets.UTF_8));
        passwordDto.setNewPassword(encryptPassword);
        userMapper.updateUserPassword(passwordDto,user.getUserAccount());
        return true;
    };

    public Boolean upload(MultipartFile file,String fileHash) throws IOException {
        try {
            String fileSum = CalcFileSum.getSum(file.getBytes());
            if (!fileSum.equals(fileHash)) {
                throw new BusinessException("文件缺少内容,需要重新上传");
            }
            CosUtils.upload(file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<UserVo> searchUser(String UserAccount) {
        if(StringUtils.isNotEmpty(UserAccount)) {
            throw BusinessException.getUserNull();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("UserAccount",UserAccount);
        List<User> userList = this.list(queryWrapper);
        List<UserVo> userVos = userList.stream().map(UserConvert::convertUserVo).collect(Collectors.toList());
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

    public User getCurUser(HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session == null) {
            throw BusinessException.getUserNotLogin();
        } else {
            UserVo userVo = (UserVo) session.getAttribute(UserContant.LOGIN_STATUS);
            if (Objects.isNull(userVo)) {
                throw BusinessException.getUserNull();
            }
            return userMapper.selectUserByUserAccount(userVo.getUserAccount());
        }

    };


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
                UserVo currentUserVo = UserConvert.convertUserVo(currentUser);
                LOG.info("user:{}",currentUserVo);
                return currentUserVo;
            }
        }
    }

}




