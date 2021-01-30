package com.studyny.account;

import com.studyny.domain.Account;
import com.studyny.settings.form.Notifications;
import com.studyny.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.validation.Valid;
import java.util.List;


/**
 * Account Service 클래스
 * @author nyju
 * @since 2021-01-16 오후 11:50
 **/
@Service //데이터 변경은 서비스 계층으로 위임해서 트랜잭션안에서 처리한다. 데이터 조회는 리파지토리 또는 서비스를 사용한다.
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    //private final AuthenticationManager authenticationManager; 사용하려면 스프링시큐리티 설정을 다르게 해줘야함


    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm); // 새로운 Account 생성
        newAccount.generateEmailCheckToken(); // 이메일 체크 토큰 생성
        // saveNewAccount 후에 detached 상태가 되기 때문에 토큰값이 저장이 안됨
        // persist 상태 유지를 위해 @Transactional 을 붙여야 함

        sendSignUpConfirmEmail(newAccount); // 이메일 전송
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("StudyNy, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {

        System.out.println("##### login");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(token);

//        아래가 정석적인 방법. 위와 같은 기능을 함
//        정석적으로 하려면 인코딩된 비밀번호가 아닌 플레인텍스트로 받은 비밀번호를 사용하여야 함.
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
//        Authentication authentication = authenticationManager.authenticate(token);
//        SecurityContext context =SecurityContextHolder.getContext();
//        context.setAuthentication(authentication);
    }

    @Transactional(readOnly = true) // 데이터를 변경하는것이 아닌 읽는 것이기 때문에
    @Override // 스프링 시큐리티에서 /login, /logout 에 대한 처리를 위해 구현해야함
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account);
        // TODO 프로필 이미지
        accountRepository.save(account); // acount가 detached 상태이기 때문. completeSignUp의 account 와 상태 다름


//        account.setUrl(profile.getUrl()); modelMapper 로 간단히 구현
//        account.setOccupation(profile.getOccupation());
//        account.setLocation(profile.getLocation());
//        account.setBio(profile.getBio());

    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);

//        account.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb()); modelMapper 로 간단히 구현
//        account.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
//        account.setStudyUpdatedByWeb(notifications.isStudyUpdatedByWeb());
//        account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
//        account.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());
//        account.setStudyEnrollmentResultByWeb(notifications.isStudyEnrollmentResultByWeb());

    }
}
