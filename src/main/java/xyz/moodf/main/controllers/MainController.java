package xyz.moodf.main.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;

@Controller
@RequestMapping("/")
@ApplyCommonController
@RequiredArgsConstructor
public class MainController {

    private final Utils utils;

    @GetMapping
    public String index() {
        return utils.tpl("main/index");
    }
}
