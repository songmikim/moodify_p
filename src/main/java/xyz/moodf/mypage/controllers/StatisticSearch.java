package xyz.moodf.mypage.controllers;

import lombok.Data;
import xyz.moodf.diary.constants.StatisticType;
import xyz.moodf.global.search.CommonSearch;

@Data
public class StatisticSearch extends CommonSearch {
    private StatisticType type;
}
