package xyz.moodf.global.libs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CalendarTest {
    @Test
    void test1() {
        // 이번 달의 첫번째 날
        LocalDate firstDate = LocalDate.of(2025, 7, 1);

        // 이번 달의 마지막 날: 다음 달 1일의 하루 전 날
        LocalDate lastDate = firstDate.plusMonths(1L).minusDays(1L);

        // 첫번째 날 요일: 1(월)~7(일) -> 0(일)~6(토)
        long yoil = firstDate.getDayOfWeek().getValue() % 7;

        // 달력의 전체 칸 개수: 5주(35일)면 35칸, 6주(42일)면 42칸
        long cnt = yoil + lastDate.getDayOfMonth();
        long total = cnt > 35L ? 42L : 35L;

        // 달력에 나올 첫번째 일요일부터 total칸 만큼의 날짜 모두 출력
        for (long i = -yoil; i < total - yoil; i++) {
            LocalDate date = firstDate.plusDays(i);
            System.out.println(date);
        }
    }

    @Test
    void test2() {
        Calendar calendar = new Calendar();
        List<Map<String, Object>> dates = calendar.getDates(2025, 7);
        dates.forEach(System.out::println);
    }
}
