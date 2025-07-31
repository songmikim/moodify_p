package xyz.moodf.email;


import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("이메일 인증 코드 발급 및 검증 테스트")
    void sendVerifyEmailTest() throws Exception {
        /* 인증 코드 발급 테스트 S */
        HttpSession session = mockMvc.perform(get("/api/email/verify?email=chtkdldjs43@naver.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession();
        Integer authNum = (Integer)session.getAttribute("EmailAuthNum");
        /* 인증 코드 발급 테스트 E */

        /* 인증 코드 검증 테스트 S */
        mockMvc.perform(get("/api/email/auth_check?authNum=" + authNum.intValue()))
                .andDo(print());
        /* 인증 코드 검증 테스트 E */
    }

    @Test
    void test1() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // API 요청 처리 (email 파라미터는 SQ에 심어서 요청 | 테스트를 위해 | 지정된 이메일로 인증 이메일 발급)
        mockMvc.perform(get("/api/email/verify").param("email","chtkdldjs43@naver.com").session(session))
                .andDo(print()) // 요청/응답 정보를 콘솔에 출력
                .andExpect(status().isOk()) // 응답 상태 점검 | 2xx
                .andReturn().getRequest().getSession(); // 실행 결과 반환 -> getSession : 테스트로 생성된 세션 정보 가져오기

        Integer authNum = (Integer) session.getAttribute("EmailAuthNum");

        // API 요청 처리 (인증번호 검증)
        mockMvc.perform(get("/api/email/auth_check").param("authNum", String.valueOf(authNum)).session(session))
                .andDo(print());
    }
}