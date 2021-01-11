package com.studyny.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
* AccountController
* @author nyju
* @since 2021-01-12 오전 12:14
**/
@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute(new SignUpForm());
        // model.addAttribute("signUpForm", new SignUpForm());  클래스이름의 camel case인 경우 생략 가능
        return "account/sign-up";
    }
}
