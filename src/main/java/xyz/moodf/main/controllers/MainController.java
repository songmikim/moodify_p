package xyz.moodf.main.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.libs.MemberUtil;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
@ApplyCommonController
@RequiredArgsConstructor
public class MainController {

    private final Utils utils;
    private final MemberUtil memberUtil;

    @GetMapping
    public String RedirectwLogin(Model model){
        common(model);
        boolean isLogin = memberUtil.isLogin();
        if (isLogin) {
            return "redirect:/diary"; // 로그인 되어 있으면 메인 페이지로 리다이렉트
        } else {
            return utils.tpl("/login");
        }
    }


    void common(Model model) {
        List<String> addCommonScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();
        addCss.add("main/style");
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonCss", addCommonCss);
    }
}
