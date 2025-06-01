package com.galilikelike.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    OK(200000, "成功"),
    FAIL(500000,"错误"),
    PARAM_ERROR(500001,"参数错误"),
    NO_AUTH(500002,"无权限"),
    SERVER_ERROR(500003,"服务器错误"),
    TIMEOUT_ERROR(500004,"处理超时");
    private Integer code;
    private String message;
    private ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
