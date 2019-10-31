package com.shinonometn.re.ssim.std.course.query.v1.model;

import java.util.Set;

public interface ClassCourseScheduleRequest {

    String getTermCode();

    String getClazzName();

    Set<String> getExcludedClazzType();

    Integer getWeek();

    String getDataVersion();

}
