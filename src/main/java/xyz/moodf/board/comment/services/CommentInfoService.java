package xyz.moodf.board.comment.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import xyz.moodf.board.comment.entities.Comment;
import xyz.moodf.board.comment.entities.QComment;
import xyz.moodf.board.comment.repositories.CommentRepository;
import xyz.moodf.board.comment.search.CommentSearch;
import xyz.moodf.global.search.ListData;
import xyz.moodf.global.search.Pagination;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

@Service
@RequiredArgsConstructor
public class CommentInfoService {
    private final HttpServletRequest request;
    private final CommentRepository repository;

    public Comment get(Long seq) {
        return repository.findById(seq)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
    }

    public ListData<Comment> getList(Long boardDataSeq, CommentSearch search) {
        // QueryDSL로 특정 게시글의 댓글 목록 조회
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();

        limit = limit < 1 ? 20 : limit;

        BooleanBuilder andBuilder = new BooleanBuilder();
        QComment comment = QComment.comment;

        //특정 seq의 게시글에 달린 댓글만
        andBuilder.and(comment.boardDataSeq.eq(boardDataSeq));
        //deletedAt 필터링
        andBuilder.and(comment.deleted.isFalse());

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));
        Page<Comment> data = repository.findAll(andBuilder, pageable);
        List<Comment> items = data.getContent();

        int total = (int)data.getTotalElements();
        Pagination pagination = new Pagination(page, total, 10, limit, request);

        return new ListData<>(items, pagination);
    }
}