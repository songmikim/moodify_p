package xyz.moodf.mypage.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xyz.moodf.diary.constants.StatisticType;
import xyz.moodf.diary.services.DiaryInfoService;
import xyz.moodf.diary.services.RecommendService;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.global.rests.JSONData;
import xyz.moodf.global.search.ListData;
import xyz.moodf.global.search.Pagination;
import xyz.moodf.member.libs.MemberUtil;
import xyz.moodf.spotify.entities.Music;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class ApiMyPageController {
    private final DiaryInfoService infoService;
    private final RecommendService recommendService;
    private final MemberUtil memberUtil;
    private final Utils utils;
    /**
     * 특정 기간 동안의 감정 통계 데이터를 조회합니다.
     */
    @GetMapping("/emotion")
    public Map<LocalDate, Map<String, Integer>> emotionData(@ModelAttribute StatisticSearch search) {
        LocalDate sDate = Objects.requireNonNullElse(search.getSDate(), LocalDate.now().minusMonths(1L));
        LocalDate eDate = Objects.requireNonNullElse(search.getEDate(), LocalDate.now());
        StatisticType type = Objects.requireNonNullElse(search.getType(), StatisticType.MONTHLY);

        return infoService.getStatistics(sDate, eDate, type);
    }

    /**
     * 사용자의 감정에 맞는 추천 음악 목록 조회
     */
    @GetMapping("/recommend-songs")
    public JSONData<ListData<Music>> recommendSongs(
            @RequestParam("emotion") String emotion,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            HttpServletRequest request) {
        List<Music> songs = recommendService.getSongs(memberUtil.getMember(), emotion);
        int total = songs != null ? songs.size() : 0;
        page = Math.max(page, 1);
        limit = limit < 1 ? 10 : limit;

        int fromIndex = Math.min((page - 1) * limit, total);
        int toIndex = Math.min(fromIndex + limit, total);
        List<Music> items = songs != null ? songs.subList(fromIndex, toIndex) : List.of();

        Pagination pagination = new Pagination(page, total, 10, limit, request);
        ListData<Music> listData = new ListData<>(items, pagination);

        JSONData<ListData<Music>> data = new JSONData<>(listData);
        if (items == null || items.isEmpty()) {
            data.setMessage(utils.getMessage("추천곡이_없습니다"));
        }
        return data;
    }
}
