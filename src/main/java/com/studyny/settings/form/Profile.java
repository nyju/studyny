package com.studyny.settings.form;


import com.studyny.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
//@NoArgsConstructor
public class Profile {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    private String profileImage;

    // 컨트롤러에서 Profile 로 바인딩을 받을 때 NullpointerException 에러 발생
    // Profile 생성자가 실행될때 Account 가 없기 때문
    // @NoArgsConstructor를 쓰거나 기본생성자 Profile() 를 생성해줘야 함

    // ModelMapper 사용하기 -> 빈이 아니기 때문에 AccountService 처럼 다른 빈(ModelMapper)을 주입 받을 수 없음
    /// SettingController 에서 모델매퍼 이용해서 셋팅하도록 변경
//    public Profile(Account account) {
//        this.bio = account.getBio();
//        this.url = account.getUrl();
//        this.occupation = account.getOccupation();
//        this.location = account.getLocation();
//    }
}