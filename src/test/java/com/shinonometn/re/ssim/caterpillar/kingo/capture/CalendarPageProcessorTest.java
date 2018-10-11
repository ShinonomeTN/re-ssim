package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import org.junit.Test;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.HashMap;
import java.util.Map;

import static com.shinonometn.re.ssim.caterpillar.kingo.KingoUrls.calendarPage;
import static org.junit.Assert.*;

public class CalendarPageProcessorTest {

    @Test
    public void testGetCalendar(){
        Site site = Site.me()
                .setDomain("jwgl.lnc.edu.cn")
                .setTimeOut(10000)
                .setRetryTimes(81)
                .setSleepTime(500)
                .setCharset("GBK")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8")
                .addHeader("Referer", calendarPage);

        String termCode = "20180";

        Request calendarQueryRequest = new Request(calendarPage);
        calendarQueryRequest.setMethod(HttpConstant.Method.POST);
        Map<String,Object> requestContent = new HashMap<>();
        requestContent.put("sel_xnxq",termCode);
        calendarQueryRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK");
        calendarQueryRequest.setRequestBody(HttpRequestBody.form(requestContent,"gbk"));

        Spider.create(new CalendarPageProcessor(site))
                .addRequest(calendarQueryRequest)
                .run();
    }
}