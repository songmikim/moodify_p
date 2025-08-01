package xyz.moodf.board.comment.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.moodf.board.comment.entities.Comment;
import xyz.moodf.board.comment.search.CommentSearch;
import xyz.moodf.board.comment.services.CommentDeleteService;
import xyz.moodf.board.comment.services.CommentInfoService;
import xyz.moodf.board.comment.services.CommentUpdateService;
import xyz.moodf.board.entities.BoardData;
import xyz.moodf.board.services.BoardPermissionService;
import xyz.moodf.global.search.ListData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/comment")
public class CommentController {
    private final CommentUpdateService updateService;
    private final CommentDeleteService deleteService;
    private final CommentInfoService infoService;
    private final BoardPermissionService permissionService;

    @PostMapping("/save")
    public String save(@Valid RequestComment form, RedirectAttributes redirectAttributes) {
        try {
            updateService.process(form);
            redirectAttributes.addFlashAttribute("message", "댓글이 등록되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "댓글 등록에 실패했습니다.");
        }
        return "redirect:/board/view/" + form.getBoardDataSeq();
    }

    @PostMapping("/delete/{seq}")
    public String delete(@PathVariable Long seq, RedirectAttributes redirectAttributes) {
        try {
            Comment comment = infoService.get(seq);
            deleteService.delete(seq);
            redirectAttributes.addFlashAttribute("message", "댓글이 삭제되었습니다.");
            return "redirect:/board/view/" + comment.getBoardDataSeq();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "댓글 삭제에 실패했습니다.");
            return "redirect:/";
        }
    }

    public void comment(@PathVariable Long seq, Model model, CommentSearch search) {
        ListData<Comment> CommentList=infoService.getList(seq,search);
        model.addAttribute("commentList",CommentList.getItems());
        System.out.println(CommentList);

        RequestComment newComment = new RequestComment();
        newComment.setBoardDataSeq(seq); // 초기값 세팅 가능
        model.addAttribute("requestComment", newComment);

    }

    @PostMapping("/check-guest-password")
    @ResponseBody
    public Map<String, Object> checkGuestPassword(@RequestParam Long seq, @RequestParam String password, @RequestParam String action, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("ㅠㅣ카츄");
        try {
            Comment comment = infoService.get(seq);
            // guestPwCheck 메서드 사용
            if (permissionService.guestPwCheck(comment, password)) {
                System.out.println("피카");
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
