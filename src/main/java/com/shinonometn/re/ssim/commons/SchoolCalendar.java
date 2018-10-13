package com.shinonometn.re.ssim.commons;

import java.time.LocalDateTime;

public interface SchoolCalendar {
    public String getName();

    public LocalDateTime getStartDate();

    LocalDateTime getEndDate();

    public int getDaysOfTerm();

    public SchoolDate getFromDateTime(LocalDateTime dateTime);
}
