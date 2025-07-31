package xyz.moodf.board.comment.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.moodf.board.comment.entities.Comment;
import xyz.moodf.board.comment.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentDeleteService {
    private final CommentRepository repository;
    private final CommentInfoService infoService;

    public void delete(Long seq) {
        Comment comment = infoService.get(seq);

        // 소프트 삭제
        comment.setDeleted(true);

        repository.save(comment);
    }
}
