package com.shinonometn.re.ssim.service.caterpillar.kingo.capture;

import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Map;
import java.util.stream.Collectors;

public class CalendarListPageProcessor implements PageProcessor {

    private final Site site;

    public CalendarListPageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        Element selectElement = page
                .getHtml()
                .getDocument()
                .getElementsByTag("select")
                .first();

        if (selectElement == null) return;

        Map<String, String> calendarList = selectElement
                .children()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.attr("value"),
                        Element::text
                ));

        page.putField(FIELD_CALENDAR_LIST, calendarList);
    }

    @Override
    public Site getSite() {
        return site;
    }

    private final static String FIELD_CALENDAR_LIST = "calendarList";
    public static Map<String, String> getCalendarList(ResultItems resultItems) {
        return resultItems.get(FIELD_CALENDAR_LIST);
    }
}
