package xyz.moodf.diary.constants;

import lombok.Getter;

@Getter
public enum Weather {
    NULL("선택 안 함"),
    SUNNY("맑음"),
    CLOUDY("흐림"),
    RAINY("비"),
    SNOWY("눈"),
    STORMY("폭풍"),
    WINDY("바람");

    private final String description; // 한국어로 출력하기 위해 description 추가

    Weather(String description) {
        this.description = description;
    }
}
