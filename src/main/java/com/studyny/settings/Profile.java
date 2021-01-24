package com.studyny.settings;


import com.studyny.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class Profile {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    // 컨트롤러에서 Profile 로 바인딩을 받을 때 NullpointerException 에러 발생
    // Profile 생성자가 실행될때 Account 가 없기 때문
    // @NoArgsConstructor를 쓰거나 기본생성자 Profile() 를 생성해줘야 함
    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}