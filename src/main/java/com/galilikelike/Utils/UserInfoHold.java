package com.galilikelike.Utils;

import com.galilikelike.model.pojo.User;
import com.galilikelike.model.vo.UserVo;

public class UserInfoHold {

    private static ThreadLocal<User> userHold = new ThreadLocal<>();

    public static ThreadLocal<User> getUserHold() {
        return userHold;
    }

}
