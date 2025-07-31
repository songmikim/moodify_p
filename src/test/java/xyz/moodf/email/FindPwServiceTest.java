package xyz.moodf.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.moodf.member.services.FindPwService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class FindPwServiceTest {

    @Autowired
    private FindPwService service;

    @Test
    @DisplayName("비밀번호 초기화 및 초기화된 메일 이메일 전송 테스트")
    void resetTest() {
        assertDoesNotThrow(() -> service.reset("chtkdldjs43@naver.com"));
    }
}