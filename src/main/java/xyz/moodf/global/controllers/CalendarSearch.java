package xyz.moodf.global.controllers;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class CalendarSearch {
    private int year;
    private int month;
    private Map<LocalDate, Object> extraData;
}
