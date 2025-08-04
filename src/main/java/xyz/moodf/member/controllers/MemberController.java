package xyz.moodf.member.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.file.constants.FileStatus;
import xyz.moodf.global.file.entities.FileInfo;
import xyz.moodf.global.file.services.FileInfoService;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.services.FindPwService;
import xyz.moodf.member.services.JoinService;
import xyz.moodf.member.social.constants.SocialType;
import xyz.moodf.member.social.services.KakaoLoginService;
import xyz.moodf.member.social.services.NaverLoginService;
import xyz.moodf.member.validators.JoinValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@ApplyCommonController
@RequestMapping
@SessionAttributes({"requestLogin", "EmailAuthVerified"})
public class MemberController {

    private final Utils utils;
    private final JoinValidator joinValidator;
    private final JoinService joinService;
    private final KakaoLoginService kakaoLoginService;
    private final NaverLoginService naverLoginService;
    private final FileInfoService fileInfoService;
    private final FindPwService findPwService;

    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("member/style");
    }

    @ModelAttribute("requestLogin")
    public RequestLogin requestLogin() {
        return new RequestLogin();
    }

    // 회원가입 양식
    @GetMapping("/join")
    public String join(@ModelAttribute RequestJoin form, Model model,
                       @SessionAttribute(name="socialType", required = false) SocialType type,
                       @SessionAttribute(name="socialToken", required = false) String socialToken) {

        commonProcess("join", model);

        form.setSocialType(type);
        form.setSocialToken(socialToken);
        form.setGid(UUID.randomUUID().toString());

        // 이메일 인증 여부 false로 초기화
        model.addAttribute("EmailAuthVerified", false);

        return utils.tpl("member/join");
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String joinPs(@Valid RequestJoin form, Errors errors, Model model, SessionStatus sessionStatus, @SessionAttribute(name = "EmailAuthVerified", required = false) Boolean emailVerified) {
        commonProcess("join", model);

        joinValidator.validate(form, errors);

        if (emailVerified == null || !emailVerified) {
            errors.reject("email", "이메일 인증을 완료해야 회원가입이 가능합니다.");
        }

        if (errors.hasErrors()) {

            // 프로필 이미지 유지 처리
            List<FileInfo> items = fileInfoService.getList(form.getGid(), null, FileStatus.ALL);
            form.setProfileImage(items == null || items.isEmpty() ? null : items.getFirst());

            return utils.tpl("member/join");
        }

        joinService.process(form);

        // EmailAuthVerified 세션값 비우기
        sessionStatus.setComplete();

        // 회원가입 성공시
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@ModelAttribute RequestLogin form, Errors errors, Model model) {
        commonProcess("login", model);

        /* 검증 실패 처리 S */
        List<String> fieldErrors = form.getFieldErrors();
        if (fieldErrors != null) {
            fieldErrors.forEach(s -> {
                // 0 - 필드, 1 - 에러코드
                String[] value = s.split("_");
                errors.rejectValue(value[0], value[1]);
            });

        }
        List<String> globalErrors = form.getGlobalErrors();
        if (globalErrors != null) {
            globalErrors.forEach(errors::reject);
        }
        /* 검증 실패 처리 E */

        /* 소셜 로그인 URL */
        model.addAttribute("kakaoLoginUrl", kakaoLoginService.getLoginUrl(StringUtils.hasText(form.getRedirectUrl()) ? form.getRedirectUrl() : "/diary"));
        model.addAttribute("naverLoginUrl", naverLoginService.getLoginUrl(StringUtils.hasText(form.getRedirectUrl()) ? form.getRedirectUrl() : "/diary"));

        return utils.tpl("main/login");
    }

    /**
     * 비밀번호 만료시 변경 페이지
     *
     * @param model
     * @return
     */
    @GetMapping("/password")
    public String password(Model model) {
        commonProcess("password", model);

        return utils.tpl("member/password");
    }

    /**
     * 현재 컨트롤러의 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "join";
        String pageTitle = "";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        if (mode.equals("join")) { // 회원 가입 공통 처리
            addCommonScript.add("fileManager");
            addScript.add("member/join");
            addScript.add("member/form");
            addCss.add("member/join");
            pageTitle = utils.getMessage("회원가입");

        } else if (mode.equals("login")) { // 로그인 공통 처리
            pageTitle = utils.getMessage("로그인");
            addCss.add("member/login");

        } else if (mode.equals("find_pw")) { // 비밀번호 찾기
            pageTitle = utils.getMessage("비밀번호 찾기");
            addCss.add("member/find_pw");

        } else if (mode.equals("find_pw_done")) { // 비밀번호 찾기 완료
            pageTitle = utils.getMessage("비밀번호 찾기 완료");
            addCss.add("member/find_pw_done");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("addCss", addCss);
        model.addAttribute("pageTitle", pageTitle);
    }

    /**
     * 비밀번호 찾기 양식
     * @param form
     * @param model
     * @return
     */
        @GetMapping("/find_pw")
    public String findPw(@ModelAttribute RequestFindPw form, Model model) {
        commonProcess("find_pw", model);

        return utils.tpl("member/find_pw");
    }

    /**
     * 비밀번호 찾기 처리
     * @param form
     * @param errors
     * @param model
     * @return
     */
    @PostMapping("/find_pw")
    public String findPwPs(@Valid RequestFindPw form, Errors errors, Model model) {
        commonProcess("find_pw", model);

        findPwService.process(form, errors); //비밀번호 찾기 처리

        if (errors.hasErrors()){
            return utils.tpl("member/find_pw");
        }

        // 비밀번호 찾기 성공 시 완료 페이지
        return "redirect:/find_pw_done";
    }

    @GetMapping("find_pw_done")
    public String findPwDone (Model model) {
        commonProcess("find_pw_done", model);

        return utils.tpl("member/find_pw_done");
    }




//    @ResponseBody
//    @GetMapping("/test")
//    public void test(Principal principal) {
//        String email = principal.getName();
//        System.out.println("email:" + email);
//    }

//    @ResponseBody
//    @GetMapping("/test")
//    public void test(@AuthenticationPrincipal MemberInfo memberInfo) {
//        System.out.println("memberInfo:" + memberInfo);
//    }

//    @ResponseBody
//    @GetMapping("/test")
//    public void test() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("인증상태:" + auth.isAuthenticated());
//        System.out.println("Principle:" + auth.getPrincipal());
//    }

//    @ResponseBody
//    @GetMapping("/test")
//    public void test() {
//        System.out.printf("로그인:%s, 관리자여부:%s, 회원정보:%s%n", memberUtil.isLogin(), memberUtil.isAdmin(), memberUtil.getMember());
//    }
}
