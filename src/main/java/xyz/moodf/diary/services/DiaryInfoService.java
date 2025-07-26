package xyz.moodf.diary.services;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.QDiary;
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

    public List<Diary> getList(long seq) {
        QDiary diary = QDiary.diary;

        return jpaQueryFactory
                .selectFrom(diary)
                .where(diary.member.seq.eq(seq))
                .fetch();
    }

    public String getMostFrequentSentiment(long diarySeq) {
        QDiary diary = QDiary.diary;

        List<String> allSentiments = jpaQueryFactory
                .select(diary.sentiment.sentiment)
                .from(diary)
                .where(diary.did.eq(diarySeq))
                .fetch();

        // 각 감정의 빈도 수 저장
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String line : allSentiments) {
            if (line == null || line.isBlank()) continue;

            // string을 [] 형태로 변환
            String[] split = line.split(",");
            for (String sentiment : split) {
                String trimmed = sentiment.trim();  // 공백 제거
                if (trimmed.isEmpty()) continue;
                frequencyMap.put(trimmed, frequencyMap.getOrDefault(trimmed, 0) + 1);
            }
        }

        // 가장 많이 나온 감정
        return frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey) // 가장 많이 나온 감정 문자열 반환
                .orElse(""); // 감정이 하나도 없을 경우 빈 문자열 반환
    }
}
