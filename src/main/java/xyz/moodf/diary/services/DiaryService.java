package xyz.moodf.diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.moodf.diary.dtos.DiaryRequest;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.Sentiment;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.member.entities.Member;

@Service
@Transactional  // 여러 Repository 메서드 호출이 하나의 트랜잭션 안에서 처리
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public Diary process(DiaryRequest request, Member member) {
        Diary diary = new Diary();
        Sentiment sentiment = new Sentiment();

        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());
        diary.setDate(request.getDate());
        diary.setWeather(request.getWeather());
        diary.setMember(member);

        diary.setSentiment(sentiment);
        sentiment.setDiary(diary);

        return diaryRepository.save(diary);
    }
}