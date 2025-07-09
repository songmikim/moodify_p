package xyz.moodf.main.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
@ApplyCommonController
@RequiredArgsConstructor
public class MainController {

    private final Utils utils;

    @GetMapping
    public String index(Model model) {
        List<String> addCommonScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();
        addCss.add("main/style");
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonCss", addCommonCss);
        return utils.tpl("main/index");
    }
}
