package com.shinonometn.re.ssim.data.kingo.application.caterpillar.base.calendar;

import com.shinonometn.re.ssim.service.caterpillar.common.SchoolDate;

import java.time.LocalDateTime;

public interface SchoolCalendar {
    public String getName();

    public LocalDateTime getStartDate();

    LocalDateTime getEndDate();

    public int getDaysOfTerm();

    public SchoolDate getFromDateTime(LocalDateTime dateTime);
}
