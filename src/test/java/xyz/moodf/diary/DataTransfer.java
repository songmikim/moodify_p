package xyz.moodf.diary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.moodf.spotify.entities.Music;
import xyz.moodf.spotify.repositories.MusicRepository;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

@SpringBootTest
public class DataTransfer {

    private CsvMapper mapper;

    @Autowired
    private MusicRepository repository;

    @BeforeEach
    void init() {
        mapper = new CsvMapper();
    }

    @Test
    void transfer() throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:/data/spotify_dataset.csv")))) {
            long cnt = 0L;
            String line = null;
            while((line = br.readLine()) != null) {
                if (cnt == 0L) {
                    cnt++;
                    continue;
                }

                try {
                    List<String> item = mapper.readValue(line, new TypeReference<>() {
                    });

                    Music music = new Music();
                    music.setArtist(item.get(0));
                    music.setSong(item.get(1));
                    music.setText(item.get(2));
                    music.setLength(item.get(3));
                    music.setEmotion(item.get(4));
                    music.setGenre(item.get(5));
                    music.setAlbum(item.get(6));
                    music.setReleaseDate(item.get(7));
                    music.setKey(item.get(8));
                    try {
                        music.setTempo(Integer.parseInt(item.get(9)));
                    } catch (Exception e) {
                    }

                    music.setLoudness(item.get(10));
                    music.setTimeSignature(item.get(11));
                    music.setExplicit(item.get(12).equalsIgnoreCase("YES"));
                    try {
                        music.setPopularity(Integer.parseInt(item.get(13)));
                    } catch (Exception e) {
                    }

                    try {
                        music.setEnergy(Integer.parseInt(item.get(14)));
                    } catch (Exception e) {
                    }

                    try {
                        music.setDanceability(Integer.parseInt(item.get(15)));
                    } catch (Exception e) {
                    }

                    try {
                        music.setPositiveness(Integer.parseInt(item.get(16)));
                    } catch (Exception e) {
                    }

                    try {
                        music.setSpeechiness(Integer.parseInt(item.get(17)));
                    } catch (Exception e) {
                    }

                    try {
                        music.setLiveness(Integer.parseInt(item.get(18)));
                    } catch (Exception e) {
                    }

                    try {
                        music.setAcousticness(Integer.parseInt(item.get(19)));
                    } catch (Exception e) {
                    }

                    try {
                        music.setInstrumentalness(Integer.parseInt(item.get(20)));
                    } catch (Exception e) {
                    }

                    music.setGoodForWorkOrStudy(item.get(21).equals("1"));
                    music.setGoodForRelaxationOrMeditation(item.get(22).equals("1"));
                    music.setGoodForExercise(item.get(23).equals("1"));
                    music.setGoodsForRunning(item.get(24).equals("1"));
                    music.setGoodForYogaStretching(item.get(25).equals("1"));
                    music.setGoodForDriving(item.get(26).equals("1"));
                    music.setGoodForSocialGatherings(item.get(27).equals("1"));
                    music.setGoodForMorningRoutine(item.get(28).equals("1"));
                    music.setGoodForParty(item.get(29).equals("1"));
                    music.setSimilarArtist1(item.get(30));
                    music.setSimilarSong1(item.get(31));

                    try {
                        music.setSimilarityScore1(Double.parseDouble(item.get(32)));
                    } catch (Exception e) {
                    }

                    music.setSimilarArtist2(item.get(33));
                    music.setSimilarSong2(item.get(34));

                    try {
                        music.setSimilarityScore2(Double.parseDouble(item.get(35)));
                    } catch (Exception e) {
                    }

                    music.setSimilarArtist3(item.get(36));
                    music.setSimilarSong3(item.get(37));

                    try {
                        music.setSimilarityScore3(Double.parseDouble(item.get(38)));
                    } catch (Exception e) {
                    }

                    repository.saveAndFlush(music);

                    cnt++;
                } catch (Exception e) {}
            }
        }
    }
}

