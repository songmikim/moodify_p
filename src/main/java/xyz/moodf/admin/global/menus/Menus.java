package xyz.moodf.admin.global.menus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menus {
    private static Map<String, List<Menu>> menus = new HashMap<>();

    static {
        // 사이트 설정 메뉴
        menus.put("basic", List.of(
                new Menu("basic", "기본설정", "/admin/config"),
                new Menu("terms", "약관설정", "/admin/config/terms")
        ));

        // 회원 관리 메뉴
        menus.put("member", List.of(
                new Menu("list", "회원목록", "/admin/member")
        ));

        // 공지 게시판 관리 메뉴
        menus.put("noticeBoard", List.of(
                new Menu("noticeList", "게시판 목록", "/admin/board/notice"),
                new Menu("noticeRegister", "게시판 등록", "/admin/board/notice/register"),
                new Menu("noticePosts", "게시글 관리", "/admin/board/notice/posts")
        ));

        // 건의 게시판 관리 메뉴
        menus.put("suggestionBoard", List.of(
                new Menu("suggestionList", "게시판 목록", "/admin/board/suggestion"),
                new Menu("suggestionRegister", "게시판 등록", "/admin/board/suggestion/register"),
                new Menu("suggestionPosts", "게시글 관리", "/admin/board/suggestion/posts")
        ));

    }

    /**
     * 주 메뉴 코드로 서브 메뉴를 조회
     *
     * @param mainCode
     * @return
     */
    public static List<Menu> getMenus(String mainCode) {
        return menus.getOrDefault(mainCode, List.of());
    }
}