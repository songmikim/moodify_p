package xyz.moodf.mypage.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import xyz.moodf.global.email.EmailMessage;
import xyz.moodf.global.email.Services.EmailSendService;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.repositories.MemberRepository;
import xyz.moodf.mypage.entities.DeleteAccountToken;
import xyz.moodf.mypage.repositories.DeleteAccountTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final DeleteAccountTokenRepository tokenRepository;
    private final EmailSendService emailSendService;
    private final HttpServletRequest request;
    private final Utils utils;

    public boolean changePassword(Member member, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            return false;
        }
        member.setPassword(passwordEncoder.encode(newPassword));
        member.setCredentialChangedAt(LocalDateTime.now());
        repository.saveAndFlush(member);
        return true;
    }

    public void deleteAccount(Member member) {
        member.setDeletedAt(LocalDateTime.now());
        repository.saveAndFlush(member);
    }

    /**
     * 탈퇴 요청 - 이메일로 확인 링크 전송
     */
    public void requestDelete(Member member) {
        tokenRepository.deleteByMember(member);

        DeleteAccountToken token = new DeleteAccountToken();
        token.setMember(member);
        token.setToken(UUID.randomUUID().toString());
        token.setExpireAt(LocalDateTime.now().plusMinutes(5));
        tokenRepository.saveAndFlush(token);

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        String link = baseUrl + "/mypage/delete/confirm?token=" + token.getToken();

        EmailMessage emailMessage = new EmailMessage(
                member.getEmail(),
                utils.getMessage("Email.delete.subject"),
                utils.getMessage("Email.delete.message")
        );
        Map<String, Object> tplData = new HashMap<>();
        tplData.put("link", link);
        emailSendService.sendMail(emailMessage, "delete", tplData);
    }

    /**
     * 이메일 링크 클릭시 탈퇴 처리
     */
    public boolean confirmDelete(String token) {
        DeleteAccountToken tokenEntity = tokenRepository.findByToken(token).orElse(null);
        if (tokenEntity == null) return false;

        if (tokenEntity.getExpireAt() != null && tokenEntity.getExpireAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(tokenEntity);
            return false;
        }

        Member member = tokenEntity.getMember();
        deleteAccount(member);
        tokenRepository.delete(tokenEntity);

        return true;
    }
}