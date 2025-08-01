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
import xyz.moodf.diary.entities.DiaryId;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.diary.repositories.SentimentRepository;
import xyz.moodf.diary.services.DiaryInfoService;
import xyz.moodf.diary.services.DiaryService;
import xyz.moodf.diary.services.RecommendService;
import xyz.moodf.diary.services.SentimentService;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.codevalue.services.CodeValueService;
import xyz.moodf.global.file.entities.FileInfo;
import xyz.moodf.global.file.services.FileInfoService;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;
import xyz.moodf.spotify.entities.Music;

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

    private final DiaryRepository diaryRepository;
    private final SentimentRepository sentimentRepository;

    private final CodeValueService codeValueService;
    private final FileInfoService fileInfoService;

    private final DiaryService diaryService;
    private final DiaryInfoService infoService;
    private final SentimentService sentimentService;
    private final RecommendService recommendService;

    @ModelAttribute("extraData")
    public Map<LocalDate, Object> getExtraData() {
        Map<LocalDate, Object> map = new HashMap<>();
        //map.put(LocalDate.now(), "<img src='/common/images/kakao_login.png'>");
        return map;
    }

    @GetMapping
    public String firstDiary() {
        LocalDate today = LocalDate.now();
        return "redirect:/diary/" + today;
    }

    @GetMapping("/{date}")
    public String diary(@PathVariable("date") LocalDate date,
                        @ModelAttribute DiaryRequest request, Model model) {
        commonProcess("diary", model);

        Member member = memberUtil.getMember();

        Optional<Diary> optional = diaryRepository.findById(new DiaryId(member, date));
        boolean isSaved;

        if (!optional.isPresent()) {

            DiaryRequest form = new DiaryRequest();
            form.setDate(date);
            form.setWeather(Weather.NULL);
            form.setGid(UUID.randomUUID().toString());

            isSaved = true;
            model.addAttribute("diaryRequest", form);

        } else {

            Diary diary = optional.get();
            request.setDate(diary.getDate());
            request.setTitle(diary.getTitle());
            request.setWeather(diary.getWeather());
            request.setContent(diary.getContent());
            request.setGid(diary.getGid());

            isSaved = false;

            model.addAttribute("diaryRequest", request);
        }

        model.addAttribute("today", LocalDate.now());
        model.addAttribute("weatherValues", Weather.values());
        model.addAttribute("isSaved", isSaved);
        model.addAttribute("weatherValues", Weather.values());
        model.addAttribute("date", request.getDate());


        return utils.tpl("diary/diary");
    }

    @PostMapping
    public String process(@Valid DiaryRequest form, Errors errors, Model model) {
        commonProcess("diary", model);

        System.out.println("결과 전달 중...");

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

        // 감정 분석에 따른 추천 콘텐츠 리스트
        Diary diary = infoService.get(date);
        String sentiment = diary.getStrongest();
        List<Music> items = recommendService.getContents(sentiment);
        model.addAttribute("items", items);

        // 캘린더로 이동할 때 쿼리스트링 구하기
        Integer year = date.getYear();
        Integer month = date.getMonthValue();

        model.addAttribute("year", year);
        model.addAttribute("month", month);



        return utils.tpl("diary/result");
    }

    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month,
            Model model, @ModelAttribute("extraData") Map<LocalDate, Object> extraData) {
        commonProcess("calendar", model);

        LocalDate today = LocalDate.now();
        year = Objects.requireNonNullElse(year, today.getYear());
        month = Objects.requireNonNullElse(month, today.getMonthValue());

        List<Diary> items = infoService.getList(year, month);
        items.forEach(item -> {
            String emo = item.getStrongest();
            String gid = codeValueService.get(emo, String.class);
            if (StringUtils.hasText(gid)) {
                FileInfo fileItem = fileInfoService.get(gid);
                if (fileItem != null) {
                    emo = String.format("<img src='%s'>", fileItem.getFileUrl());
                }
            }
            extraData.put(item.getDate(), emo);
        });

        model.addAttribute("year", year);
        model.addAttribute("month", month);

//        Member member = memberUtil.getMember();
//        List<Diary> diaryList = infoService.getList(member);
//
//        /* 날짜마다 감정 이미지 삽입 */
//        for (Diary diary : diaryList) {
//            String sentimentString = "";
//            String diarySentiment = infoService.getMostFrequentSentiment(member, diary.getDate());
//
//            /* 추후에 수정 필요 - 감정 결과에 따라 이미지 다르게 띄우기 */
//            switch (diarySentiment) {
//                case "기쁨": sentimentString = "happiness"; break;
//                default: break;
//            }
//
//            extraData.put(diary.getDate(), "<img src='/common/images/sentiments/" + sentimentString + ".png'>");
//        }

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

    @GetMapping("/recommend/{seq}")
    public String content(@PathVariable("seq") Long seq, Model model) {
        Music item = recommendService.get(seq);

        model.addAttribute("item", item);

        return utils.tpl("diary/recommend");
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
            pageTitle = utils.getMessage("일기_작성하기");
            addScript.add("diary/sentiment");
            addScript.add("diary/diary");
            addCss.add("diary/diary");
        } else if (mode.equals("result")) {
            pageTitle = utils.getMessage("일기_분석_결과");
            addScript.add("diary/calendar");
            addCommonScript.add("modal");
        } else if (mode.equals("calendar")) {
            pageTitle = utils.getMessage("일기_목록");
            addScript.add("diary/calendar");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("pageTitle", pageTitle);
    }
}
