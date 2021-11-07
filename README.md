# studyny


[tistory_blog 정리내용 보기](https://anjuna.tistory.com/category/Study/Spring%20JPA)


* 회원가입 폼 검증
  * JSR303 어노테이션 검증 - 값의 길이, 필수값
  * validator 커스텀 검증, @InitBinder - 중복이메일, 닉네임 여부 확인


* 회원가입 - 테스트 코드 작성
  * csrf() 토큰값 전달


* 회원가입 - 시큐리티 설정
  * WebSecurityConfigurerAdapter
  
  
* 회원가입 - 패스워드 인코더
  * 스프링 시큐리티 - PasswordEncoder
  * PasswordEncoderFactories.createDelegatingPasswordEncoder()
  * 해싱 알고리즘 - bcrypt, salt


* 회원가입 이메일 인증
  * 인증메일 발송 JavaMailSender
  * 인증 토큰값 확인


* 회원가입 : 가입완료 후 자동로그인 및 인증정보 확인
  * 스프링 시큐리티 : UsernamePasswordAuthenticationToken, SecurityContextHolder
  * 뷰에서 인증정보 확인 : 타임리프에서 제공하는 thymeleaf-extras-springsecurity5


* 현재 인증된 사용자 정보 참조
  * 스프링 시큐리티 : @AuthenticationPrincipal 사용


* 스프링 시큐리티 로그인, 로그아웃
  * 시큐리티설정, UserDetailsService 설정
  * 테스트코드 Unit 5의 @BeforeEach와 @AfterEach


* 로그인 기억하기 (RememberMe)
  * 로그인 방식
    * 로그인 방식에는 세션을 이용하는 방법과 쿠키를 이용하는 방법이 있다.
    * 세션방식은 데이터를 서버에 보관하여 안전하다는 장점이 있지만, 만료되었을때 사용할 수 없다는 단점이 있다
    * 쿠키에 인증 정보를 남겨두어 세션이 만료 되더라도
    * 쿠키에 남아있는 정보로 인증하여 로그인을 유지할 수 있다.
  * 쿠키사용하기
    * 해시 기반의 쿠키
      * username, password를 암호화(해싱)하여 쿠키게 담아둔다.
      * 쿠키를 다른 사람이 가져가면 그 계정은 탈취당한 것과 같다.
    * 조금 더 안전한 방법은?
      * 쿠키안에 랜덤한 문자열(토큰)을 만들어 같이 저장하여 매번 인증할 때마다 바꾼다.
      * Username, 토큰
      * 문제는 이 방법도 취약하다. 쿠키를 탈취 당하면 희생자는 쿠키로 인증하지 못한다.
      * 해커가 탈취한 쿠키만 유효하게 된다.
    * 조금 더 개선한 방법은?
      * Username, 토큰(랜덤, 매번 바뀜), 시리즈(랜덤, 고정)
      * 쿠키를 탈취 당한 경우, 희생자는 유효하지 않은 토큰과 유효한 시리즈와 Username으로 접속하게 된다.
      * 이 경우, 모든 토큰을 삭제하여 해커가 더이상 탈취한 쿠키를 사용하지 못하도록 방지할 수 있다.
  * RememberMe 사용하기
    * 스프링시큐리티  rememberMe() 설정
    * JdbcTokenRepositoryImpl 설정
    * persistent_logins 테이블
 
 
* 프로필 수정 테스트
  * 인증된 사용자만 접근할 수 있는 URL이므로 인증된 Authentication 정보가 필요.
  * @WithSecurityContext, @WithAccount 를 이용하여 인증된 사용자를 제공하는 커스텀 어노테이션 생성


* ModelMapper 설정
  * 객체의 프로퍼티를 다른 객체의 프로퍼티로 맵핑해주는 유틸리티
  * 토그나이저 설정



> 인프런 스프링과 JPA 기반 웹 애플리케이션 개발 강의를 듣고 실습 및 정리한 프로젝트입니다.
