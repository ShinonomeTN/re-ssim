package com.shinonometn.re.ssim.service.caterpillar.kingo.pojo;

import java.util.List;

public class Course {
    private Integer id; //服务器上的ID
    private String term;//学期
    private String name;//课程名称
    private String code;//课程代号
    private String unit;//承担单位
    private double timeSpend;//学时
    private Double point;//学分
    private List<Lesson> lessons;//课堂

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setNameWithCode(String string) {
        string = string.trim();
        int splitPos = string.indexOf("]");
        if (splitPos < 2) {
            code = "";
            name = string;
        } else {
            code = string.substring(1, splitPos);
            name = string.substring(splitPos + 1, string.length());
        }
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getTimeSpend() {
        return timeSpend;
    }

    public void setTimeSpend(double timeSpend) {
        this.timeSpend = timeSpend;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", term='" + term + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", unit='" + unit + '\'' +
                ", timeSpend=" + timeSpend +
                ", point=" + point +
                ", lessons=" + lessons +
                '}';
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
