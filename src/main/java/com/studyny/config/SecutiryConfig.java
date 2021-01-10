package com.studyny.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecutiryConfig extends WebSecurityConfigurerAdapter {

    @Override // 원하는 특정요청은 인증체크를 하지않도록 오버라이딩
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
               .mvcMatchers("/","/login","/sign-up","/check-email","/check-email-token",
                       "/email-login","/check-email-login", "/login-link").permitAll()
               .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll() // 프로필은 get 요청만 허용
               .anyRequest().authenticated();
    }
}
