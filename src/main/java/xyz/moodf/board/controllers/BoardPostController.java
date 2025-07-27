package xyz.moodf.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import xyz.moodf.admin.board.entities.Board;
import xyz.moodf.admin.board.services.BoardConfigInfoService;
import xyz.moodf.board.entities.BoardData;
import xyz.moodf.board.search.BoardSearch;
import xyz.moodf.board.services.BoardDataInfoService;
import xyz.moodf.board.services.BoardDataUpdateService;
import xyz.moodf.board.services.BoardPermissionService;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.exceptions.UnAuthorizedException;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.global.search.ListData;
import xyz.moodf.member.libs.MemberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/board")
@SessionAttributes({"board"})
public class BoardPostController {
    private final Utils utils;
    private final MemberUtil memberUtil;
    private final BoardDataInfoService InfoService;
    private final BoardPermissionService permissionService;
    private final BoardConfigInfoService configInfoService;
    private final BoardDataUpdateService updateService;
    private final xyz.moodf.board.validators.BoardDataValidator boardDataValidator;

    @ModelAttribute("board")
    public Board getBoard() {
        return new Board();
    }

    // 게시글 목록
    @GetMapping("/list/{bid}")
    public String list(@PathVariable("bid") String bid, @ModelAttribute BoardSearch search, Model model) {
        commonProcess(bid, "list", model);
        ListData<BoardData> data = InfoService.getList(search, bid);
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
    public String update(@PathVariable("seq") Long seq, Model model) {
        commonProcess(seq, "update", model);

        return utils.tpl("board/update");
    }

    // 게시글 보기
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model) {
        commonProcess(seq, "view", model);

        return utils.tpl("board/view");
    }

    // 게시글 삭제
    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable("seq") Long seq, Model model, @SessionAttribute("board") Board board) {
        commonProcess(seq, "delete", model);

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

        addScript.add("board/common"); // 스킨 상관없는 공통 자바스크립트

        if (mode.equals("write") || mode.equals("update")) { // 등록, 수정
            if (board.isAttachFile() || (board.isImageUpload() && board.isEditor())) {
                addCommonScript.add("fileManager");
            }

            if (board.isEditor()) { // 에디터를 사용하는 경우, CKEDITOR5 스크립트를 추가
                addCommonScript.add("ckeditor5/ckeditor");
            }

            addScript.add(String.format("board/%s/form", skin)); // 스킨별 양식 관련 자바스크립트
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
        BoardData boardData = InfoService.get(seq);
        Board board = boardData.getBoard();

        mode = StringUtils.hasText(mode) ? mode : "view";

        List<String> addCommonScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        String pageTitle = board.getName(); // 게시판 명

        String skin = board.getSkin();
        addCss.add("board/style"); // 스킨과 상관없는 공통 스타일
        addCss.add(String.format("board/%s/style", skin)); // 스킨별 스타일

        addScript.add("board/common"); // 스킨 상관없는 공통 자바스크립트

        if (mode.equals("update")) { // 수정 모드
            // 수정 폼에 필요한 스크립트와 CSS 추가
            if (board.isAttachFile() || (board.isImageUpload() && board.isEditor())) {
                addCommonScript.add("fileManager");
            }

            if (board.isEditor()) { // 에디터를 사용하는 경우, CKEDITOR5 스크립트를 추가
                addCommonScript.add("ckeditor5/ckeditor");
            }

            addScript.add(String.format("board/%s/form", skin)); // 스킨별 양식 관련 자바스크립트

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
            pageTitle = board.getName() + " - 글 수정";

        } else if (mode.equals("view")) { // 조회 모드
            // 조회수 증가 (별도 서비스에서 처리하는 것이 좋음)
            addScript.add(String.format("board/%s/view", skin));
            addScript.add(String.format("board/%s/form", skin));
            pageTitle = boardData.getSubject() + " - " + board.getName();

            model.addAttribute("canEdit", permissionService.canEdit(boardData));
            model.addAttribute("canDelete", permissionService.canDelete(boardData));
            model.addAttribute("isGuest", boardData.getMember() == null);

        } else if (mode.equals("delete")) { // 삭제 모드
            // 삭제 권한 확인 로직이 여기에 들어갈 수 있음
            // 실제 삭제 처리는 별도 서비스에서 수행
            pageTitle = board.getName() + " - 글 삭제";
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("board", board);
        model.addAttribute("boardData", boardData);
    }


}
