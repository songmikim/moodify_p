package xyz.moodf.mypage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.moodf.diary.constants.StatisticType;
import xyz.moodf.diary.services.DiaryInfoService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class ApiMyPageController {
    private final DiaryInfoService infoService;

    @GetMapping("/emotion")
    public Map<LocalDate, Map<String, Integer>> emotionData(@ModelAttribute StatisticSearch search) {
        LocalDate sDate = Objects.requireNonNullElse(search.getSDate(), LocalDate.now().minusYears(1L));
        LocalDate eDate = search.getEDate();
        StatisticType type = Objects.requireNonNullElse(search.getType(), StatisticType.MONTHLY);

        return infoService.getStatistics(sDate, eDate, type);
    }

/*    @GetMapping("/emotion")
    public JSONData<Map<String, Long>> emotionData(@RequestParam int year, @RequestParam int month) {
        Member member = memberUtil.getMember();

        // 필요하면 여기에 유효성 검증 추가

        try {
            Map<String, Long> result = infoService.getSentimentFrequencies(member, year, month);
            return new JSONData<>(result);
        } catch (MemberNotFoundException e) {
            throw new AlertBackException(utils.getMessage("NotFound.member"), HttpStatus.NOT_FOUND);
        }
    }*/
}
