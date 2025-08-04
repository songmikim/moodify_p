package xyz.moodf.main.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.libs.MemberUtil;

@Controller
@RequestMapping("/")
@ApplyCommonController
@RequiredArgsConstructor
public class MainController {

    private final Utils utils;
    private final MemberUtil memberUtil;

    @GetMapping
    public String index(@RequestParam(value = "redirectUrl", required = false) String redirectUrl) {
        if (StringUtils.hasText(redirectUrl) && !redirectUrl.equals("/")) {
            if (memberUtil.isLogin()) {
                return "redirect:" + redirectUrl;
            } else {
                return "redirect:/login?redirectUrl=" + redirectUrl;
            }
        } else {
            if (memberUtil.isLogin()) {
                return "redirect:/diary";
            } else {
                return "redirect:/login";
            }
        }
    }
}
