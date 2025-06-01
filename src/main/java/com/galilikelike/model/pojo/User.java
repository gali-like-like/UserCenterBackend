package com.galilikelike.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.galilikelike.model.vo.UserSimpleVo;
import com.galilikelike.model.vo.UserVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @TableName users
 */
@TableName(value ="users")
@Data
public class User {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 0表示普通用户,1表示管理员
     */
    private Short userRole;

    /**
     * 0表示正常,1表示封号
     */
    private Short userStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 0表示没有,1表示已经删除了
     */
    @TableLogic
    private Short isDelete;

    public static UserSimpleVo getUserSimpleVo(User user) {
        UserSimpleVo userSimpleVo = new UserSimpleVo();
        userSimpleVo.setUserName(user.getUserName());
        userSimpleVo.setAvatarUrl(user.getAvatarUrl());
        userSimpleVo.setId(user.getId());
        return userSimpleVo;
    }

    public static UserVo getUserVo(User user) {
        UserVo userVo = new UserVo();
        userVo.setId(user.getId());
        userVo.setUserName(user.getUserAccount());
        userVo.setUserAccount(user.getUserAccount());
        userVo.setUserPassword(user.getUserPassword());
        userVo.setAvatarUrl(user.getAvatarUrl());
        userVo.setUserRole(user.getUserRole()==1?"管理员":"普通用户");
        userVo.setUserStatus(user.getUserStatus()==0?"正常":"封号");
        userVo.setCreateTime(user.getCreateTime());
        String phone = user.getPhone();
        if (Objects.isNull(phone)) {
            userVo.setHiddenPhone(phone);
        } else {
            String hiddenPhone = phone.replace(phone.substring(3, 7), "****");
            userVo.setHiddenPhone(hiddenPhone);
        }
        String email = user.getEmail();
        if (Objects.isNull(email) || ( Objects.nonNull(email) && email.isEmpty()))
        {
            userVo.setHiddenEmail(email);
        } else {
            int count = email.lastIndexOf("@");
            String hiddenEmail = email.replace(email.substring(0, count), "*".repeat(count + 1));
            userVo.setHiddenEmail(hiddenEmail);
        }
        return userVo;
    }
}
