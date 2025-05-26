package com.galilikelike.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UserDto {

    /**
     * 注册时用的类型
     */
    @NotNull(message = "账号不能为空")
    @Pattern(regexp = "\\w{6,}",message = "账号不能包含特殊字符，且长度至少6个字符")
    private String userAccount;

    @NotNull(message = "密码不能为空")
    @Pattern(regexp = "\\w{6,}",message = "密码不能包含特殊字符，且长度至少6个字符")
    private String userPassword;

    @NotNull(message = "验证码不能为空")
    private String code;
}
