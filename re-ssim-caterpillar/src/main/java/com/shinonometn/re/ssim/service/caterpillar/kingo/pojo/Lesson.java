package com.shinonometn.re.ssim.service.caterpillar.kingo.pojo;

import java.util.Arrays;
import java.util.List;

public class Lesson {
    private String teacher;//任课老师
    private String classNumber;//上课班号
    private Integer attendAmount;//上课人数
    private String[] classAttend;//上课班级构成
    private String classType;//课程类别
    private String assessmentType;//考核方式
    private List<TimePoint> timePoint;
    private String position;//地点

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public Integer getAttendAmount() {
        return attendAmount;
    }

    public void setAttendAmount(Integer attendAmount) {
        this.attendAmount = attendAmount;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String[] getClassAttend() {
        return classAttend;
    }

    public void setClassAttend(String[] classAttend) {
        this.classAttend = classAttend;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "teacher='" + teacher + '\'' +
                ", classNumber='" + classNumber + '\'' +
                ", attendAmount=" + attendAmount +
                ", classAttend=" + Arrays.toString(classAttend) +
                ", classType='" + classType + '\'' +
                ", assessmentType='" + assessmentType + '\'' +
                ", weeks=" + timePoint +
                ", timePoint=" + timePoint +
                ", position='" + position + '\'' +
                '}';
    }

    public List<TimePoint> getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(List<TimePoint> timePoint) {
        this.timePoint = timePoint;
    }
}
