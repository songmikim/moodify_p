package xyz.moodf.global.search;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CommonSearch {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sDate; // 검색 시작일

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eDate; // 검색 종료일
    private String sopt; // 검색 옵션,
    private String skey; // 검색 키워드
    private int page; // 페이지 번호, 1, 2, 3, ...
    private int limit; // 한페이지에 조회할 레코드 갯수
}
