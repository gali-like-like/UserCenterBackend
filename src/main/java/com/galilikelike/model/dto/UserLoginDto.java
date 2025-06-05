package com.galilikelike.model.dto;

import com.galilikelike.groups.Login;
import com.galilikelike.groups.Reset;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserLoginDto {

    @NotNull(message = "账号不能为空")
    @Pattern(regexp = "\\w{6,}",message = "账号不能包含特殊字符，且长度至少6个字符",groups = {Reset.class, Login.class})
    private String userAccount;

    @NotNull(message = "密码不能为空")
    @Pattern(regexp = "\\w{6,}",message = "密码不能包含特殊字符，且长度至少6个字符",groups = {Login.class})
    private String userPassword;
}

