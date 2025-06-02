package com.galilikelike.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDto implements Serializable {

    private String oldPassword;

    @NotNull(message = "密码不能为空!")
    @Pattern(regexp = "\\w{6,}",message = "密码不能包含特殊字符，且长度至少6个字符")
    private String newPassword;

}
