package com.galilikelike.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.galilikelike.Utils.UserInfoHold;
import com.galilikelike.contant.UserContant;
import com.galilikelike.model.dto.ConditionQuery;
import com.galilikelike.model.dto.PasswordDto;
import com.galilikelike.model.dto.UserBaseDto;
import com.galilikelike.model.pojo.User;
import com.galilikelike.model.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


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

    List<User> selectUserByCondition(ConditionQuery query);

    Integer totalByCondition(ConditionQuery query);

    default Boolean changeStatus(String userAccount) {
        if (StringUtils.isNotEmpty(userAccount)) {
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("userStatus",1).eq("userAccount",userAccount);
            this.update(updateWrapper);
            return true;
        }
        return false;
    }

    void edit(UserBaseDto userVo);

    @Select("select phone from users where userAccount = #{userAccount}")
    String showPhone(String userAccount);

    @Select("select email from users where userAccount = #{userAccount}")
    String showEmail(String userAccount);

    default void updateUserPassword(PasswordDto passwordDto,String userAccount) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("userPassword",passwordDto.getNewPassword()).eq("userAccount",userAccount);
        this.update(updateWrapper);
    };

    @Select("select * from users where userAccount = #{account}")
    User selectUserByUserAccount(String account);
}




