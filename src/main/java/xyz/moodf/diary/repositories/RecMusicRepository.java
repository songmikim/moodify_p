package xyz.moodf.diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import xyz.moodf.diary.entities.RecMusic;

public interface RecMusicRepository extends JpaRepository<RecMusic, String>, QuerydslPredicateExecutor<RecMusic> {

}
