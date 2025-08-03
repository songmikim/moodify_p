package xyz.moodf.mypage.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
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

    /**
     * 비밀번호 변경 처리
     * - 현재 비밀번호가 일치하는지 확인
     * - 새로운 비밀번호를 암호화하여 저장
     *
     * @param member 현재 로그인된 회원
     * @param currentPassword 입력한 현재 비밀번호
     * @param newPassword 새로운 비밀번호
     * @return 성공 여부 (true: 변경 성공, false: 현재 비밀번호 불일치)
     */
    public boolean changePassword(Member member, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            return false;
        }
        member.setPassword(passwordEncoder.encode(newPassword));
        member.setCredentialChangedAt(LocalDateTime.now());
        repository.saveAndFlush(member);
        return true;
    }

    /**
     * 회원 즉시 탈퇴 처리
     * - 탈퇴 일시를 기록하고 저장
     *
     * @param member 탈퇴할 회원
     */
    public void deleteAccount(Member member) {
        member.setDeletedAt(LocalDateTime.now());
        repository.saveAndFlush(member);
    }

    /**
     * 탈퇴 요청 처리
     * - 기존 토큰 제거 후 새 토큰 생성
     * - 이메일로 확인 링크 전송
     *
     * @param member 탈퇴 요청을 한 회원
     */
    public boolean requestDelete(Member member) {
        // 기존 탈퇴 토큰 제거
        tokenRepository.deleteByMember(member);

        // 새로운 탈퇴 토큰 생성
        DeleteAccountToken token = new DeleteAccountToken();
        token.setMember(member);
        token.setToken(UUID.randomUUID().toString());
        token.setExpireAt(LocalDateTime.now().plusMinutes(5)); // 유효시간: 5분
        tokenRepository.saveAndFlush(token);

        // 이메일 링크 생성
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        String link = baseUrl + "/mypage/delete/confirm?token=" + token.getToken();

        // 이메일 메시지 및 템플릿 데이터 설정
        EmailMessage emailMessage = new EmailMessage(
                member.getEmail(),
                utils.getMessage("Email.delete.subject"),
                utils.getMessage("Email.delete.message")
        );
        Map<String, Object> tplData = new HashMap<>();
        tplData.put("link", link);


        boolean result = false;
        try {
            result = emailSendService.sendMail(emailMessage, "delete", tplData);
            if (!result) {
                log.error("Failed to send delete confirmation email");
            }
        } catch (Exception e) {
            log.error("Exception occurred while sending delete confirmation email", e);
        }

        return result;
    }

    /**
     * 탈퇴 확인 처리 (이메일 링크 클릭 시)
     * - 토큰 유효성 검사 및 만료 여부 확인
     * - 탈퇴 처리 및 토큰 삭제
     *
     * @param token 확인 링크에 포함된 토큰
     * @return 탈퇴 성공 여부 (true: 성공, false: 실패 또는 만료)
     */
    public boolean confirmDelete(String token) {
        DeleteAccountToken tokenEntity = tokenRepository.findByToken(token).orElse(null);
        if (tokenEntity == null) return false;

        // 만료된 토큰일 경우 삭제 후 실패 처리
        if (tokenEntity.getExpireAt() != null && tokenEntity.getExpireAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(tokenEntity);
            return false;
        }

        // 탈퇴 처리 및 토큰 제거
        Member member = tokenEntity.getMember();
        deleteAccount(member);
        tokenRepository.delete(tokenEntity);

        return true;
    }
}