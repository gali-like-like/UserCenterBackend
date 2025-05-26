package com.galilikelike.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    private String description;

    private Object data;

    private Result(Integer code, String msg, String description, Object data) {
        this.code = code;
        this.msg = msg;
        this.description = description;
        this.data = data;
    }

    public static Result success(Object data) {
        return new Result(200,"ok","成功",data);
    }

    public static Result success(String msg,String description,Object data) {
        return new Result(200,msg,description,data);
    }

    public static Result fail(String msg,String description) {
        return new Result(500,msg,description,null);
    }

    public static Result fail(ErrorCode errorCode,String description) {
        return new Result(errorCode.getCode(), errorCode.getMessage(),description,null);
    }

    public static Result fail(ErrorCode errorCode) {
        return new Result(errorCode.getCode(),errorCode.getMessage(), errorCode.getMessage(),null);
    }

    public static Result fail(BusinessException exception) {
        return new Result(500,exception.getMessage(),null,exception);
    }
}
