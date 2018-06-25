package com.shinonometn.re.ssim.caterpillar.kingo.pojo;

public class TimePoint {
    private int week;
    private int day;
    private int turn;

    public TimePoint(){
    }

    public TimePoint(int week, int day, int turn) {
        this.week = week;
        this.day = day;
        this.turn = turn;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        return "TimePoint{" +
                "week=" + week +
                ", day=" + day +
                ", turn=" + turn +
                '}';
    }
}
