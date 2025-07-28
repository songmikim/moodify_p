package xyz.moodf.board.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.moodf.board.entities.BoardData;
import xyz.moodf.board.repositories.BoardDataRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardDataDeleteService {

    private final BoardDataRepository repository;

    public void softDelete(Long seq) {
        BoardData boardData = repository.findById(seq)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 물리적 삭제 대신 deletedAt 설정
        boardData.setDeletedAt(LocalDateTime.now());
        repository.save(boardData);
    }

    public void hardDelete(Long seq) {
        // 진짜 삭제 (관리자 전용)
        repository.deleteById(seq);
    }
}