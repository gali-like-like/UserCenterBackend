package com.galilikelike.service.impl;

import com.galilikelike.mapper.UserMapper;
import com.galilikelike.model.dto.ConditionQuery;
import com.galilikelike.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class AsyncService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PlatformTransactionManager txManager;

    private <T> T doInTransaction(Supplier<T> supplier) {
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        try {
            T result = supplier.get();
            txManager.commit(status);
            return result;
        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
    }

    public CompletableFuture<List<User>> selectUserByCondition(ConditionQuery query) {
        return CompletableFuture.supplyAsync(() -> doInTransaction(() -> userMapper.selectUserByCondition(query)));
    }

    public CompletableFuture<Integer> totalByCondition(ConditionQuery query) {
        return CompletableFuture.supplyAsync(() -> doInTransaction(() -> userMapper.totalByCondition(query)));
    }
}
