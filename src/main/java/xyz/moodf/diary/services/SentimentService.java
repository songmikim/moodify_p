package xyz.moodf.diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xyz.moodf.diary.dtos.DiaryRequest;
import xyz.moodf.diary.entities.Sentiment;
import xyz.moodf.diary.exceptions.SentimentNotFoundException;
import xyz.moodf.diary.repositories.SentimentRepository;

import java.util.Arrays;
import java.util.List;

@Lazy
@Service
@Transactional  // 여러 Repository 메서드 호출이 하나의 트랜잭션 안에서 처리
@RequiredArgsConstructor
public class SentimentService {
    private final SentimentRepository sentimentRepository;
//    private final MemberRepository memberRepository;
//
//    public Sentiment create(Long memberSeq) {
//        Member member = memberRepository.findById(memberSeq)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));
//
//        String gid = UUID.randomUUID().toString();
//
//        Sentiment sentiment = new Sentiment();
//        sentiment.setGid(gid);
//        sentiment.setContent("");
//        sentiment.setSentiments("");
//        sentimentRepository.save(sentiment);
//
//        return sentimentRepository.saveAndFlush(sentiment);
//    }

//    public Sentiment update(String gid, SentimentRequest request) {
//        // Sentiment 조회
//        Sentiment sentiment = sentimentRepository.findById(gid)
//                .orElseThrow(() -> new IllegalArgumentException("해당 감정 분석 데이터가 존재하지 않습니다."));
//
//        // 값 업데이트
//        sentiment.setContent(request.getContent());
//        sentiment.setSentiments(request.getSentiments());
//
//        // 저장 후 반환
//        return sentimentRepository.saveAndFlush(sentiment);
//    }

    public void update(DiaryRequest form) {
        // content만 수정
        Sentiment sentiment = sentimentRepository.findById(form.getGid())
                .orElseThrow(SentimentNotFoundException::new);
        sentiment.setContent(form.getContent());

        sentimentRepository.saveAndFlush(sentiment);
    }

    public List<String> get(String gid) {
        Sentiment item = sentimentRepository.findById(gid).orElse(null);
        if (item == null || !StringUtils.hasText(item.getSentiments()))
            return List.of();

        return Arrays.stream(item.getSentiments().split(","))
                .toList();
    }
}
