package xyz.moodf.diary.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import xyz.moodf.diary.constants.Weather;
import xyz.moodf.diary.dtos.DiaryRequest;
import xyz.moodf.diary.dtos.SentimentRequest;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.repositories.SentimentRepository;
import xyz.moodf.diary.services.DiaryInfoService;
import xyz.moodf.diary.services.DiaryService;
import xyz.moodf.diary.services.SentimentService;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;

import java.time.LocalDate;
import java.util.*;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/diary")
@SessionAttributes("extraData")
public class DiaryController {

    private final Utils utils;
    private final MemberUtil memberUtil;
    private final DiaryService diaryService;
    private final DiaryInfoService infoService;
    private final SentimentService sentimentService;
    private final SentimentRepository sentimentRepository;

    @ModelAttribute("extraData")
    public Map<LocalDate, Object> getExtraData() {
        Map<LocalDate, Object> map = new HashMap<>();
        //map.put(LocalDate.now(), "<img src='/common/images/kakao_login.png'>");
        return map;
    }

    @GetMapping
    public String diary(@ModelAttribute DiaryRequest form, Model model) {
        commonProcess("diary", model);

        form.setWeather(Weather.NULL);
        form.setDate(LocalDate.now());
        form.setGid(UUID.randomUUID().toString());

        model.addAttribute("today", LocalDate.now());
        model.addAttribute("weatherValues", Weather.values());
        model.addAttribute("diaryRequest", form);

//        Member member = memberUtil.getMember();
//
//        Diary diary = new Diary();
//        diary.setWeather(Weather.NULL);
//
//        model.addAttribute("today", LocalDate.now());
//        model.addAttribute("diary", diary);
//        model.addAttribute("weatherValues", Weather.values());
//
//        Sentiment sentiment = sentimentService.create(member.getSeq());
//
//        model.addAttribute("gid", sentiment.getGid());

        return utils.tpl("diary/diary");
    }

    @PostMapping
    public String process(@Valid DiaryRequest form, Errors errors, Model model) {
        commonProcess("diary", model);

        if (errors.hasErrors()) {
            return utils.tpl("diary/diary");
        }

        Member member = memberUtil.getMember();

        Diary diary = diaryService.process(form, member);

        return "redirect:/diary/result/" + diary.getDate();
    }

    @ResponseBody
    @PostMapping("/sentiment")
    public void sentiment(DiaryRequest form) {
        sentimentService.update(form);
    }

    @ResponseBody
    @GetMapping("/sentiment/{gid}")
    public List<String> currentSentiment(@PathVariable("gid") String gid) {
        return sentimentService.get(gid);
    }

    @GetMapping("/result/{date}")
    public String result(@PathVariable("date") LocalDate date, Model model) {
        commonProcess("result", model);

        Diary diary = infoService.get(date);
        System.out.println(diary);
        model.addAttribute("diaryItem", diary);
        return utils.tpl("diary/result");
    }

    @GetMapping("/calendar")
    public String calendar(Model model, @ModelAttribute("extraData") Map<LocalDate, Object> extraData) {
        commonProcess("result", model);

        Member member = memberUtil.getMember();
        List<Diary> diaryList = infoService.getList(member.getSeq());

        /* 날짜마다 감정 이미지 삽입 */
        for (Diary diary : diaryList) {
            String sentimentString = "";
            String diarySentiment = infoService.getMostFrequentSentiment(member, diary.getDate());

            /* 추후에 수정 필요 - 감정 결과에 따라 이미지 다르게 띄우기 */
            switch (diarySentiment) {
                case "기쁨": sentimentString = "happiness"; break;
                default: break;
            }

            extraData.put(diary.getDate(), "<img src='/common/images/sentiments/" + sentimentString + ".png'>");
        }

        return utils.tpl("diary/calendar");
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateDiary(
            @RequestParam("gid") String gid,
            @RequestBody SentimentRequest request) {

        // 임시...
        request.setSentiments("기쁨");

        System.out.println("sentiments: " + request.getSentiments());
        System.out.println("content: " + request.getContent());

//        sentimentService.update(gid, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/result")
    public String result(Model model) {
        commonProcess("diary", model);

        return utils.tpl("diary/result");
    }

    @PostMapping("/save")
    public String saveDiary(@ModelAttribute DiaryRequest diaryRequest,
                            @RequestParam("gid") String gid,
                            Model model) {

        Member member = memberUtil.getMember();

        diaryService.process(diaryRequest, member);

        return "redirect:/diary/result";
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteSentiment(@RequestParam String gid) {
        System.out.println("삭제요청");
        sentimentRepository.deleteById(gid);
        return ResponseEntity.ok().build();
    }

    /**
     * 일기 작성 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "diary";
        String pageTitle = "";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();

        if (mode.equals("diary")) {
            addScript.add("diary/sentiment");  // 추가로 만든 sentiment db 관리 js 파일
            pageTitle = utils.getMessage("일기쓰기");

        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("pageTitle", pageTitle);
    }
}
