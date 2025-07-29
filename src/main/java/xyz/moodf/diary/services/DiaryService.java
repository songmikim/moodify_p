package xyz.moodf.diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.moodf.diary.dtos.DiaryRequest;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.Sentiment;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.diary.repositories.SentimentRepository;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.repositories.MemberRepository;

import java.time.LocalDateTime;

@Service
@Transactional  // 여러 Repository 메서드 호출이 하나의 트랜잭션 안에서 처리
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final SentimentRepository sentimentRepository;
    private final MemberRepository memberRepository;

    public Diary process(DiaryRequest request, String gid, Long memberSeq) {
        Sentiment sentiment = sentimentRepository.findById(gid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 감정 분석 결과입니다."));

        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));

        Diary diary = new Diary();


        diary.setMember(member);
        diary.setDate(request.getDate());
        diary.setGid(gid);
        diary.setTitle(request.getTitle());
        diary.setContent(sentiment.getContent());
        diary.setWeather(request.getWeather());
        diary.setCreatedAt(LocalDateTime.now());

        return diaryRepository.saveAndFlush(diary);
    }
}