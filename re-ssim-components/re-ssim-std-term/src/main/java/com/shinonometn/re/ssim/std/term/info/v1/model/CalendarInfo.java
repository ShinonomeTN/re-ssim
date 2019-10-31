package com.shinonometn.re.ssim.std.term.info.v1.model;

import java.util.Date;

public interface CalendarInfo {
    Date getStartDate();

    Date getEndDate();

    Integer getDays();

    Integer getWeeks();
}
