package xyz.moodf.admin.member.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.admin.global.controllers.CommonController;
import xyz.moodf.global.annotations.ApplyCommonController;

@Controller("adminMemberController")
@ApplyCommonController
@RequestMapping("/admin/member")
public class MemberController extends CommonController {

    @Override
    @ModelAttribute("mainCode")
    public String mainCode() {
        return "member";
    }

    @GetMapping
    public String basicConfig(Model model) {
        commonProcess("member", model);

        return "admin/member/index";
    }


    private void commonProcess(String code, Model model) {

        model.addAttribute("subCode", code);
    }
}
