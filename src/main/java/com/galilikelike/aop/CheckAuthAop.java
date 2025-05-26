package com.galilikelike.aop;

import com.galilikelike.annos.PreAuth;
import com.galilikelike.contant.UserContant;
import com.galilikelike.model.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class CheckAuthAop {

    @Pointcut("@annotation(com.galilikelike.annos.PreAuth)")
    public void auth() {

    }

    @Around("auth()")
    public Object checkAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PreAuth annotation = method.getAnnotation(PreAuth.class);
        Object request = joinPoint.getArgs()[1];
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession httpSession = httpServletRequest.getSession();
        Object userObj = httpSession.getAttribute(UserContant.LOGIN_STATUS);
        UserVo userVo = (UserVo) userObj;
        String nowUserRole = userVo.getUserRole();
        String userRole = annotation.userRole();
        if (nowUserRole.equals(userRole)) {
            return joinPoint.proceed();
        } else {
            return null;
        }
    }
}
