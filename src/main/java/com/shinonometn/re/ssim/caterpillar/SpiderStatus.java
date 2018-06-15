package com.shinonometn.re.ssim.caterpillar;

import java.util.Date;
import java.util.List;

public interface SpiderStatus {
    String getName();

    String getStatus();

    int getThread();

    int getTotalPageCount();

    int getLeftPageCount();

    int getSuccessPageCount();

    int getErrorPageCount();

    List<String> getErrorPages();

    void start();

    void stop();

    Date getStartTime();

    int getPagePerSecond();
}
