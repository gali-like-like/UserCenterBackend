package com.galilikelike.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    private final static BusinessException USER_NULL = new BusinessException("用户不存在");
    private final static BusinessException PASSWORD_ERROR = new BusinessException("密码错误");
    private final static BusinessException USER_NOT_LOGIN = new BusinessException("用户未登录");
    private final static BusinessException USER_EXISTS = new BusinessException("用户已存在,无法注册");
    private final static BusinessException USER_INFO_EXPIRE = new BusinessException("用户信息已过期");
    public static BusinessException getUserNull() {
        return USER_NULL;
    }
    public static BusinessException getPasswordError() {
        return PASSWORD_ERROR;
    }
    public static BusinessException getUserNotLogin() {
        return USER_NOT_LOGIN;
    }
    public static BusinessException getUserExists() {
        return USER_EXISTS;
    }
    public static BusinessException getUserInfoExpire() {
        return USER_INFO_EXPIRE;
    }

}
