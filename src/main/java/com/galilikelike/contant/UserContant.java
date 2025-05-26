package com.galilikelike.contant;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class UserContant {
    public final static String LOGIN_STATUS = "userLoginStatus";
    public final static String SALT = "galiNB";
    public final static String MD5_RESET_PD = DigestUtils.md5DigestAsHex((SALT + "123456").getBytes(StandardCharsets.UTF_8));
}
