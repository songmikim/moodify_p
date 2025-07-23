package xyz.moodf.diary.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.moodf.diary.services.DiaryService;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.entities.Member;

import java.util.ArrayList;
import java.util.List;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/diary")
@SessionAttributes("requestLogin")
public class DiaryController {

    private final Utils utils;
    private final DiaryService service;

    @GetMapping({"", "/"})
    public String diary(Model model) {
        commonProcess("member", model);

        return utils.tpl("diary/diary");
    }

    @GetMapping("/result")
    public String result(Model model) {
        commonProcess("member", model);

        return utils.tpl("diary/result");
    }

    @PostMapping("/write")
    public String saveDiary(@RequestParam String title,
                            @RequestParam String content,
                            @SessionAttribute("requestLogin") Member member,
                            Model model) {
        service.saveDiary(title, content, member);

        return "redirect:/diary/result";
    }


    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "member";
        String pageTitle = "";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        if (mode.equals("member")) {
            addCommonScript.add("fileManager");
            addScript.add("/diary");
            pageTitle = utils.getMessage("일기쓰기");

        } else if (mode.equals("login")) { // 로그인 공통 처리
            pageTitle = utils.getMessage("일기장_관리");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageTitle", pageTitle);
    }
}
