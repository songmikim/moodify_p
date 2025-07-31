package xyz.moodf.terms.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.global.libs.Utils;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermsController {
    private final Utils utils;

    @GetMapping("/privacy")
    public String privacyUsage(Model model){
        commonProcess(model, "privacy");
        return utils.tpl("terms/privacyUsage");
    }
    @GetMapping("/guide")
    public String guide(Model model){
        commonProcess(model, "guide");
        return utils.tpl("terms/guide");
    }
    private void commonProcess(Model model, String mode) {
        String pageTitle = "";
        mode = StringUtils.hasText(mode) ? mode : "privacy";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();

        if (mode.equals("privacy")){
            addCss.add("terms/privacyUsage");
        } else if (mode.equals("guide")){
            addCss.add("terms/guide");
        }

        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("pageTitle", pageTitle);
    }
}
