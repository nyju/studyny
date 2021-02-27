package com.studyny.domain;

import com.studyny.account.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;



@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})
// findByPath 메서드를 사용할때 무조건 다 필요하도록 eagar로 한번에 가져오도록 한다. 여러번 select 되지 않도록
@NamedEntityGraph(name = "Study.withTagsAndManagers", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")})
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
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

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
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime; // 스터디 공개한 시간

    private LocalDateTime closedDateTime; // 스터디 종료 시간

    private LocalDateTime recruitingUpdatedDateTime; // 스터디 모집 시간

    private boolean recruiting; // 현재 모집중 여부

    private boolean published;  // 공개 여부

    private boolean closed; // 종료여부

    private boolean useBanner; // 배너사용 여부

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);

    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }

    public String getImage() {
        return image != null ? image : "/images/default_banner.png";
    }
}
