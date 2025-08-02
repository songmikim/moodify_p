package xyz.moodf.diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xyz.moodf.diary.dtos.DiaryRequest;
import xyz.moodf.diary.entities.Sentiment;
import xyz.moodf.diary.repositories.SentimentRepository;
import xyz.moodf.global.codevalue.services.CodeValueService;
import xyz.moodf.global.file.entities.FileInfo;
import xyz.moodf.global.file.services.FileInfoService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Lazy
@Service
@Transactional  // 여러 Repository 메서드 호출이 하나의 트랜잭션 안에서 처리
@RequiredArgsConstructor
public class SentimentService {
    private final SentimentRepository sentimentRepository;
    private final CodeValueService codeValueService;
    private final FileInfoService fileInfoService;

    public void update(DiaryRequest form) {
        
        Sentiment sentiment = sentimentRepository.findById(form.getGid()).orElse(null);

        System.out.println("감정 데이터: " + sentiment);
        System.out.println("전달된 일기: " + form.getContent());

        if (sentiment == null) {
            // sentiment가 없으면, 새로 추가
            Sentiment newSent = new Sentiment();
            newSent.setGid(form.getGid());
            newSent.setContent(form.getContent());
            newSent.setDone(false);
            sentimentRepository.saveAndFlush(newSent);
        } else {
            // 이미 sentiment가 존재하면, content만 수정
            sentiment.setContent(form.getContent());
            sentimentRepository.saveAndFlush(sentiment);
        }
    }

    public List<String> get(String gid) {
        Sentiment item = sentimentRepository.findById(gid).orElse(null);
        if (item == null || !StringUtils.hasText(item.getSentiments()))
            return List.of();

        List<String> items = new ArrayList<>(Arrays.stream(item.getSentiments().split(","))
                .toList());

        for (int i = 0; i < items.size(); i++) {
            String code = items.get(i).split(" ")[0];  // 대분류만 가져오기
            String iconGid = codeValueService.get(code, String.class);
            FileInfo fileItem = fileInfoService.get(iconGid);
            if (fileItem != null) {
                items.set(i, String.format("<img src='%s' width=50 height=50>", fileItem.getFileUrl()));
            }
        }

        return items;
    }

    public void resetDone(String gid) {
        Sentiment sentiment = sentimentRepository.findById(gid).orElse(null);

        sentiment.setDone(false);
    }
}
