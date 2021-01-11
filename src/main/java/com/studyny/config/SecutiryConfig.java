package com.studyny.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
* Secutiry Config 설정
* @author nyju
* @since 2021-01-12 오전 12:12
**/
@Configuration
@EnableWebSecurity // 스프링 시큐리티 사용을 위한 선언
public class SecutiryConfig extends WebSecurityConfigurerAdapter {

    @Override // 원하는 특정요청은 인증체크를 하지않도록 오버라이딩
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
               .mvcMatchers("/","/login","/sign-up","/check-email","/check-email-token",
                       "/email-login","/check-email-login", "/login-link").permitAll() // 전체 접근 허용
               .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll() // 프로필은 get 요청만 허용
               .anyRequest().authenticated();
    }


    @Override // 아이콘 안나오는 현상 -> static 리소스들은 시큐리티 필터 적용하지 않도록 설정
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
