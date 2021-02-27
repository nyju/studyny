package com.studyny.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
* 계정 정보
* @author nyju
* @since 2021-01-11 오후 11:49
**/
@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private  String nickname;

    private String password;

    private boolean emailVerified; // 이메일 인증 여부

    private String emailCheckToken; // 이메일 검증에 사용할 토큰값

    private LocalDateTime joinedAt; // 가입날짜

    private LocalDateTime emailCheckTokenGeneratedAt; // 이메일 확인 토큰 생성시간

    private String bio; // 자기소개

    private String url; //url

    private String occupation; // 직업

    private String location; // 지역

    @Lob @Basic(fetch = FetchType.EAGER) // varchar255사이즈보다 커질수 있기 때문에 Lob으로 설정
    private String profileImage;

    private boolean studyCreatedByEmail; // 스터디생성 이메일 알림 여부

    private boolean studyCreatedByWeb = true; // 스터디생성 웹 알림 여부

    private boolean studyEnrollmentResultByEmail; // 스터디 가입신청 이메일 알림 여부

    private boolean studyEnrollmentResultByWeb = true; // 스터디 가입신청 웹 알림 여부

    private boolean studyUpdatedByEmail; // 스터디 갱신정보 이메일 알림 여부

    private boolean studyUpdatedByWeb = true; // 스터디 갱신정보 웹 알림 여부

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString(); // 랜덤 UUID 사용
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    public boolean isManagerOf(Study study) {
        return study.getManagers().contains(this);
    }
}
