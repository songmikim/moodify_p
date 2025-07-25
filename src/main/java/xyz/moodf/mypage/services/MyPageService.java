package xyz.moodf.mypage.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.repositories.MemberRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;

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
}