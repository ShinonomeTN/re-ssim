package com.shinonometn.re.ssim.caterpillar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.util.Date;
import java.util.List;

public class SpiderStatusImpl implements SpiderStatus {

    @JsonIgnore
    protected final Spider spider;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final SpiderMonitorListener monitorSpiderListener;

    public SpiderStatusImpl(Spider spider, SpiderMonitorListener monitorSpiderListener) {
        this.spider = spider;
        this.monitorSpiderListener = monitorSpiderListener;
    }

    @JsonIgnore
    public Spider getSpider() {
        return this.spider;
    }

    public String getName() {
        return spider.getUUID();
    }

    public int getLeftPageCount() {
        if (spider.getScheduler() instanceof MonitorableScheduler) {
            return ((MonitorableScheduler) spider.getScheduler()).getLeftRequestsCount(spider);
        }
        logger.warn("Get leftPageCount fail, try to use a Scheduler implement MonitorableScheduler for monitor count!");
        return -1;
    }

    public int getTotalPageCount() {
        if (spider.getScheduler() instanceof MonitorableScheduler) {
            return ((MonitorableScheduler) spider.getScheduler()).getTotalRequestsCount(spider);
        }
        logger.warn("Get totalPageCount fail, try to use a Scheduler implement MonitorableScheduler for monitor count!");
        return -1;
    }

    @Override
    public int getSuccessPageCount() {
        return monitorSpiderListener.getSuccessCount().get();
    }

    @Override
    public int getErrorPageCount() {
        return monitorSpiderListener.getErrorCount().get();
    }

    public List<String> getErrorPages() {
        return monitorSpiderListener.getErrorUrls();
    }

    @Override
    public String getStatus() {
        return spider.getStatus().name();
    }

    @Override
    public int getThread() {
        return spider.getThreadAlive();
    }

    public void start() {
        spider.start();
    }

    public void stop() {
        spider.stop();
    }

    @Override
    public Date getStartTime() {
        return spider.getStartTime();
    }

    @Override
    public int getPagePerSecond() {
        int runSeconds = (int) (System.currentTimeMillis() - getStartTime().getTime()) / 1000;
        if(runSeconds == 0) return 0;
        return getSuccessPageCount() / runSeconds;
    }

}
