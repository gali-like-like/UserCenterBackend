package com.galilikelike.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galilikelike.Utils.UserInfoHold;
import com.galilikelike.common.BusinessException;
import com.galilikelike.common.ErrorCode;
import com.galilikelike.common.Result;
import com.galilikelike.model.pojo.User;
import com.galilikelike.model.vo.UserVo;
import com.galilikelike.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
@Slf4j
@Order(1)
@WebFilter(urlPatterns = "/*")
public class CheckFilter implements Filter {

    private UserService userService;
    private String[] noAuthArray = {"/api/user/login","/api/user/regedit","/api/user/code","/api/user/reset"};

    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.userService = webApplicationContext.getBean(UserService.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (request.getMethod().equalsIgnoreCase("options")) {
            filterChain.doFilter(request,response);
        } else {
            log.info("{} {}",request.getMethod(),request.getRequestURI());
            int index = Arrays.binarySearch(noAuthArray, request.getRequestURI());
            if (index >= 0) {
                filterChain.doFilter(request,response);
                return;
            }
            try {
                User currentUser = userService.getCurUser(request);
                UserInfoHold.getUserHold().set(currentUser);
                filterChain.doFilter(request,response);
            } catch (BusinessException e) {
                Result fail = Result.fail(e);
                failHandle(response,fail);
            } catch (Exception e) {
                Result fail = Result.fail(ErrorCode.SERVER_ERROR);
                failHandle(response,fail);
            }
        }
    }

    public void failHandle(HttpServletResponse response,Result fail) throws IOException {
        // 必须添加的响应头配置
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); // 解决中文乱码
        ObjectMapper mapper = new ObjectMapper();
        String failJson = mapper.writeValueAsString(fail);
        response.getWriter().write(failJson);
    }

    public void destroy() {
        UserInfoHold.getUserHold().remove();
    }
}
