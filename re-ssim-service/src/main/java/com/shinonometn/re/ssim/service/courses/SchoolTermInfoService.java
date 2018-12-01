package com.shinonometn.re.ssim.service.courses;

import com.shinonometn.re.ssim.service.courses.repository.SchoolCalendarEntityRepository;

public class SchoolTermInfoService {

    private final SchoolCalendarEntityRepository schoolCalendarEntityRepository;

    public SchoolTermInfoService(SchoolCalendarEntityRepository schoolCalendarEntityRepository) {
        this.schoolCalendarEntityRepository = schoolCalendarEntityRepository;
    }


}
