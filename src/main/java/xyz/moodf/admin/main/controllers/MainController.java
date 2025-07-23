package xyz.moodf.admin.main.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.moodf.global.annotations.ApplyCommonController;

@RequestMapping("/admin")
@ApplyCommonController
@Controller("adminMainController")
public class MainController {

    @GetMapping
    public String index() {
        return "admin/main/index";
    }
}
