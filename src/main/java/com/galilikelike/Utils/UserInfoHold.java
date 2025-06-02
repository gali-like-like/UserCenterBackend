package com.galilikelike.Utils;

import com.galilikelike.model.vo.UserVo;

public class UserInfoHold {

    private static ThreadLocal<String> userHold = new ThreadLocal<>();

    public static ThreadLocal<String> getUserHold() {
        return userHold;
    }

}
