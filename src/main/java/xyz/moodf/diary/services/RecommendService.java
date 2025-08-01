package xyz.moodf.diary.services;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.moodf.spotify.entities.Music;
import xyz.moodf.spotify.entities.QMusic;
import xyz.moodf.spotify.repositories.MusicRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Lazy
@Service
@RequiredArgsConstructor
public class RecommendService {
    private static Map<String, List<String>> sentiments;
    private final JPAQueryFactory queryFactory;
    private final MusicRepository repository;

    // 일기 감정과 음악 감정 매치
    static {
        sentiments = new HashMap<>();
        sentiments.put("분노", java.util.List.of("anger", "angry"));
        sentiments.put("슬픔", java.util.List.of("sadness"));
        sentiments.put("상처", List.of("thirst", "sadness"));
        sentiments.put("당황", List.of("confusion", "surprise"));
        sentiments.put("기쁨", List.of("Love", "love", "pink", "joy", "True", "interest"));
        sentiments.put("불안", List.of("fear"));
    }

    public static List<String> getMusicEmotion(String sentiment) {
        return sentiment == null ? List.of() : sentiments.get(sentiment);
    }

    /**
     * 콘텐츠 추천
     *
     * @param emotion Music의 emotion
     * @param limit 가져올 콘텐츠 개수
     * @return
     */
    public List<Music> getContents(String emotion, int limit) {
        List<String> sentiments = getMusicEmotion(emotion);
        if (sentiments == null || sentiments.isEmpty())
            return List.of();

        QMusic music = QMusic.music;
        List<Music> items = queryFactory.selectFrom(music)
                .where(music.emotion.in(sentiments))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();

        // 추가 정보 처리
        items.forEach(this::addInfo);

        return items;
    }

    public List<Music> getContents(String emotion) {
        return getContents(emotion, 10);
    }

    /**
     * 콘텐츠 하나 조회
     *
     * @param seq 콘텐츠 seq
     * @return 콘텐츠 데이터
     */
    public Music get(Long seq) {
        Music item = repository.findById(seq).orElse(null);
        if (item != null) {
            addInfo(item);  // 추가 정보 처리
        }

        return item;
    }

    /**
     * 추가 정보 처리
     *
     * @param item
     */
    private void addInfo(Music item) {
        item.setYoutubeUrl("https://www.youtube.com/watch?v=iEfIcJHEb70&pp=ygUK7Jyk7ZWYIDQ4Ng%3D%3D");

        String ytbUrl = item.getYoutubeUrl();

        if (StringUtils.hasText(ytbUrl)) {
            Pattern pattern = Pattern.compile("v=([^&]*)&?");
            Matcher matcher = pattern.matcher(ytbUrl);

            if (matcher.find()) {
                String ytbId = matcher.group(1);
                item.setYoutubeId(ytbId);
            }
        }
    }
}
