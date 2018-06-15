package com.shinonometn.re.ssim.caterpillar;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SpiderMonitorListener implements SpiderListener {
    private final AtomicInteger successCount = new AtomicInteger(0);

    private final AtomicInteger errorCount = new AtomicInteger(0);

    private List<String> errorUrls = Collections.synchronizedList(new ArrayList<String>());

    @Override
    public void onSuccess(Request request) {
        successCount.incrementAndGet();
    }

    @Override
    public void onError(Request request) {
        errorUrls.add(request.getUrl());
        errorCount.incrementAndGet();
    }

    public AtomicInteger getSuccessCount() {
        return successCount;
    }

    public AtomicInteger getErrorCount() {
        return errorCount;
    }

    public List<String> getErrorUrls() {
        return errorUrls;
    }
}
