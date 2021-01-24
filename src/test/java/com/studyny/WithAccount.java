package com.studyny;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountSecurityContextFacotry.class) // 시큐리티컨텍스트를 만들어줄 팩토리를 만듦
public @interface WithAccount {

    String value();

}


