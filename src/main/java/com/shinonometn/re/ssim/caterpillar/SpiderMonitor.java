package com.shinonometn.re.ssim.caterpillar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 *
 * Spider monitor
 *
 * Spider monitor is used for registering spiders to a list for managing.
 *
 */
public class SpiderMonitor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

//    private AtomicBoolean started = new AtomicBoolean(false);

    private Map<String, SpiderStatus> spiderStatus = new HashMap<>();

    public Map<String, SpiderStatus> getSpiderStatus() {
        return spiderStatus;
    }

    public synchronized SpiderMonitor register(Spider... spiders) {
        Stream.of(spiders).forEach(spider -> {
            SpiderMonitorListener spiderMonitorListener = new SpiderMonitorListener();

            /**
             *
             * If spider already has listeners, get the list and add the monitor listener,
             * else, create a new array list and put the listener in.
             *
             */
            if (spider.getSpiderListeners() == null) {
                List<SpiderListener> spiderMonitorListeners = new ArrayList<>();
                spiderMonitorListeners.add(spiderMonitorListener);
                spider.setSpiderListeners(spiderMonitorListeners);
            } else {
                spider.getSpiderListeners().add(spiderMonitorListener);
            }

            SpiderStatus spiderStatus = new SpiderStatusImpl(spider, spiderMonitorListener);
            this.spiderStatus.put(spider.getUUID(),spiderStatus);

            logger.info("Task {} registered to monitor.", spider.getUUID());
        });

        return this;
    }
}
