package com.galilikelike.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserLoginDto {

    @NotNull(message = "账号不能为空")
    @Pattern(regexp = "\\w{6,}",message = "账号不能包含特殊字符，且长度至少6个字符")
    private String userAccount;

    @NotNull(message = "密码不能为空")
    @Pattern(regexp = "\\w{6,}",message = "密码不能包含特殊字符，且长度至少6个字符")
    private String userPassword;

}
