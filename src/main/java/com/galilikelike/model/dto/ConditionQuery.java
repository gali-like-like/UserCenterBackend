package com.galilikelike.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class ConditionQuery {

    private String userName;

    private String userAccount;

    @NotNull(message = "用户状态是必填项")
    @Min(value = 0,message = "状态不符合规格")
    @Max(value = 2,message = "状态不符合规格")
    private Byte userStatus;

    @NotNull(message = "当前页不能为空")
    @Min(value = 1,message = "必须从第一页起步")
    private Integer current;

    @NotNull(message = "分页大小不能为空")
    @Min(value = 10,message = "分页大小不能小于10页")
    private Integer pageSize;

    public ConditionQuery() {
    }

    public ConditionQuery(String userName, String userAccount, Byte userStatus, Integer current, Integer pageSize) {
        this.userName = userName;
        this.userAccount = userAccount;
        this.userStatus = userStatus;
        this.current = current;
        this.pageSize = pageSize;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public Byte getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Byte userStatus) {
        this.userStatus = userStatus;
    }

    public @NotNull(message = "当前页不能为空") @Min(value = 1, message = "必须从第一页起步") Integer getCurrent() {
        return current;
    }

    public void setCurrent(@NotNull(message = "当前页不能为空") @Min(value = 1, message = "必须从第一页起步") Integer current) {
        this.current = current;
    }

    public @NotNull(message = "分页大小不能为空") @Min(value = 10, message = "分页大小不能小于10页") Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(@NotNull(message = "分页大小不能为空") @Min(value = 10, message = "分页大小不能小于10页") Integer pageSize) {
        this.pageSize = pageSize;
    }
}
