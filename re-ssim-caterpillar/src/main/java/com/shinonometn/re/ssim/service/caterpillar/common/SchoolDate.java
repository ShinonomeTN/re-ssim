package com.shinonometn.re.ssim.service.caterpillar.common;

import java.time.DayOfWeek;
import java.util.Objects;

public class SchoolDate {

    private String term;
    private int week;
    private DayOfWeek day;

    public SchoolDate() {
    }

    public SchoolDate(String term, int week, DayOfWeek day) {
        this.term = term;
        this.week = week;
        this.day = day;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "SchoolDate{" +
                "term='" + term + '\'' +
                ", week=" + week +
                ", day=" + day +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolDate that = (SchoolDate) o;
        return week == that.week &&
                Objects.equals(term, that.term) &&
                day == that.day;
    }

    @Override
    public int hashCode() {

        return Objects.hash(term, week, day);
    }
}
