package xyz.moodf.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.moodf.global.email.EmailMessage;
import xyz.moodf.global.email.Services.EmailSendService;
import xyz.moodf.global.email.Services.EmailVerifyService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EmailSendTest {
    @Autowired
    private EmailSendService emailSendService;

    @Autowired
    private EmailVerifyService emailVerifyService;

    @Test
    void sentTest() {
        EmailMessage message = new EmailMessage("chtkdldjs43@naver.com", "이메일 테스트", "인증번호 할 예정");
        boolean success = emailSendService.sendMail(message);

        assertTrue(success);
    }

    @Test
    @DisplayName("템플릿 형태로 전송 테스트")
    void sendWithTplTest() {
        EmailMessage message = new EmailMessage("chtkdldjs43@naver.com", "제목...", "내용...");
        Map<String,Object> tplData = new HashMap<>();
        tplData.put("authNum", "123456");
        boolean success = emailSendService.sendMail(message, "auth", tplData);

        assertTrue(success);
    }

    @Test
    @DisplayName("이메일 인증 번호 전송 테스트")
    void emailVerifyTest() {
        boolean result = emailVerifyService.sendCode("chtkdldjs43@naver.com");
        assertTrue(result);
    }
}
