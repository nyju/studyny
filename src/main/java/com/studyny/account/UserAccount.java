package com.studyny.account;

import com.studyny.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
* Account를 직접 참조하기 위한 Adapter클래스
* @author nyju
* @since 2021-01-19 오후 11:23
**/
@Getter // 시큐리티에서 다루는 유저정보와 도메인에서 다루는 유저정보 사이의 갭을 위한 클래스
public class UserAccount extends User { // UserDetails를 구현하는 User 클래스를 상속

    private Account account;

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
