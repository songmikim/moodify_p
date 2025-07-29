package xyz.moodf.mypage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.moodf.diary.services.DiaryInfoService;
import xyz.moodf.global.exceptions.script.AlertBackException;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.global.rests.JSONData;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.exceptions.MemberNotFoundException;
import xyz.moodf.member.libs.MemberUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class ApiMyPageController {
    private final MemberUtil memberUtil;
    private final DiaryInfoService infoService;
    private final Utils utils;

    @GetMapping("/emotion")
    public JSONData<Map<String, Long>> emotionData(@RequestParam int year, @RequestParam int month) {
        Member member = memberUtil.getMember();

        // 필요하면 여기에 유효성 검증 추가

        try {
            Map<String, Long> result = infoService.getSentimentFrequencies(member.getSeq(), year, month);
            return new JSONData<>(result);
        } catch (MemberNotFoundException e) {
            throw new AlertBackException(utils.getMessage("NotFound.member"), HttpStatus.NOT_FOUND);
        }
    }
}
