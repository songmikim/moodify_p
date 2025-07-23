package xyz.moodf.admin.board.notice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.admin.global.controllers.CommonController;
import xyz.moodf.global.annotations.ApplyCommonController;

@Controller
@ApplyCommonController
@RequestMapping("/admin/board/notice")
public class NoticeBoardController extends CommonController {

    @Override
    @ModelAttribute("mainCode")
    public String mainCode() {
        return "noticeBoard";
    }

    @GetMapping
    public String basicConfig(Model model) {
        commonProcess("noticeBoard", model);

        return "admin/board/notice/index";
    }


    private void commonProcess(String code, Model model) {

        model.addAttribute("subCode", code);
    }
}
