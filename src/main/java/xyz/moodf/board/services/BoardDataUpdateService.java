package xyz.moodf.board.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.moodf.board.controllers.RequestPostBoard;
import xyz.moodf.board.entities.BoardData;
import xyz.moodf.board.repositories.BoardDataRepository;

import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardDataUpdateService {
    private final ModelMapper mapper;
    private final BoardDataRepository boardRepository;

    public void process(RequestPostBoard form) {
        String mode = Objects.requireNonNullElse(form.getMode(), "register");
        BoardData item = mapper.map(form, BoardData.class);
        boardRepository.saveAndFlush(item);


    }
}
