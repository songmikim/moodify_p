package xyz.moodf.diary.services;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.DiaryId;
import xyz.moodf.diary.entities.QDiary;
import xyz.moodf.diary.entities.QSentiment;
import xyz.moodf.diary.exceptions.DiaryNotFoundException;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;
import xyz.moodf.member.repositories.MemberRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Lazy
@Service
@RequiredArgsConstructor
public class DiaryInfoService {

    private final DiaryRepository diaryRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final MemberRepository memberRepository;
    private final MemberUtil memberUtil;

    public Diary get(Member member, LocalDate date) {
        DiaryId diaryId = new DiaryId(member, date);
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(DiaryNotFoundException::new);

        return diary;
    }

    public Diary get(LocalDate date) {
        Member member = memberUtil.getMember();

        return get(member, date);
    }

    public List<Diary> getList(long memberSeq) {
        QDiary diary = QDiary.diary;

        return jpaQueryFactory
                .selectFrom(diary)
                .where(diary.member.seq.eq(memberSeq))
                .fetch();
    }

    public Map<String, Long> getSentimentFrequencies(Member member, int year, int month) {

        LocalDate firstDate = LocalDate.of(year, month, 1);
        LocalDate lastDate = firstDate.withDayOfMonth(firstDate.lengthOfMonth());

        QDiary diary = QDiary.diary;

        // 지정된 기간 동안 해당 멤버가 작성한 일기들의 날짜 리스트
        List<LocalDate> diaryDateList = jpaQueryFactory
                .select(diary.date)
                .from(diary)
                .where(
                        diary.member.eq(member),
                        diary.date.between(firstDate, lastDate)
                )
                .fetch();

        // 각 날짜의 대표 감정 리스트
        List<String> sentList = new ArrayList<>();
        for (LocalDate diaryDate : diaryDateList) {
            sentList.add(getMostFrequentSentiment(member, diaryDate));
        }

        // 지정된 기간 동안의 대표 감정 빈도 수
        Map<String, Long> sentimentFrequencyMap = sentList.stream()
                .filter(Objects::nonNull)  // null 값 제외
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return sentimentFrequencyMap;
    }

    public String getMostFrequentSentiment(Member member, LocalDate date) {
        DiaryId diaryId = new DiaryId(member, date);
        QDiary diary = QDiary.diary;
        QSentiment sentiment = QSentiment.sentiment;

        List<String> allSentiments = jpaQueryFactory
                .select(sentiment.sentiments)
                .from(diary)
                .join(sentiment).on(diary.gid.eq(sentiment.gid))
                .where(
                        diary.member.eq(diaryId.getMember())
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
