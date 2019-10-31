package com.shinonometn.re.ssim.std.term.info.v1.service;

import com.shinonometn.re.ssim.std.base.RSComponentVersion;
import com.shinonometn.re.ssim.std.base.RSFeature;
import com.shinonometn.re.ssim.std.term.info.v1.model.CalendarInfo;

import java.util.List;
import java.util.Optional;

@RSComponentVersion("1")
public interface TermInfoService {

    @RSFeature("term.courses")
    Optional<List<String>> queryCourseListForTerm(
            String termCode,
            String dataVersion
    );

    @RSFeature("term.course_types")
    Optional<List<String>> queryCourseTypesForTerm(
            String termCode,
            String dataVersion
    );

    @RSFeature("term.teachers")
    Optional<List<String>> queryTeachersForTerm(
            String termCode,
            String dataVersion
    );

    @RSFeature("term.departments")
    Optional<List<String>> queryDepartmentsForTerm(
            String termCode,
            String dataVersion
    );

    @RSFeature("term.classrooms")
    Optional<List<String>> queryClassroomsForTerm(
            String termCode,
            String dataVersion
    );

    @RSFeature("term.calendar")
    Optional<List<CalendarInfo>> getCalendarForTerm(
            String termCode
    );
}
