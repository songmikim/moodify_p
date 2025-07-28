package xyz.moodf.global.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import xyz.moodf.global.libs.Calendar;
import xyz.moodf.global.libs.Utils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {
    private final Calendar calendar;
    private final Utils utils;

    @GetMapping
    public String index(@ModelAttribute CalendarSearch search,
                        @SessionAttribute(name = "extraData", required = false) Map<LocalDate, Object> extraData,
                        Model model) {

        search.setExtraData(extraData);

        model.addAttribute("items", calendar.getDates(search));
        model.addAttribute("year", calendar.getYear());
        model.addAttribute("month", calendar.getMonth());
        model.addAttribute("prevYear", calendar.getPrevYear());
        model.addAttribute("prevMonth", calendar.getPrevMonth());
        model.addAttribute("nextYear", calendar.getNextYear());
        model.addAttribute("nextMonth", calendar.getNextMonth());

        model.addAttribute("addCommonScript", List.of("calendar"));
        model.addAttribute("addCommonCss", List.of("calendar"));

        return utils.tpl("calendar/index");
    }
}
