package com.shinonometn.re.ssim.std.course.query.v1.service;

import com.shinonometn.re.ssim.std.base.RSComponentVersion;
import com.shinonometn.re.ssim.std.base.RSFeature;
import com.shinonometn.re.ssim.std.course.query.v1.model.TimePointLessonInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RSComponentVersion("1")
public interface TeacherCourseScheduleQueryService {

    @RSFeature("course.lessons_in_week_for_teacher")
    Optional<List<TimePointLessonInfo>> queryLessonsInTermInWeekForTeacher(
            String termCode,
            String teacherName,
            Integer week,
            Set<String> excludedCourseType,
            String dataVersion
    );


    @RSFeature("course.weekdays_in_term_for_teacher")
    Optional<Set<Integer>> queryWeeksInTermForTeacher(
            String termCode,
            String teacherName,
            Integer week,
            Set<String> excludedCourseType,
            String dataVersion
    );
}
