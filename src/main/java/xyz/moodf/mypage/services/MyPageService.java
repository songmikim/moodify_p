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

    public void changePassword(Member member, String password) {
        member.setPassword(passwordEncoder.encode(password));
        member.setCredentialChangedAt(LocalDateTime.now());
        repository.saveAndFlush(member);
    }

    public void deleteAccount(Member member) {
        member.setDeletedAt(LocalDateTime.now());
        repository.saveAndFlush(member);
    }
}