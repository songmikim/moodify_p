package xyz.moodf.diary.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.moodf.diary.constants.Weather;
import xyz.moodf.diary.dtos.DiaryRequest;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.DiaryId;
import xyz.moodf.diary.services.DiaryInfoService;
import xyz.moodf.diary.services.DiaryService;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/diary")
@SessionAttributes("extraData")
public class DiaryController {

    private final Utils utils;
    private final DiaryService diaryService;
    private final DiaryInfoService infoService;
    private final MemberUtil memberUtil;

    @ModelAttribute("extraData")
    public Map<LocalDate, Object> getExtraData() {
        Map<LocalDate, Object> map = new HashMap<>();
        map.put(LocalDate.now(), "<img src='/common/images/kakao_login.png'>");
        return map;
    }

    @GetMapping
    public String diary(Model model) {
        commonProcess("member", model);

        Diary diary = new Diary();
        diary.setWeather(Weather.NULL);

        model.addAttribute("today", LocalDate.now());
        model.addAttribute("diary", diary);
        model.addAttribute("weatherValues", Weather.values());

        return utils.tpl("diary/diary");
    }

    @GetMapping("/calendar")
    public String calendar(Model model, @ModelAttribute("extraData") Map<LocalDate, Object> extraData) {
        commonProcess("member", model);

        Member member = memberUtil.getMember();
        List<Diary> diaryList = infoService.getList(member.getSeq());

        /* 날짜마다 감정 이미지 삽입 */
        for (Diary diary : diaryList) {
            String sentimentString = "";
            String diarySentiment = infoService.getMostFrequentSentiment(new DiaryId(member, diary.getDate()));

            /* 추후에 수정 필요 - 감정 결과에 따라 이미지 다르게 띄우기 */
            switch (diarySentiment) {
                case "": sentimentString = "happiness"; break;
                default: break;
            }

            extraData.put(diary.getDate(), "<img src='/common/images/sentiments/" + sentimentString + ".png'>");
        }

        return utils.tpl("diary/calendar");
    }

    @GetMapping("/result")
    public String result(Model model) {
        commonProcess("member", model);

        return utils.tpl("diary/result");
    }

    @PostMapping("/save")
    public String saveDiary(@ModelAttribute DiaryRequest diaryRequest,
                            Model model) {

        Member member = memberUtil.getMember();
        LocalDate date = diaryRequest.getDate();

        diaryService.process(diaryRequest, new DiaryId(member, date));

        return "redirect:/diary/result";
    }


    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "member";
        String pageTitle = "";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();

        if (mode.equals("member")) {
            addCommonScript.add("fileManager");
            addScript.add("/diary");
            pageTitle = utils.getMessage("일기쓰기");

        } else if (mode.equals("login")) { // 로그인 공통 처리
            pageTitle = utils.getMessage("일기장_관리");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("pageTitle", pageTitle);
    }
}
