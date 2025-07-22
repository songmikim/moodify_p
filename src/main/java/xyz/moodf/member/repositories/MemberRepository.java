package xyz.moodf.member.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.social.constants.SocialType;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    boolean existsBySocialTypeAndSocialToken(SocialType type, String token);

    Member findBySocialTypeAndSocialToken(SocialType type, String token);
}
