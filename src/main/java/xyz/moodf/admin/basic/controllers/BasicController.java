package xyz.moodf.admin.basic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.admin.global.controllers.CommonController;
import xyz.moodf.global.annotations.ApplyCommonController;

@Controller
@ApplyCommonController
@RequestMapping("/admin/basic")
public class BasicController extends CommonController {

    @Override
    @ModelAttribute("mainCode")
    public String mainCode() {
        return "basic";
    }

    @GetMapping
    public String basicConfig(Model model) {
        commonProcess("basic", model);

        return "admin/basic/index";
    }


    private void commonProcess(String code, Model model) {

        model.addAttribute("subCode", code);
    }
}
