package xyz.moodf.diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.moodf.diary.constants.Weather;
import xyz.moodf.diary.dtos.SentimentRequest;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.DiaryId;
import xyz.moodf.diary.entities.Sentiment;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.diary.repositories.SentimentRepository;
import xyz.moodf.member.entities.Member;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional  // 여러 Repository 메서드 호출이 하나의 트랜잭션 안에서 처리
@RequiredArgsConstructor
public class SentimentService {
    private final DiaryRepository diaryRepository;
    private final SentimentRepository sentimentRepository;

    public Sentiment create(SentimentRequest request, Member member) {
        String gid = UUID.randomUUID().toString();

        Sentiment sentiment = new Sentiment();
        sentiment.setGid(gid);
        sentiment.setContent("");
        sentiment.setSentiments("");
        sentimentRepository.save(sentiment);

        Diary diary = new Diary();
        diary.setTitle("");
        diary.setContent("");
        diary.setDate(request.getDate());
        diary.setWeather(Weather.NULL);
        diary.setMember(member);
        diary.setGid(gid);
        diaryRepository.saveAndFlush(diary);

        return sentimentRepository.saveAndFlush(sentiment);
    }

    public Sentiment update(SentimentRequest request, Member member, LocalDate date) {
        // 1. Diary 조회 (기본키: member + date)
        Diary diary = diaryRepository.findById(new DiaryId(member, date))  // 기본키
                .orElseThrow(() -> new IllegalArgumentException("해당 일기가 존재하지 않습니다."));

        // 2. gid 가져오기
        String gid = diary.getGid();

        // 3. Sentiment 조회
        Sentiment sentiment = sentimentRepository.findById(gid)
                .orElseThrow(() -> new IllegalArgumentException("해당 감정 분석 데이터가 존재하지 않습니다."));

        // 4. 값 업데이트
        sentiment.setContent(request.getContent());
        sentiment.setSentiments(request.getSentiments());

        // 5. 저장 후 반환
        return sentimentRepository.saveAndFlush(sentiment);
    }
}
