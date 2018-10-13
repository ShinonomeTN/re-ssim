package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import com.shinonometn.re.ssim.caterpillar.test.TestHelper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.*;
import java.util.stream.Collectors;

import static com.shinonometn.re.ssim.caterpillar.kingo.KingoUrls.calendarPage;

@Ignore
public class CalendarPageProcessorTest {

    private Site site = Site.me()
            .setDomain("jwgl.lnc.edu.cn")
            .setTimeOut(10000)
            .setRetryTimes(81)
            .setSleepTime(500)
            .setCharset("GBK")
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8")
            .addHeader("Referer", calendarPage);

    @Test
    public void testGetCalendar() {

        String termCode = "20180";

        Request calendarQueryRequest = new Request(calendarPage);
        calendarQueryRequest.setMethod(HttpConstant.Method.POST);
        Map<String, Object> requestContent = new HashMap<>();
        requestContent.put("sel_xnxq", termCode);
        calendarQueryRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK");
        calendarQueryRequest.setRequestBody(HttpRequestBody.form(requestContent, "gbk"));

        Spider.create(new CalendarPageProcessor(site))
                .addRequest(calendarQueryRequest)
                .run();
    }

    @Test
    public void testGetCalendarAndFiltering() {

        Map<String, String> resultSet = new HashMap<>();

        Spider.create(new CalendarListPageProcessor(site))
                .addUrl(calendarPage)
                .addPipeline((resultItems, task) -> resultSet.putAll(CalendarListPageProcessor.getCalendarList(resultItems)))
                .run();

        Set<CalendarPageProcessor.CalendarRangeInfo> calendarRangeInfo = resultSet.keySet().stream()
                .map(item -> {
                    Request calendarQueryRequest = new Request(calendarPage);
                    calendarQueryRequest.setMethod(HttpConstant.Method.POST);
                    Map<String, Object> requestContent = new HashMap<>();
                    requestContent.put("sel_xnxq", item);
                    calendarQueryRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK");
                    calendarQueryRequest.setRequestBody(HttpRequestBody.form(requestContent, "gbk"));

                    List<CalendarPageProcessor.CalendarRangeInfo> list = new ArrayList<>();

                    Spider.create(new CalendarPageProcessor(site))
                            .addRequest(calendarQueryRequest)
                            .addPipeline((r, t) -> list.addAll(CalendarPageProcessor.getCalendarRangeInfo(r)))
                            .run();

                    return list;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());


        TestHelper.info(calendarRangeInfo.toString());

        Assert.assertFalse(calendarRangeInfo.isEmpty());
    }
}