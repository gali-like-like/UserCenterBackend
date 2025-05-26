package com.galilikelike.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 手机号
     */
    private String hiddenPhone;

    /**
     * 邮箱
     */
    private String hiddenEmail;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 0表示普通用户,1表示管理员
     */
    private String userRole;

    /**
     * 0表示正常,1表示封号
     */
    private String userStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getHiddenPhone() {
        return hiddenPhone;
    }

    public void setHiddenPhone(String hiddenPhone) {
        this.hiddenPhone = hiddenPhone;
    }

    public String getHiddenEmail() {
        return hiddenEmail;
    }

    public void setHiddenEmail(String hiddenEmail) {
        this.hiddenEmail = hiddenEmail;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", hiddenPhone='" + hiddenPhone + '\'' +
                ", hiddenEmail='" + hiddenEmail + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", userRole='" + userRole + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
