package xyz.moodf.mypage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.libs.MemberUtil;
import xyz.moodf.mypage.entities.RequestPasswordChange;
import xyz.moodf.mypage.services.MyPageService;
import xyz.moodf.mypage.validators.PasswordChangeValidator;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mypage")
@ApplyCommonController
@RequiredArgsConstructor
public class MyPageController {

    private final Utils utils;
    private final MemberUtil memberUtil;
    private final MyPageService service;
    private final PasswordChangeValidator passwordChangeValidator;

    @GetMapping
    public String index(Model model) {
        commonProcess("index", model);
        model.addAttribute("member", memberUtil.getMember());
        return utils.tpl("mypage/index");
    }

    @GetMapping("/password")
    public String passwordForm(@ModelAttribute("form") RequestPasswordChange form, Model model) {
        commonProcess("password", model);
        return utils.tpl("mypage/password");
    }

    @PostMapping("/password")
    public String passwordSubmit(@ModelAttribute("form") RequestPasswordChange form, Errors errors, Model model) {
        commonProcess("password", model);
        passwordChangeValidator.validate(form, errors);
        if (errors.hasErrors()) {
            return utils.tpl("mypage/password");
        }

        boolean result = service.changePassword(memberUtil.getMember(), form.getCurrentPassword(), form.getPassword());
        if (!result) {
            errors.rejectValue("currentPassword", "Mismatch.currentPassword");
            return utils.tpl("mypage/password");
        }

        return "redirect:/mypage";
    }

    /**
     *
     * @return
     */
    @PostMapping("/delete")
    public String deleteAccount() {
        service.deleteAccount(memberUtil.getMember());
        return "redirect:/logout";
    }

    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "index";
        String pageTitle = "";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        if (mode.equals("index")) {
            pageTitle = utils.getMessage("마이페이지");
        } else if (mode.equals("password")) {
            pageTitle = utils.getMessage("비밀번호_변경");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("addCss", addCss);
        model.addAttribute("pageTitle", pageTitle);
    }
}