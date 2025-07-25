package xyz.moodf.admin.board.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.moodf.admin.board.controllers.RequestBoard;
import xyz.moodf.admin.board.entities.Board;
import xyz.moodf.admin.board.repositories.BoardRepository;

import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardConfigUpdateService {
    private final ModelMapper mapper;
    private final BoardRepository boardRepository;

    public void process(RequestBoard form) {
        String mode = Objects.requireNonNullElse(form.getMode(), "register");
        Board item = mapper.map(form, Board.class);
        boardRepository.saveAndFlush(item);
    }

}