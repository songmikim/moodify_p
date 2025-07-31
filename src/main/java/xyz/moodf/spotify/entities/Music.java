package xyz.moodf.spotify.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Music {
    @Id
    @GeneratedValue
    private Long seq;
    @Column(length=60)
    private String artist;
    private String song;

    @Lob
    private String text;

    @Column(length=20)
    private String length;

    @Column(length=45)
    private String emotion;

    @Column(length=45)
    private String genre;

    @Column(length=45)
    private String album;
    private String releaseDate;

    @Column(length=30, name="_key")
    private String key;
    private int tempo;

    @Column(length=30)
    private String loudness;

    @Column(length=40)
    private String timeSignature;

    private boolean explicit;
    private int popularity;
    private int energy;
    private int danceability;
    private int positiveness;
    private int speechiness;
    private int liveness;
    private int acousticness;
    private int instrumentalness;
    private boolean goodForParty;
    private boolean goodForWorkOrStudy;
    private boolean goodForRelaxationOrMeditation;
    private boolean goodForExercise;
    private boolean goodsForRunning;
    private boolean goodForYogaStretching;
    private boolean goodForDriving;
    private boolean goodForSocialGatherings;
    private boolean goodForMorningRoutine;

    @Column(length=100)
    private String similarArtist1;

    @Column(length=100)
    private String similarSong1;
    private double similarityScore1;

    @Column(length=100)
    private String similarArtist2;

    @Column(length=100)
    private String similarSong2;
    private double similarityScore2;

    @Column(length=100)
    private String similarArtist3;

    @Column(length=100)
    private String similarSong3;
    private double similarityScore3;

    private String youtubeUrl;
}
