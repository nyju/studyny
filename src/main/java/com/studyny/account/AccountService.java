package com.studyny.account;

import com.studyny.domain.Account;
import com.studyny.domain.Tag;
import com.studyny.domain.Zone;
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
import java.util.Optional;
import java.util.Set;


/**
 * Account Service 클래스
 *
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
        
/*      saveNewAccount() 에서 생성하는것으로 변경
        newAccount.generateEmailCheckToken(); // 이메일 체크 토큰 생성
        // saveNewAccount 후에 detached 상태가 되기 때문에 토큰값이 저장이 안됨
        // persist 상태 유지를 위해 @Transactional 을 붙여야 함*/

        sendSignUpConfirmEmail(newAccount); // 이메일 전송
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();

/*      ModelMapper로 변경
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build(); // builder를 사용하면 기본값이 있더라도 명시적으로 셋팅을 해줘야 한다.*/

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

    public void updateNickname(Account account, String nickname) {
        modelMapper.map(nickname, account);
        accountRepository.save(account);
    }

    public void addTag(Account account, Tag tag) {
        // account 가 detached 상태에기 때문에 toMany연관관계에 있는 tag도 lazy로딩 불가능하다.
        // account 를 먼저 가져와야 한다.
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag)); // 값이 있으면 실행, 없으면 아무일도 안일어남

        // accountRepository.getOne() 은 lazy로딩. 필요한 순간에만 읽어옴. 경우에 따라 효율적
        // getOne vs findById 비교
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
    }

    public Account getAccount(String nickname) {
        Account account = accountRepository.findByNickname(nickname);
        if (account == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        return account;
    }
}
