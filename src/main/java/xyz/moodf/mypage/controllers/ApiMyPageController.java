package xyz.moodf.mypage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.moodf.global.rests.JSONData;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class ApiMyPageController {
    //private final DiaryService diaryService;
    private final MemberUtil memberUtil;

    @GetMapping("/emotion")
    public JSONData<Map<String, Long>> emotionData(@RequestParam int year, @RequestParam int month) {
        Member member = memberUtil.getMember();
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        // Map<String, Long> data = diaryService.getMonthlySentimentCounts(member, start, end);
        // 수정예정
        Map<String, Long> data = new LinkedHashMap<>();
        data.put("기쁨", 5L);
        data.put("슬픔", 3L);
        data.put("분노", 1L);
        data.put("불안", 8L);
        data.put("당황", 2L);

        return new JSONData<>(data);
    }
}
