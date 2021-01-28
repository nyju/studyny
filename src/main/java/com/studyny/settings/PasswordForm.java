package com.studyny.settings;

import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
* 비밀번호 변경 폼의 정보를 위한 클래스
* @author nyju
* @since 2021-01-28 오후 9:17
**/
@Data
public class PasswordForm {

    @Length(min=8, max = 50)
    private String newPassword;

    @Length(min=8, max = 50)
    private String newPasswordConfirm;
}
