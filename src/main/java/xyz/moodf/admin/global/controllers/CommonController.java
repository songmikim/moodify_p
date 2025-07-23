package xyz.moodf.admin.global.controllers;

import org.springframework.web.bind.annotation.ModelAttribute;
import xyz.moodf.admin.global.menus.Menu;
import xyz.moodf.admin.global.menus.Menus;

import java.util.List;

public abstract class CommonController {
    // 주메뉴 코드 - 각 컨트롤러가 메서드 재정의를 통해서 정의
    public abstract String mainCode();

    @ModelAttribute("subMenus")
    public List<Menu> subMenus() {
        return Menus.getMenus(mainCode());
    }
}
