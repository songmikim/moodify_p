package xyz.moodf.member.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import xyz.moodf.global.email.EmailMessage;
import xyz.moodf.global.email.Services.EmailSendService;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.controllers.RequestFindPw;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.exceptions.MemberNotFoundException;
import xyz.moodf.member.repositories.MemberRepository;
import xyz.moodf.member.validators.FindPwValidator;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FindPwService {

    private final FindPwValidator validator;
    private final MemberRepository repository;
    private final EmailSendService sendService;
    private final PasswordEncoder encoder;
    private final Utils utils;

    public void process(RequestFindPw form, Errors errors) {
        validator.validate(form, errors);
        if (errors.hasErrors()) { //유효성 검사 실패시 처리 중단
            return;
        }

        // 비밀번호 초기화
        reset(form.email());
    }

    public void reset(String email) {
        // 비밀번호 초기화
        Member member = repository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        String newPassword = utils.randomChars(12); //비밀번호 12자
        member.setPassword(encoder.encode(newPassword));

        repository.saveAndFlush(member);


        EmailMessage emailMessage = new EmailMessage(email, utils.getMessage("Email.password.reset"), utils.getMessage("Email.password.reset"));
        Map<String, Object> tplData = new HashMap<>();
        tplData.put("password", newPassword);
        sendService.sendMail(emailMessage, "password_reset", tplData);
    }

}
