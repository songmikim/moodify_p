package xyz.moodf.diary.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.moodf.spotify.entities.Music;

import java.util.List;

@SpringBootTest
public class RecommendServiceTest {

    @Autowired
    private RecommendService service;

    @Test
    void test() {
        List<Music> items = service.getContents("기쁨");
        items.forEach(System.out::println);
    }
}
