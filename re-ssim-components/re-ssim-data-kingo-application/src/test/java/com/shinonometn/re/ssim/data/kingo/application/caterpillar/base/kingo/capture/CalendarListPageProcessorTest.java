package com.shinonometn.re.ssim.data.kingo.application.caterpillar.base.kingo.capture;

import com.shinonometn.re.ssim.data.kingo.application.caterpillar.test.TestHelper;
import com.shinonometn.re.ssim.service.caterpillar.kingo.capture.CalendarListPageProcessor;
import org.junit.Ignore;
import org.junit.Test;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;

import java.util.HashMap;
import java.util.Map;

import static com.shinonometn.re.ssim.service.caterpillar.kingo.KingoUrls.calendarPage;
import static org.junit.Assert.assertFalse;

@Ignore
public class CalendarListPageProcessorTest {

    @Test
    public void testGetSiteLists() {
        Site site = Site.me()
                .setDomain("jwgl.lnc.edu.cn")
                .setTimeOut(10000)
                .setRetryTimes(81)
                .setSleepTime(500)
                .setCharset("GBK")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8")
                .addHeader("Referer", calendarPage);

        Map<String, String> resultSet = new HashMap<>();

        Spider.create(new CalendarListPageProcessor(site))
                .addUrl(calendarPage)
                .addPipeline((resultItems, task) -> resultSet.putAll(CalendarListPageProcessor.getCalendarList(resultItems)))
                .run();

        resultSet.forEach((k, v) -> TestHelper.info(String.format("%s : %s", k, v)));

        assertFalse(resultSet.isEmpty());
    }

}