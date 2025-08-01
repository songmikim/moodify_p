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
        LocalDate sDate = Objects.requireNonNullElse(search.getSDate(), LocalDate.now().minusMonths(1L));
        LocalDate eDate = Objects.requireNonNullElse(search.getEDate(), LocalDate.now());
        StatisticType type = Objects.requireNonNullElse(search.getType(), StatisticType.MONTHLY);

        return infoService.getStatistics(sDate, eDate, type);
    }
}
