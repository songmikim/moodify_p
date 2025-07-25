package xyz.moodf.global.libs;

import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.moodf.global.controllers.CalendarSearch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Getter
@Component
public class Calendar {

    private int prevYear;
    private int prevMonth;
    private int year;
    private int month;
    private int nextYear;
    private int nextMonth;

    public List<Map<String, Object>> getDates(int year, int month, Map<LocalDate, Object> extraData) {

        // month의 첫 번째 날
        LocalDate firstDate = LocalDate.of(year, month, 1);

        // month의 마지막 날: 다음 달 1일의 하루 전 날
        LocalDate lastDate = firstDate.plusMonths(1L).minusDays(1L);

        // 첫번째 날 요일: 1(월)~7(일) -> 0(일)~6(토)
        long dayOfWeek = firstDate.getDayOfWeek().getValue() % 7L;

        // 달력의 전체 칸 개수: 5주(35일)면 35칸, 6주(42일)면 42칸
        long total = dayOfWeek + lastDate.getDayOfMonth() > 35L ? 42L : 35L;

        List<Map<String, Object>> dates = new ArrayList<>();
        for (long i = -dayOfWeek; i < total - dayOfWeek; i++) {
            LocalDate date = firstDate.plusDays(i);

            int day = date.getDayOfMonth();

            Map<String, Object> data = new HashMap<>();
            data.put("date", date);
            data.put("day", day);
            data.put("dayWithZero", String.format("%02d", day));

            /* 추가 데이터 처리 */
            data.put("extra", extraData == null ? null : extraData.get(date));

            dates.add(data);
        }

        /* 이전 달, 다음 달 처리 S */
        LocalDate prev = firstDate.minusMonths(1L);
        LocalDate next = firstDate.plusMonths(1L);

        prevYear = prev.getYear();
        prevMonth = prev.getMonthValue();

        nextYear = next.getYear();
        nextMonth = next.getMonthValue();

        this.year = year;
        this.month = month;

        /* 이전 달, 다음 달 처리 E */

        return dates;
    }

    public List<Map<String, Object>> getDates(int year, int month) {
        return getDates(year, month, null);
    }

    // 매개변수 없으면 오늘 기준 캘린더
    public List<Map<String, Object>> getDates() {
        LocalDate today = LocalDate.now();
        return getDates(today.getYear(), today.getMonthValue());
    }

    public List<Map<String, Object>> getDates(CalendarSearch search) {
        int year = search.getYear();
        int month = search.getMonth();
        Map<LocalDate, Object> extraData = search.getExtraData();

        if (year < 1 || month < 1) {
            LocalDate today = LocalDate.now();
            year = today.getYear();
            month = today.getMonthValue();
        }

        return getDates(year, month, extraData);
    }
}
