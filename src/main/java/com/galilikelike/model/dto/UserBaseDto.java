package com.galilikelike.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hiddenPhone;

    private String hiddenEmail;

    private String userName;

    private String userAccount;

}
