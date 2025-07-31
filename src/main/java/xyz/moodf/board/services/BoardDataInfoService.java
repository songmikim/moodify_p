package xyz.moodf.board.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import xyz.moodf.admin.board.exceptions.BoardDataNotFoundException;
import xyz.moodf.board.entities.BoardData;
import xyz.moodf.board.entities.QBoardData;
import xyz.moodf.board.repositories.BoardDataRepository;
import xyz.moodf.global.file.services.FileInfoService;
import xyz.moodf.global.search.CommonSearch;
import xyz.moodf.global.search.ListData;
import xyz.moodf.global.search.Pagination;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardDataInfoService
{
    private final BoardDataRepository repository;
    private final HttpServletRequest request;
    private final ModelMapper mapper;
    private final MemberUtil memberUtil;
    private final FileInfoService fileInfoService;
    private final BoardPermissionService permissionService;

    /**
     * 게시판 설정 한개 조회
     *
     * @param seq
     * @return
     */
    public BoardData get(Long seq) {
        BoardData item = repository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        addInfo(item); // 추가 정보 공통 처리

        return item;
    }

    /**
     * 게시판 설정 수정시 필요한 커맨드 객체 형태로 조회
     *
     * @param seq
     * @return
     */
    public RequestBoard getForm(Long seq) {
        BoardData board = get(seq);

        return mapper.map(board, RequestBoard.class);
    }

    /**
     * 게시판 목록 조회
     *
     * @param search
     * @param bid
     * @return
     */
    /**
     * 게시판 목록 조회
     *
     * @param search
     * @param bid
     * @return
     */
    public ListData<BoardData> getList(CommonSearch search, String bid) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();

        limit = limit < 1 ? 20 : limit;

        String sopt = search.getSopt();
        String skey = search.getSkey();

        BooleanBuilder andBuilder = new BooleanBuilder();
        QBoardData board = QBoardData.boardData;

        // bid 필터링 추가 - 특정 게시판의 글만 조회
        if (StringUtils.hasText(bid)) {
            andBuilder.and(board.board.bid.eq(bid));
        }

        //deletedAt 필터링
        andBuilder.and(board.deletedAt.isNull());

        // 키워드 검색 처리 S
        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";
        if (StringUtils.hasText(skey)) {
            skey = skey.trim();

            StringExpression fields = null;
            if (sopt.equals("POSTER")) {
                fields = board.poster;
            } else if (sopt.equals("SUBJECT")) {
                fields = board.subject;
            } else if (sopt.equals("CONTENT")) {
                fields = board.content;
            } else { // 통합 검색
                fields = board.poster.concat(board.subject).concat(board.content);
            }
            andBuilder.and(fields.contains(skey));
        }
        // 키워드 검색 처리 E

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));
        Page<BoardData> data = repository.findAll(andBuilder, pageable);
        List<BoardData> items = data.getContent();
        items.forEach(this::addInfo); // 추가정보 처리

        int total = (int)data.getTotalElements();
        Pagination pagination = new Pagination(page, total, 10, limit, request);

        return new ListData<>(items, pagination);
    }


    // 관리자 게시글 관리용
    public ListData<BoardData> getList(CommonSearch search) {
        // 모든 게시글을 조회하기 위해 공백으로 필터링 우회
        ListData<BoardData> data = getList(search, null);
        return data;
    }

    /**
     * 게시판 설정에 대한 추가 정보 가공 처리
     *
     * @param item
     */
    private void addInfo(BoardData item) {
        String gid = item.getGid();

        // 첨부된 이미지 & 파일 목록
        item.setEditorImages(fileInfoService.getList(gid, "editor"));
        item.setAttachFiles(fileInfoService.getList(gid, "attach"));
    }
}

