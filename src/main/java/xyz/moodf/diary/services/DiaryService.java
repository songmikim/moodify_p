package xyz.moodf.diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.moodf.diary.dtos.DiaryRequest;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.DiaryId;
import xyz.moodf.diary.entities.Sentiment;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.diary.repositories.RecMusicRepository;
import xyz.moodf.diary.repositories.SentimentRepository;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.exceptions.MemberNotFoundException;
import xyz.moodf.member.libs.MemberUtil;
import xyz.moodf.member.repositories.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional  // 여러 Repository 메서드 호출이 하나의 트랜잭션 안에서 처리
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final RecMusicRepository recMusicRepository;
    private final SentimentRepository sentimentRepository;

    private final DiaryInfoService infoService;

    private final MemberUtil memberUtil;
    private final MemberRepository memberRepository;

    public Diary process(DiaryRequest request, Member member) {

        Member loggedMember = memberRepository.findById(member.getSeq())
                .orElseThrow(MemberNotFoundException::new);     // 영속 상태

        String gid = request.getGid();

        Diary diary = new Diary();

        diary.setMember(loggedMember);
        diary.setDate(request.getDate());
        diary.setGid(gid);
        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());
        diary.setWeather(request.getWeather());
        diary.setCreatedAt(LocalDateTime.now());

        // 감정 분석 완료 처리
        Sentiment sentiment = sentimentRepository.findById(gid).orElse(null);
        if (sentiment != null) {
            sentiment.setDone(true);
            diary.setSentiments(sentiment.getSentiments());
            sentimentRepository.saveAndFlush(sentiment);
        }

        // 추가 정보 세팅
        infoService.addInfo(diary);

        diaryRepository.saveAndFlush(diary);

        return diary;
    }

    public void delete(String gid, LocalDate date) {
        Member member = memberUtil.getMember();
        System.out.println("삭제 시도: gid=" + gid + ", date=" + date + ", member=" + member.getName());

        diaryRepository.deleteById(new DiaryId(member, date));
        sentimentRepository.deleteById(gid);
        recMusicRepository.deleteById(gid);
    }
}