package com.shinonometn.re.ssim.caterpillar.kingo;

import com.shinonometn.re.ssim.commons.SchoolCalendar;
import com.shinonometn.re.ssim.commons.SchoolDate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class KingoSchoolCalendar implements SchoolCalendar {

    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public KingoSchoolCalendar() {
    }

    public KingoSchoolCalendar(String name, Date startDate, Date endDate) {
        this.name = name;
        if (startDate.after(endDate)) throw new IllegalArgumentException("startDate_after_endDate");
        this.startDate = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
        this.endDate = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getDaysOfTerm() {
        return (int) ChronoUnit.DAYS.between(startDate,endDate) + 1;
    }

    public SchoolDate getFromDateTime(LocalDateTime dateTime) {
        if (startDate.isAfter(endDate)) throw new IllegalArgumentException("startDate_after_endDate");
        if (endDate.isBefore(dateTime)) return null;

        LocalDateTime startDateTime = this.startDate;
        DayOfWeek startWeekday = startDate.getDayOfWeek();
        if (!startWeekday.equals(DayOfWeek.MONDAY)) startDateTime = startDateTime.minusDays(startWeekday.getValue());
        if (startDateTime.isAfter(dateTime)) return null;

        int week = (int) Math.ceil((double) ChronoUnit.DAYS.between(startDateTime.toLocalDate(), dateTime) / 7);
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();

        SchoolDate schoolDate = new SchoolDate();
        schoolDate.setTerm(this.name);
        schoolDate.setWeek(week);
        schoolDate.setDay(dayOfWeek);
        return schoolDate;
    }
}
