package com.shinonometn.re.ssim.service.caterpillar.commons;

import com.shinonometn.re.ssim.service.caterpillar.common.SchoolDate;
import com.shinonometn.re.ssim.service.caterpillar.kingo.KingoSchoolCalendar;
import com.shinonometn.re.ssim.service.caterpillar.test.TestHelper;
import org.junit.Test;

import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SchoolCalendarTest {

    @Test
    public void testGetWeekAndDay() {
        String term = "2017-2018学年第一学期";
        Date startDate = Date.from(LocalDate.of(2017, 9, 4).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(LocalDate.of(2018, 1, 5).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        KingoSchoolCalendar schoolCalendar = new KingoSchoolCalendar(term, startDate, endDate);

        Map<LocalDateTime, SchoolDate> testCases = new HashMap<>();
        testCases.put(LocalDateTime.of(2017, 9, 4, 20, 31), new SchoolDate(term, 1, DayOfWeek.MONDAY));
        testCases.put(LocalDateTime.of(2017, 10, 17, 0, 0), new SchoolDate(term, 7, DayOfWeek.TUESDAY));
        testCases.put(LocalDateTime.of(2017, 12, 1, 0, 0), new SchoolDate(term, 13, DayOfWeek.FRIDAY));
        testCases.put(LocalDateTime.of(2018, 1, 3, 0, 0), new SchoolDate(term, 18, DayOfWeek.WEDNESDAY));
        testCases.put(LocalDateTime.of(2018, 1, 5, 0, 0), new SchoolDate(term, 18, DayOfWeek.FRIDAY));
        testCases.put(LocalDateTime.of(2018, 1, 5, 18, 31), new SchoolDate(term, 18, DayOfWeek.FRIDAY));
        testCases.put(LocalDateTime.of(2018, 1, 6, 0, 0), null);


        testCases.forEach((k, v) -> {
            SchoolDate schoolDate = schoolCalendar.getFromDateTime(k);

            TestHelper.info(String.valueOf(schoolDate));
            assertEquals(schoolDate, v);
        });
    }

    @Test
    public void testWeekDayShift() {
        String term = "2017-2018学年第一学期";
        Date endDate = Date.from(LocalDate.of(2018, 1, 5).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        Date startDate = Date.from(LocalDate.of(2017, 9, 7).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        KingoSchoolCalendar schoolCalendar = new KingoSchoolCalendar(term, startDate, endDate);

        SchoolDate schoolDate = schoolCalendar.getFromDateTime(LocalDateTime.of(2017, 9, 4, 20, 31));
        TestHelper.info(String.valueOf(schoolDate));
        assertEquals(schoolDate, new SchoolDate(term, 1, DayOfWeek.MONDAY));

    }

    @Test
    public void getDaysOfTerm() {
        String term = "2017-2018学年第一学期";
        Date startDate = Date.from(LocalDate.of(2017, 9, 4).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(LocalDate.of(2018, 1, 5).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        KingoSchoolCalendar schoolCalendar = new KingoSchoolCalendar(term, startDate, endDate);

        int days = schoolCalendar.getDaysOfTerm();

        TestHelper.info(String.valueOf(days));
        assertEquals(days, 18 * 7 - 2); // 18 weeks, 7 days in a week, minus the latest weekend
    }

}