package com.shinonometn.re.ssim.std.course.query.v1.model;

import java.util.List;

public interface TimePointLessonInfo {

    List<BasicLessonInfo> getLessons();

    LessonTime getTimePoint();
}
