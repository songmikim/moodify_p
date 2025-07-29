package xyz.moodf.diary.services;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.DiaryId;
import xyz.moodf.diary.entities.QDiary;
import xyz.moodf.diary.entities.QSentiment;
import xyz.moodf.diary.repositories.DiaryRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service
@RequiredArgsConstructor
public class DiaryInfoService {

    private final JPAQueryFactory jpaQueryFactory;
    private final DiaryRepository repository;
    private final HttpServletRequest request;
    private final ModelMapper mapper;

    public List<Diary> getList(long memberSeq) {
        QDiary diary = QDiary.diary;

        return jpaQueryFactory
                .selectFrom(diary)
                .where(diary.member.seq.eq(memberSeq))
                .fetch();
    }

    public String getMostFrequentSentiment(DiaryId diaryId) {
        QDiary diary = QDiary.diary;
        QSentiment sentiment = QSentiment.sentiment;

        List<String> allSentiments = jpaQueryFactory
                .select(sentiment.sentiments)
                .from(diary)
                .join(sentiment).on(diary.gid.eq(sentiment.gid))
                .where(
                        diary.member.seq.eq(diaryId.getMember())
                                .and(diary.date.eq(diaryId.getDate()))
                )
                .fetch();

        System.out.println("1: " + allSentiments);

        // 각 감정의 빈도 수 저장
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String line : allSentiments) {
            if (line == null || line.isBlank()) continue;

            // string을 [] 형태로 변환
            String[] split = line.split(",");
            for (String s : split) {
                String trimmed = s.trim();  // 공백 제거
                if (trimmed.isEmpty()) continue;
                frequencyMap.put(trimmed, frequencyMap.getOrDefault(trimmed, 0) + 1);
            }
        }

        System.out.println("2: " + frequencyMap);

        // 가장 많이 나온 감정
        return frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey) // 가장 많이 나온 감정 문자열 반환
                .orElse(""); // 감정이 하나도 없을 경우 빈 문자열 반환
    }
}
