package xyz.moodf.member.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.social.constants.SocialType;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {
    // 이메일이 이미 가입되어 있는지 확인 -> 중복 가입 방지용
    boolean existsByEmail(String email);

    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);

    // 소셜타입과 토큰 조합으로 가입된 계정이 있는지
    boolean existsBySocialTypeAndSocialToken(SocialType type, String token);

    // 소셜타입과 토큰 조합으로 회원 조회
    Member findBySocialTypeAndSocialToken(SocialType type, String token);
}
