package com.galilikelike.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galilikelike.Utils.UserConvert;
import com.galilikelike.model.dto.ConditionQuery;
import com.galilikelike.model.pojo.User;
import com.galilikelike.model.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@Service
public class ConditionService {

    @Autowired
    private AsyncService service;

    @Transactional
    public Page<UserVo> selectByCondition(@Valid ConditionQuery query, HttpServletRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        query.setCurrent((query.getCurrent() - 1) * query.getPageSize());
        query.setUserStatus(query.getUserStatus() == 2?null: query.getUserStatus());
        CompletableFuture<List<User>> futureUsers = service.selectUserByCondition(query);
        CompletableFuture<Integer> futureTotal = service.totalByCondition(query);
        CompletableFuture<Void> future = CompletableFuture.allOf(futureTotal, futureUsers);
        future.get(5, TimeUnit.SECONDS);
        List<User> users = futureUsers.get();
        Integer total = futureTotal.get();
        List<UserVo> userVos = users.stream().map(UserConvert::convertUserVo).toList();
        Page<UserVo> userVoPage = new Page<>(query.getCurrent(),query.getPageSize(),total);
        userVoPage.setRecords(userVos);
        return userVoPage;
    }


}
