package xyz.moodf.board.search;

import lombok.Data;
import xyz.moodf.global.search.CommonSearch;

import java.util.List;

@Data
public class BoardSearch extends CommonSearch {
    private List<String> bid;
}