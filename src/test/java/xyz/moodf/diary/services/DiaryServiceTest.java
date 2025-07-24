package xyz.moodf.diary.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import xyz.moodf.diary.constants.Weather;
import xyz.moodf.diary.dtos.DiaryRequest;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.repositories.MemberRepository;

import java.time.LocalDate;

@Transactional
@SpringBootTest
public class DiaryServiceTest {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void init() {
        // 멤버 추가
        member = new Member();
        member.setEmail("user01@test.org");
        member.setPassword("1234");
        member.setName("사용자01");
        member.setMobile("01010001000");

        member = memberRepository.save(member);
    }

    @Test
    void test1() {
        String title = "오늘의 일기";
        String content = "아무 노래나 일단 틀어~ 아무거나 신나는 걸로~";

//        Diary savedDiary = diaryService.process(title, content, member);
//
//        // 검증
//        assertNotNull(savedDiary.getDid(), "Diary ID가 null이면 안 됩니다.");
//        assertEquals(title, savedDiary.getTitle(), "제목이 일치해야 합니다.");
//        assertEquals(content, savedDiary.getContent(), "내용이 일치해야 합니다.");
//        assertEquals(member.getSeq(), savedDiary.getMember().getSeq(), "작성자가 일치해야 합니다.");
//
//        // DB에서 확인
//        Diary found = diaryRepository.findById(savedDiary.getDid()).orElseThrow();
//        assertEquals(title, found.getTitle());
//        System.out.println("저장된 일기: " + found);
    }

    @Test
    void test2() {
        DiaryRequest diaryRequest = new DiaryRequest();
        diaryRequest.setTitle("오늘의 일기");
        diaryRequest.setContent("내용");
        diaryRequest.setDate(LocalDate.now());
        diaryRequest.setWeather(Weather.RAINY);

        Diary savedDiary = diaryService.process(diaryRequest, member);

        // DB에서 확인
        Diary found = diaryRepository.findById(savedDiary.getDid()).orElseThrow();
        System.out.println("저장된 일기: " + found);
    }

}
