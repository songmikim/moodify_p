package xyz.moodf.member.repositories;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.entities.QMember;
import xyz.moodf.member.social.constants.SocialType;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    boolean existsBySocialTypeAndSocialToken(SocialType type, String token);

    Member findBySocialTypeAndSocialToken(SocialType type, String token);

    /**
     * 이메일과 회원명으로 조회되는지 체크
     *
     * @param email
     * @param name
     * @return
     */
    default boolean existsByEmailAndName(String email, String name) {
        QMember member = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(member.email.eq(email))
                .and(member.name.eq(name));

        return exists(builder);
    }
}
