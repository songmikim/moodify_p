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
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.DiaryId;
import xyz.moodf.diary.entities.RecMusic;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.diary.repositories.SentimentRepository;
import xyz.moodf.diary.services.DiaryInfoService;
import xyz.moodf.diary.services.DiaryService;
import xyz.moodf.diary.services.RecommendService;
import xyz.moodf.diary.services.SentimentService;
import xyz.moodf.diary.validators.DiaryValidator;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.codevalue.services.CodeValueService;
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

    private final DiaryService diaryService;
    private final DiaryInfoService infoService;
    private final SentimentService sentimentService;
    private final RecommendService recommendService;

    private final DiaryValidator diaryValidator;

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

        Diary diary = diaryRepository.findById(new DiaryId(member, date))
                .orElse(null);
        boolean isSaved = false;

        if (diary == null) {

            DiaryRequest form = new DiaryRequest();
            form.setDate(date);
            form.setWeather(Weather.NULL);
            form.setGid(UUID.randomUUID().toString());

            model.addAttribute("diaryRequest", form);

        } else {

            request.setDate(diary.getDate());
            request.setTitle(diary.getTitle());
            request.setWeather(diary.getWeather());
            request.setContent(diary.getContent());
            request.setGid(diary.getGid());

            sentimentService.resetDone(diary.getGid());  // 다시 감정 분석할 수 있게 done을 false로 변환

            isSaved = true;
            model.addAttribute("diaryRequest", request);
        }

        model.addAttribute("today", LocalDate.now());
        model.addAttribute("weatherValues", Weather.values());
        model.addAttribute("isSaved", isSaved);
        model.addAttribute("date", request.getDate());


        return utils.tpl("diary/diary");
    }

    @PostMapping("/{date}")
    public String process(@Valid @ModelAttribute("diaryRequest") DiaryRequest form, Errors errors, Model model) {
        commonProcess("diary", model);

        diaryValidator.validate(form, errors);

        if (errors.hasErrors()) {
            model.addAttribute("isSaved", false);
            model.addAttribute("today", LocalDate.now());
            model.addAttribute("date", form.getDate());
            model.addAttribute("weatherValues", Weather.values());
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

        // 추천 콘텐츠 리스트 저장 후 불러오기
        RecMusic recMusic = recommendService.process(diary.getGid(), sentiment);
        List<Music> items = recommendService.recMusicToMusicList(recMusic);
        model.addAttribute("items", items);

        // 캘린더로 이동 시 사용할 쿼리스트링 구하기
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
                emo = utils.printThumb(gid, 50, 50);
            }
            extraData.put(item.getDate(), emo);
        });

        model.addAttribute("year", year);
        model.addAttribute("month", month);

        return utils.tpl("diary/calendar");
    }

    @PostMapping("/save")
    public String saveDiary(@ModelAttribute DiaryRequest diaryRequest,
                            @RequestParam("gid") String gid,
                            Model model) {

        Member member = memberUtil.getMember();

        diaryService.process(diaryRequest, member);

        return "redirect:/diary/result";
    }

    @PostMapping("/delete/{gid}")
    @ResponseBody
    public ResponseEntity<?> deleteDiary(@PathVariable("gid") String gid,
                              @RequestBody Map<String, String> payload) {

        String dateStr = payload.get("date");
        LocalDate date = LocalDate.parse(dateStr);

        diaryService.delete(gid, date);

        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteSentiment(@RequestBody Map<String, String> payload) {
        String gid = payload.get("gid");
        System.out.println("삭제요청: " + gid);
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
            addCss.add("diary/result");
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
