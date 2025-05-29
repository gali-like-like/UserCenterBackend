package com.galilikelike.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galilikelike.contant.UserContant;
import com.galilikelike.model.pojo.User;
import org.apache.ibatis.annotations.Mapper;


/**
* @author Lenovo
* @description 针对表【users】的数据库操作Mapper
* @createDate 2025-05-06 17:35:20
* @Entity generator.pojo.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

    default Boolean resetPasswordByAccount(String userAccount) {
        if (StringUtils.isNotEmpty(userAccount)) {
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("userPassword", UserContant.MD5_RESET_PD).eq("userAccount",userAccount);
            this.update(updateWrapper);
            return true;
        }
        return false;
    }

    Page<User> selectUserPage(Page<User> page);
}




