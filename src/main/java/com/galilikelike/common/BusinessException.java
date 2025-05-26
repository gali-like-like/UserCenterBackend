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
    public static BusinessException getUserNull() {
        return USER_NULL;
    }
    public static BusinessException getPasswordError() {
        return PASSWORD_ERROR;
    }
    public static BusinessException getUserNotLogin() {
        return USER_NOT_LOGIN;
    }
}
