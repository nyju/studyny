package com.studyny.config;

import com.studyny.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
* Secutiry Config 설정
* @author nyju
* @since 2021-01-12 오전 12:12
**/
@Configuration
@EnableWebSecurity // 스프링 시큐리티 사용을 위한 선언
@RequiredArgsConstructor
public class SecutiryConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override // 원하는 특정요청은 인증체크를 하지않도록 오버라이딩
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
               .mvcMatchers("/","/login","/sign-up", "/check-email-token",
                       "/email-login","/check-email-login", "/login-link", "/h2-console/*", "/h2-console").permitAll() // 전체 접근 허용
               .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll() // 프로필은 get 요청만 허용
               .anyRequest().authenticated();


        http.formLogin() // 커스텀한 로그인페이지 URL. 설정하지 않으면 시큐리티기본 로그인페이지가 보임
                .loginPage("/login").permitAll(); // GET, POST 모두 동일한 경로를 사용

        http.logout() // 로그아웃 했을때 이동할 URL
                .logoutSuccessUrl("/");


        http.rememberMe() //Username, 토큰(랜덤, 매번 바뀜), 시리즈(랜덤, 고정) 세 가지 정보 이용
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository());

    }

    @Bean // 토큰 레파지토리 구현체
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }


    @Override // 아이콘 안나오는 현상 -> static 리소스들은 시큐리티 필터 적용하지 않도록 설정
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
