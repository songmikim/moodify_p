package xyz.moodf.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.board.validators.BoardValidator;
import xyz.moodf.global.controllers.CommonController;
import xyz.moodf.member.constants.Authority;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController extends CommonController {

    private final BoardValidator boardValidator;

    @Override
    @ModelAttribute("mainCode")
    public String mainCode() {
        return "board";
    }

    /**
     * 게시판 목록
     * @return
     */
    @GetMapping({"", "/list"})
    public String list(Model model) {
        commonProcess("list", model);

        return "admin/board/list";
    }

    /**
     * 게시판 등록
     *
     * @param model
     * @return
     */
    @GetMapping("/register")
    public String register(@ModelAttribute RequestBoard form, Model model) {
        commonProcess("register", model);

        // 기본값 설정
        form.setSkin("default");
        form.setListAuthority(Authority.ALL);
        form.setViewAuthority(Authority.ALL);
        form.setWriteAuthority(Authority.ALL);
        form.setCommentAuthority(Authority.ALL);
        form.setRowsForPage(20);
        form.setPageCount(10);

        return "admin/board/register";
    }

    @PostMapping("/save")
    public String save(@Valid RequestBoard form, Errors errors, Model model) {
        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode : "register";
        commonProcess(mode, model);

        boardValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return "admin/board/" + mode;
        }

        return "redirect:/admin/board";
    }

    /**
     * 컨트롤러 요청 처리 메서드의 공통 처리 부분
     * @param code
     * @param model
     */
    private void commonProcess(String code, Model model) {
        String pageTitle = "";
        code = StringUtils.hasText(code) ? code : "list";
        if (code.equals("register")) {
            pageTitle =  "게시판 등록";
        } else {
            pageTitle = "게시판 목록";
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("subCode", code);
    }
}