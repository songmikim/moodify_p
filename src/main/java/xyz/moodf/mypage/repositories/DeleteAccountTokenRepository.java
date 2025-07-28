package xyz.moodf.mypage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.moodf.mypage.entities.DeleteAccountToken;
import xyz.moodf.member.entities.Member;

import java.util.Optional;

public interface DeleteAccountTokenRepository extends JpaRepository<DeleteAccountToken, Long> {
    Optional<DeleteAccountToken> findByToken(String token);
    void deleteByMember(Member member);
}