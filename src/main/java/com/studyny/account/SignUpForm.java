package com.studyny.account;

import lombok.Data;

/**
* 회원가입 정보
* @author nyju
* @since 2021-01-11 오후 11:43
**/
@Data
public class SignUpForm {

    private String nickname;

    private String email;

    private String password;

}
