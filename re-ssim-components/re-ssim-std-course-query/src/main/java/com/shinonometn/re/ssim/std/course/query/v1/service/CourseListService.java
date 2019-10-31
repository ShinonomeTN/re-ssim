package com.shinonometn.re.ssim.std.course.query.v1.service;

import com.shinonometn.re.ssim.std.base.RSComponentVersion;
import com.shinonometn.re.ssim.std.base.RSFeature;

import java.util.List;
import java.util.Optional;

@RSComponentVersion("1")
public interface CourseListService {

    @RSFeature("course.courses_in_term")
    Optional<List<String>> queryCourseListForTerm(
            String termCode,
            String dataVersion
    );
}
