package xyz.moodf.board.services.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.moodf.board.controllers.RequestBoard;
import xyz.moodf.board.repositories.BoardRepository;

import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardConfigUpdateService {
    private final BoardRepository boardRepository;

    public void process(RequestBoard form) {
        String bid = form.getBid();
        String mode = Objects.requireNonNullElse(form.getMode(), "register");

    }
}