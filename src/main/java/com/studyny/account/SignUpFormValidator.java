package com.studyny.account;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
* 회원가입 커스텀 검증
* @author nyju
* @since 2021-01-13 오후 11:19
**/
@Component
@RequiredArgsConstructor // private final 필드, @NonNull 필드에 대해 생성자 만들어줌
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;
    // 스프링 4.2 이후부터는 Autowired가 없어도 빈으로 주입받을 수 있음

    @Override // 어떤 타입의 인스턴스를 검증할지 정의
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override // 무엇을 검증할지 정의
    public void validate(Object object, Errors errors) {
        // email, nickname 중복 여부
        SignUpForm signUpForm = (SignUpForm) object;
        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다."); // 다국화 없이
        }

        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
    }
}
