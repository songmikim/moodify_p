package xyz.moodf.diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.moodf.diary.dtos.DiaryRequest;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.DiaryId;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.diary.repositories.SentimentRepository;

import java.time.LocalDateTime;

@Service
@Transactional  // 여러 Repository 메서드 호출이 하나의 트랜잭션 안에서 처리
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    public final SentimentRepository sentimentRepository;

    public Diary process(DiaryRequest request, DiaryId id) {
        Diary diary = diaryRepository.findById(new DiaryId(id.getMember(), id.getDate()))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일기입니다!"));

        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());
        diary.setWeather(request.getWeather());
        diary.setDate(request.getDate());
        diary.setCreatedAt(LocalDateTime.now());

        return diaryRepository.saveAndFlush(diary);
    }
}