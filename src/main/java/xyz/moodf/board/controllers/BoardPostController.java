package xyz.moodf.board.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import xyz.moodf.admin.board.entities.Board;
import xyz.moodf.admin.board.services.BoardConfigInfoService;
import xyz.moodf.board.comment.controllers.CommentController;
import xyz.moodf.board.comment.search.CommentSearch;
import xyz.moodf.board.entities.BoardData;
import xyz.moodf.board.search.BoardSearch;
import xyz.moodf.board.services.BoardDataDeleteService;
import xyz.moodf.board.services.BoardDataInfoService;
import xyz.moodf.board.services.BoardDataUpdateService;
import xyz.moodf.board.services.BoardPermissionService;
import xyz.moodf.board.validator.BoardDataValidator;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.global.search.ListData;
import xyz.moodf.member.libs.MemberUtil;

import java.util.*;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/board")
@SessionAttributes({"board"})
public class BoardPostController {
    private final Utils utils;
    private final MemberUtil memberUtil;
    private final BoardDataInfoService infoService;
    private final BoardPermissionService permissionService;
    private final BoardConfigInfoService configInfoService;
    private final BoardDataUpdateService updateService;
    private final BoardDataDeleteService deleteService;
    private final BoardDataValidator boardDataValidator;
    private final CommentController comment;

    @ModelAttribute("board")
    public Board getBoard() {
        return new Board();
    }

    // 게시글 목록
    @GetMapping("/list/{bid}")
    public String list(@PathVariable("bid") String bid, @ModelAttribute BoardSearch search, Model model) {
        commonProcess(bid, "list", model);
        ListData<BoardData> data = infoService.getList(search, bid);
        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return utils.tpl("board/list");
    }

    // 게시글 작성
    @GetMapping("/write/{bid}")
    public String write(@PathVariable("bid") String bid, RequestPostBoard form, Model model) {
        commonProcess(bid, "write", model);

        form.setBid(bid);
        form.setGid(UUID.randomUUID().toString());
        model.addAttribute("requestPostBoard", form);
        Board board = configInfoService.get(bid);

        if (!permissionService.canPost(board)) {
            return "redirect:/board/list/" + bid + "?error=unauthorized";
        }

        if (memberUtil.isLogin()) {
            form.setPoster(memberUtil.getMember().getName());
        } else {
            form.setGuest(true);
        }

        return utils.tpl("board/write");
    }

    // 게시글 저장
    @PostMapping("/save")
    public String save(@Valid RequestPostBoard form, Errors errors, Model model) {
        String mode = form.getMode();
        String bid = form.getBid();
        mode = StringUtils.hasText(mode) ? mode : "register";

        commonProcess(bid, mode, model);

        boardDataValidator.validate(form, errors);

        // 에러가 있으면 저장하지 말고 바로 폼으로 돌아가기
        if (errors.hasErrors()) {
            return utils.tpl("board/" + mode); // utils.tpl() 사용
        }

        // 에러가 없을 때만 저장
        updateService.process(form);

        return "redirect:/board/list/" + bid; // {bid} 말고 직접 붙이기
    }

    // 게시글 수정
    @GetMapping("/update/{seq}")
    public String update(@PathVariable("seq") Long seq, HttpSession session, Model model) {
        BoardData boardData = infoService.get(seq);
        commonProcess(seq, "update", model);



        if (!permissionService.canEdit(boardData)) {
            return "redirect:/board/view/" + seq + "?error=unauthorized";
        }

        // 🔒 비회원 글인 경우 세션 검증
        if (boardData.getMember() == null) {
            String sessionKey = "guest_verified_" + seq + "_update";
            Long expireTime = (Long) session.getAttribute(sessionKey);
            if (expireTime == null || System.currentTimeMillis() > expireTime) {
                return "redirect:/board/view/" + seq + "?needPassword=true";
            }
        }

        return utils.tpl("board/update");
    }

    // 게시글 보기
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model) {
        commonProcess(seq, "view", model);

        BoardData boardData = infoService.get(seq);
        if (!permissionService.canView(boardData)) {
            return "redirect:/board/list/" + boardData.getBoard().getBid() + "?error=access_denied";
        }

        CommentSearch search= new CommentSearch();
        comment.comment(seq, model, search);

        return utils.tpl("board/view");
    }

    // 게시글 삭제
    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable("seq") Long seq, Model model, @SessionAttribute("board") Board board,HttpSession session) {
        commonProcess(seq, "delete", model);
        BoardData boardData = infoService.get(seq);
        if (!permissionService.canDelete(boardData)) {
            return "redirect:/error/forbidden";
        }
        if (boardData.getMember() == null && !memberUtil.isAdmin()) {
            String sessionKey = "guest_verified_" + seq + "_delete";
            Long expireTime = (Long) session.getAttribute(sessionKey);
            if (expireTime == null || System.currentTimeMillis() > expireTime) {
                return "redirect:/board/view/" + seq + "?needPassword=true";
            }

            // 🔧 사용 후 세션 제거
            session.removeAttribute(sessionKey);
        }
        deleteService.softDelete(boardData.getSeq());

        return "redirect:/board/list/" + board.getBid();
    }

    /**
     * bid 기준의 공통 처리
     *  - 게시글 설정조회가 공통 처리
     *
     * @param bid
     * @param mode
     * @param model
     */
    private void commonProcess(String bid, String mode, Model model) {
        Board board = configInfoService.get(bid);
        mode = StringUtils.hasText(mode) ? mode : "list";

        List<String> addCommonScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        String pageTitle = board.getName(); // 게시판 명

        String skin = board.getSkin();
        addCss.add("board/style"); // 스킨과 상관없는 공통 스타일
        addCss.add(String.format("board/%s/style", skin)); // 스킨별 스타일
        addCss.add(String.format("board/%s/%s", skin, mode)); // 모드별 스타일
        if (mode.equals("update")){
            addCss.add(String.format("board/%s/write", skin)); // 모드별 스타일
            addScript.add(String.format("board/%s/write", skin)); // 모드별 스타일
        }

        addScript.add("board/common"); // 스킨 상관없는 공통 자바스크립트

        if (mode.equals("write") || mode.equals("view") || mode.equals("update")) {
            if (mode.equals("write")) {
                // write 모드 전용 스크립트
                if (board.isAttachFile() || (board.isImageUpload() && board.isEditor())) {
                    addCommonScript.add("fileManager");
                }
                if (board.isEditor()) {
                    addCommonScript.add("ckeditor5/ckeditor");
                }
            }

            // 🎯 공통으로 form.js 추가
            addScript.add(String.format("board/%s/form", skin));
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("board", board);
    }

    /**
     * seq 기준의 공통 처리
     *  - 게시글 조회가 공통 처리
     * @param seq
     * @param mode
     * @param model
     */
    private void commonProcess(Long seq, String mode, Model model) {
        // 게시글 정보 조회
        BoardData boardData = infoService.get(seq);
        Board board = boardData.getBoard();
        String pageTitle = board.getName();

        commonProcess(board.getBid(),mode,model);

        if (mode.equals("update")) { // 수정 모드

            // 수정 폼 데이터 설정
            RequestPostBoard form = new RequestPostBoard();
            form.setMode("update");
            form.setSeq(boardData.getSeq());
            form.setBid(board.getBid());
            form.setGid(boardData.getGid());
            form.setPoster(boardData.getPoster());
            form.setSubject(boardData.getSubject());
            form.setContent(boardData.getContent());
            form.setNotice(boardData.isNotice());
            form.setSecret(boardData.isSecret());
            form.setGuest(boardData.getMember() == null); // 비회원 글인지 확인

            model.addAttribute("requestPostBoard", form);

        } else if (mode.equals("view")) { // 조회 모드
            // 조회수 증가 (별도 서비스에서 처리하는 것이 좋음)
            model.addAttribute("canEdit", permissionService.canEdit(boardData));
            model.addAttribute("canDelete", permissionService.canDelete(boardData));
            model.addAttribute("isGuest", permissionService.isGuest(boardData));
            model.addAttribute("memberUtil", memberUtil);

            pageTitle = boardData.getSubject() + " - " + board.getName();
        }
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("boardData", boardData);
    }

    @PostMapping("/check-guest-password")
    @ResponseBody
    public Map<String, Object> checkGuestPassword(@RequestParam Long seq, @RequestParam String password,@RequestParam String action, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            BoardData boardData = infoService.get(seq);

            // guestPwCheck 메서드 사용
            if (permissionService.guestPwCheck(boardData, password)) {
                String sessionKey = "guest_verified_" + seq + "_" + action;
                long expireTime = System.currentTimeMillis() + (5 * 60 * 1000); // 5분
                session.setAttribute(sessionKey, expireTime);
                result.put("success", true);
                result.put("message", "비밀번호가 일치합니다");
            } else {
                result.put("success", false);
                result.put("message", "비밀번호가 틀렸습니다");
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "오류가 발생했습니다");
        }

        return result;
    }
}
