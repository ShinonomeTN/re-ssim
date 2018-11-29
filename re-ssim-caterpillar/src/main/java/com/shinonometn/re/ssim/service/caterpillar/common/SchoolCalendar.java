package com.shinonometn.re.ssim.service.caterpillar.common;

import java.time.LocalDateTime;

public interface SchoolCalendar {
    public String getName();

    public LocalDateTime getStartDate();

    LocalDateTime getEndDate();

    public int getDaysOfTerm();

    public SchoolDate getFromDateTime(LocalDateTime dateTime);
}
