package com.studyny.account;

import com.studyny.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * AccountController
 * @author nyju
 * @since 2021-01-12 오전 12:14
 **/
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;


    // Validator사용 시 @Valid로 검증이 필요한 객체를 가져오기 전에 수행할 검증method를 지정
    // Validator를 직접 호출하지 않고 스프링프레임워크가 호출
    @InitBinder("signUpForm") // -> 타입의 camel case를 따라감
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        // model.addAttribute("signUpForm", new SignUpForm());  클래스이름의 camel case인 경우 생략 가능
        return "account/sign-up";
    }

    @PostMapping("/sign-up") //  JSR-303 Valid 검사
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up"; //에러가 있으면 다시 폼으로
        }
        accountService.processNewAccount(signUpForm);

        return "redirect:/";
    }
}
