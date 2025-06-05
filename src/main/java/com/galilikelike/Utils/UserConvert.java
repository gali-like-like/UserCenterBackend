package com.galilikelike.Utils;

import cn.hutool.core.bean.BeanUtil;
import com.galilikelike.model.pojo.User;
import com.galilikelike.model.vo.UserVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserConvert {

    public static UserVo convertUserVo(User user) {
        UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);
        userVo.setUserStatus(user.getUserStatus() == 0?"正常":"封号");
        String phone = user.getPhone();
        String email = user.getEmail();
        userVo.setUserStatus(user.getUserStatus() == 0?"正常":"封号");
        userVo.setUserRole(user.getUserRole() == 0?"普通用户":"管理员");
        if (user.getAvatarUrl() != null) {
            String realHeader = CosUtils.generatePresignedDownloadUrlWithOverrideResponseHeader(user.getAvatarUrl());
            userVo.setAvatarUrl(realHeader);
        }
        if (phone != null) {
            int hiddenLength = phone.length() - 4;
            String hiddenPhone = phone.replaceAll("^(\\d{2}).+(\\d{2})$", String.format("$1%s$2", "*".repeat(hiddenLength)));
            userVo.setHiddenPhone(hiddenPhone);
        }
        if (email != null) {
            int hideenLength = email.length();
            String hiddenEmail = email.replaceAll(".+", "*".repeat(hideenLength));
            userVo.setHiddenEmail(hiddenEmail);
        }
        log.info("userVo:{}", userVo);
        return userVo;
    }
}
