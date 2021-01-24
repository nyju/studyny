package com.studyny.settings;

import com.studyny.WithAccount;
import com.studyny.account.AccountRepository;
import com.studyny.account.AccountService;
import com.studyny.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {
    // 인증된 사용자가 있는 상태에서 프로필 수정 테스트 -> 인증된 사용자 정보가 있어야함


    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    // @WithUserDetails(value ="nyju") -> beforeEach 전에 실행되기 때문에 안됨.
    // @WithUserDetails(value ="nyju", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    // -> 시큐리티컨텍스트를 설정하는 위치를 지정하는 기능. 비포다음 Test코드 바로 직전 실행하도록..
    // But.. 현재 버그가 있어서 안됨.. 여전히 before실행하기 전에 실행되기 때문에 테스트가 실패됨..
    // 그래서 @WithSecurityContext 사용 -> 확장할 수 있는 기능 -> @WithAccount 생성

    //인증된 사용자면 접근할 수 있는 URL이기 때문에 인증된 사용자 정보를 제공해줘야 한다.
    @WithAccount("nyju")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }



    @WithAccount("nyju")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account nyju = accountRepository.findByNickname("nyju");
        assertEquals(bio, nyju.getBio());
    }

    @WithAccount("nyju")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 너무나도 길게 소개를 수정하는 경우. ";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account keesun = accountRepository.findByNickname("nyju");
        assertNull(keesun.getBio());
    }

}