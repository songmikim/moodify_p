package xyz.moodf.admin.board.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.moodf.admin.board.controllers.RequestBoard;
import xyz.moodf.admin.board.entities.Board;
import xyz.moodf.admin.board.entities.QBoard;
import xyz.moodf.admin.board.repositories.BoardRepository;
import xyz.moodf.admin.board.exceptions.BoardNotFoundException;
import xyz.moodf.global.search.CommonSearch;
import xyz.moodf.global.search.ListData;
import xyz.moodf.global.search.Pagination;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardConfigInfoService {

    private final BoardRepository repository;
    private final HttpServletRequest request;
    private final ModelMapper mapper;

    /**
     * 게시판 설정 한개 조회
     *
     * @param bid
     * @return
     */
    public Board get(String bid) {
        Board item = repository.findById(bid).orElseThrow(BoardNotFoundException::new);

        addInfo(item); // 추가 정보 공통 처리

        return item;
    }

    /**
     * 게시판 설정 수정시 필요한 커맨드 객체 형태로 조회
     *
     * @param bid
     * @return
     */
    public RequestBoard getForm(String bid) {
        Board board = get(bid);

        return mapper.map(board, RequestBoard.class);
    }

    /**
     * 게시판 목록 조회
     *
     * @param search
     * @return
     */
    public ListData<Board> getList(CommonSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 20 : limit;

        String sopt = search.getSopt();
        String skey = search.getSkey();

        BooleanBuilder andBuilder = new BooleanBuilder();
        QBoard board = QBoard.board;

        // 키워드 검색 처리 S
        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";
        if (StringUtils.hasText(skey)) {
            skey = skey.trim();

            StringExpression fields = null;
            if (sopt.equals("BID")) {
                fields = board.bid;
            } else if (sopt.equals("NAME")) {
                fields = board.name;
            } else { // 통합 검색 BID + NAME
                fields = board.bid.concat(board.name);
            }

            andBuilder.and(fields.contains(skey));
        }
        // 키워드 검색 처리 E

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));
        Page<Board> data = repository.findAll(andBuilder, pageable);
        List<Board> items = data.getContent();
        items.forEach(this::addInfo); // 추가정보 처리

        int total = (int)data.getTotalElements();
        Pagination pagination = new Pagination(page, total, 10, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 게시판 설정에 대한 추가 정보 가공 처리
     *
     * @param item
     */
    private void addInfo(Board item) {

    }
}
