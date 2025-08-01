package xyz.moodf.diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import xyz.moodf.diary.entities.Sentiment;

public interface SentimentRepository extends JpaRepository<Sentiment, String>, QuerydslPredicateExecutor<Sentiment> {

}
