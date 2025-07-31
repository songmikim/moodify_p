package xyz.moodf.spotify.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.moodf.spotify.entities.Music;

public interface MusicRepository extends JpaRepository<Music, Long> {

}
