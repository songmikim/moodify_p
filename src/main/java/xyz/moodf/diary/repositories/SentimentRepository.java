package xyz.moodf.diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import xyz.moodf.diary.entities.Sentiment;
import xyz.moodf.member.entities.Member;

import java.util.List;

public interface SentimentRepository extends JpaRepository<Sentiment, String>, QuerydslPredicateExecutor<Sentiment> {

    /**
     * 회원의 일기 감정 분석 데이터 조회
     *
     * @param member 현재 로그인 회원
     * @return 회원의 모든 감정 분석 데이터
     */
    @Query("SELECT s FROM Sentiment s JOIN Diary d ON s.gid = d.gid WHERE d.member = :member")
    List<Sentiment> findByMember(@Param("member") Member member);
}
