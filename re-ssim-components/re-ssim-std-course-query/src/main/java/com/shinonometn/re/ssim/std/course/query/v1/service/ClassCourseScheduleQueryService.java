package com.shinonometn.re.ssim.std.course.query.v1.service;

import com.shinonometn.re.ssim.std.base.RSComponentVersion;
import com.shinonometn.re.ssim.std.base.RSFeature;
import com.shinonometn.re.ssim.std.course.query.v1.model.TimePointLessonInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RSComponentVersion("1")
public interface ClassCourseScheduleQueryService {

    @RSFeature("course.lessons_in_week_for_class")
    Optional<List<TimePointLessonInfo>> queryCourseInTermAtWeekForClass(
            String termCode,
            String clazzName,
            Integer week,
            List<String> excludeType,
            String dataVersion
    );

    @RSFeature("course.weekdays_in_term_for_class")
    Optional<Set<Integer>> queryClassWeeksInTerm(
            String termCode,
            String clazzName,
            List<String> excludeCourseType,
            String dataVersion
    );

}
