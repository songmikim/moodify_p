package xyz.moodf.mypage.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
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

    /**
     * 마이페이지 메인 화면을 렌더링하는 메서드
     * - 공통 속성 설정을 위해 commonProcess 호출
     * - 현재 로그인된 회원 정보를 모델에 담음
     * - 뷰 템플릿은 "mypage/index"
     * @param model
     * @return
     */
    @GetMapping
    public String index(Model model) {
        commonProcess("index", model);
        model.addAttribute("member", memberUtil.getMember());
        return utils.tpl("mypage/index");
    }

    /**
     * 비밀번호 변경 폼 화면을 보여주는 메서드 (GET 요청)
     * - 비어 있는 비밀번호 변경 폼 객체를 모델에 등록
     * - 공통 속성 설정을 위해 commonProcess 호출
     * - 뷰 템플릿은 "mypage/password"
     * @param form
     * @param model
     * @return
     */
    @GetMapping("/password")
    public String passwordForm(@ModelAttribute("form") RequestPasswordChange form, Model model) {
        commonProcess("password", model);
        return utils.tpl("mypage/password");
    }

    /**
     * 비밀번호 변경 요청을 처리하는 메서드 (POST 요청)
     * - 입력값 유효성 검증 수행 (PasswordChangeValidator)
     * - 유효성 에러가 있을 경우 비밀번호 변경 폼으로 다시 이동
     * - 현재 비밀번호 검증 및 비밀번호 변경 로직 수행
     * - 실패 시 에러 메시지 출력 후 폼 유지, 성공 시 마이페이지로 리다이렉트
     * @param form
     * @param errors
     * @param model
     * @return
     */
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
     * 회원 탈퇴 요청 성공 후, 안내 화면으로 이동
     * @return
     */
    @PostMapping("/delete")
    public String deleteRequest(Model model) {
        service.requestDelete(memberUtil.getMember());
        commonProcess("delete", model);
        return utils.tpl("mypage/delete_requested");
    }

    /**
     * 이메일 링크를 통해 탈퇴 최종 확인을 처리
     * - 토큰으로 탈퇴 여부를 확인 및 처리
     * - 성공 시 세션 만료 처리
     * - 탈퇴 완료 안내 페이지로 이동
     * @param token
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/delete/confirm")
    public String deleteConfirm(@RequestParam("token") String token, Model model, HttpServletRequest request) {
        boolean result = service.confirmDelete(token);
        if (result) {
            request.getSession().invalidate();
        }
        commonProcess("delete", model);
        return utils.tpl("mypage/deleted");
    }

    /**
     *각 화면 공통 처리 메서드
     * - 페이지 모드별 타이틀 설정
     * - 공통/개별 JS, CSS 리스트 초기화
     * - 모델에 공통 속성 추가
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "index";
        String pageTitle = "";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        if (mode.equals("index")) {
            pageTitle = utils.getMessage("마이페이지");
            addCommonScript.add("chart/chart.umd");
            addScript.add("mypage/emotion-chart");
            addScript.add("mypage/recommend-songs");
            addCss.add("mypage/mypage");
            addScript.add("member/deleteForm");
        } else if (mode.equals("password")) {
            pageTitle = utils.getMessage("비밀번호_변경");
        } else if (mode.equals("delete")) {
            pageTitle = utils.getMessage("탈퇴하기");
            addScript.add("member/deleteForm");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("addCss", addCss);
        model.addAttribute("pageTitle", pageTitle);
    }
}