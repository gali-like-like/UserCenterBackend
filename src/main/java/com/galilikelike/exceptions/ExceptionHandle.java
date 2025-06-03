package com.galilikelike.exceptions;

import com.galilikelike.common.BusinessException;
import com.galilikelike.common.ErrorCode;
import com.galilikelike.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class ExceptionHandle {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandle.class);

    @Order(2)
    @ExceptionHandler(BusinessException.class)
    public Result handleBusine(BusinessException e) {
        log.error(e.getMessage());
        return Result.fail(e.getMessage(),e.getMessage());
    }

    @Order(2)
    @ExceptionHandler(TimeoutException.class)
    public Result handleTimeout(TimeoutException e) {
        log.error(e.getMessage());
        return Result.fail(ErrorCode.TIMEOUT_ERROR);
    }

    @Order(2)
    @ExceptionHandler(DataAccessException.class)
    public Result handleDataAccess(DataAccessException e) {
        log.error(e.getMessage());
        return Result.fail(ErrorCode.SERVER_ERROR);
    }

    @Order(2)
    // 处理单个参数校验失败
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        log.error(errors.toString());
        return Result.fail(ErrorCode.PARAM_ERROR,String.join(",",errors.values()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error(errors.toString());
        return Result.fail(ErrorCode.PARAM_ERROR,String.join(",",errors.values()));
    }

    @Order(10000)
    @ExceptionHandler(Exception.class)
    public Result handleOther(Exception e) {
        log.error(e.getMessage());
        return Result.fail(ErrorCode.SERVER_ERROR);
    }

}
