package xyz.moodf.board.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.moodf.admin.board.entities.Board;
import xyz.moodf.admin.board.services.BoardConfigInfoService;
import xyz.moodf.board.controllers.RequestPostBoard;
import xyz.moodf.board.entities.BoardData;
import xyz.moodf.board.repositories.BoardDataRepository;
import xyz.moodf.global.file.services.FileUploadService;
import xyz.moodf.member.libs.MemberUtil;

import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardDataUpdateService {
    private final ModelMapper mapper;
    private final BoardDataRepository boardRepository;
    private final FileUploadService uploadService;
    private final BoardConfigInfoService configInfoService;
    private final HttpServletRequest request;
    private final MemberUtil memberUtil;

    public void process(RequestPostBoard form) {
        String mode = Objects.requireNonNullElse(form.getMode(), "register");

        BoardData item = mapper.map(form, BoardData.class);
        String gid = form.getGid();

        if (mode.equals("register")){
            Board board = configInfoService.get(form.getBid());
            item.setBoard(board);
            item.setIp(request.getRemoteAddr());
            item.setUa(request.getHeader("User-Agent"));
            item.setPlainText(!board.isEditor());
        }

        if (memberUtil.isLogin()) {
            item.setMember(memberUtil.getMember());
        }

        if (memberUtil.isAdmin()) {
            item.setNotice(form.isNotice());
        } else {
            item.setNotice(false);
        }

        boardRepository.saveAndFlush(item);

        // 파일 업로드 완료처리
        uploadService.processDone(gid);

    }
}
