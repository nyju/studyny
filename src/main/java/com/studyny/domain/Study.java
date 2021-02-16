package com.studyny.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {
   // Study 에서 Account 로 @ManyToMany 단방향 관계 두 개 (managers, members) -> Account 에서 Study 를 많이 참조한다면 나중에 변경 가능
   // Study 에서 Zone 으로 @ManyToMany 단방향 관계
   // Study 에서 Tag 로 @ManyToMany 단방향 관계
    
    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers;

    @ManyToMany
    private Set<Account> members;

    @Column(unique = true)
    private String path; // url 경로이기 때문에 unique

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;
    // varchar255 안에 못담으므로 Lob
    // Lob 기본값 EAGER 이지만 명시적 설정

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags;

    @ManyToMany
    private Set<Zone> zones;

    private LocalDateTime publishedDateTime; // 스터디 공개한 시간

    private LocalDateTime closedDateTime; // 스터디 종료 시간

    private LocalDateTime recruitingUpdatedDateTime; // 스터디 모집 시간

    private boolean recruiting; // 현재 모집중 여부

    private boolean published;  // 공개 여부

    private boolean closed; // 종료여부

    private boolean useBanner; // 배너사용 여부

}
