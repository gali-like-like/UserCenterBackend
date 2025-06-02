package com.galilikelike.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galilikelike.Utils.UserInfoHold;
import com.galilikelike.common.BusinessException;
import com.galilikelike.common.Result;
import com.galilikelike.model.vo.UserVo;
import com.galilikelike.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
@Slf4j
@Order(1)
@Component
public class CheckFilter implements Filter {

    @Autowired
    private UserService userService;

    private ArrayList<String> noCheckUris = new ArrayList<>();
    {
        noCheckUris.add("/api/user/login");
        noCheckUris.add("/api/user/regedit");
        noCheckUris.add("/api/user/code");
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestMethod = request.getMethod();
        if (requestMethod.equalsIgnoreCase("options")) {
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        String uri = request.getRequestURI();
        log.info("method:{},uri:{}",requestMethod,uri);
        if (!noCheckUris.contains(uri)) {
            try {
                UserVo currentUser = userService.getCurrentUser(request);
                UserInfoHold.getUserHold().set(currentUser.getUserAccount());
                filterChain.doFilter(servletRequest,servletResponse);
            } catch (Exception e) {
                servletResponse.setContentType("application/json;charset=UTF-8");
                Result fail = Result.fail(BusinessException.getUserInfoExpire());
                ObjectMapper objectMapper = new ObjectMapper();
                String failJson = objectMapper.writeValueAsString(fail);
                servletResponse.getWriter().write(failJson);
            }
        } else {
            filterChain.doFilter(servletRequest,servletResponse);
        }
    }

    public void destroy() {
        UserInfoHold.getUserHold().remove();
    }
}
