package xyz.moodf.board.comment.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.moodf.board.comment.entities.Comment;
import xyz.moodf.board.comment.search.CommentSearch;
import xyz.moodf.board.comment.services.CommentDeleteService;
import xyz.moodf.board.comment.services.CommentInfoService;
import xyz.moodf.board.comment.services.CommentUpdateService;
import xyz.moodf.global.search.ListData;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("board/comment")
public class CommentController {
    private final CommentUpdateService updateService;
    private final CommentDeleteService deleteService;
    private final CommentInfoService infoService;

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
}
