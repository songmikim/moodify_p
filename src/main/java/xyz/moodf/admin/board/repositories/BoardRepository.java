package xyz.moodf.admin.board.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import xyz.moodf.admin.board.entities.Board;

public interface BoardRepository extends JpaRepository<Board, String>, QuerydslPredicateExecutor<Board> {
    boolean existsByBid(String bid);
}
