package com.shinonometn.re.ssim.service.caterpillar.kingo.capture;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarPageProcessor implements PageProcessor {

    private final Site site;

    public CalendarPageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        List<CalendarRangeInfo> info = page
                .getHtml()
                .getDocument()
                // Here pick the most identical element to
                // get tables
                .select("b")
                .stream()
                .map(e -> e.parent().parent().parent())
                // Here handle table info
                .map(this::handleTable)
                // Collect them to a list
                .collect(Collectors.toList());

        page.putField(FIELD_CALENDAR_INFO,info);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static class CalendarRangeInfo {
        public final String term;
        public final Date startDate;
        public final Date endDate;

        public CalendarRangeInfo(String term, Date startDate, Date endDate) {
            this.term = term;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public String toString() {
            return "CalendarRangeInfo{" +
                    "term='" + term + '\'' +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CalendarRangeInfo that = (CalendarRangeInfo) o;
            return Objects.equals(term, that.term) &&
                    Objects.equals(startDate, that.startDate) &&
                    Objects.equals(endDate, that.endDate);
        }

        @Override
        public int hashCode() {

            return Objects.hash(term, startDate, endDate);
        }
    }

    private final SimpleDateFormat generalDateFormat = new SimpleDateFormat("yyyy-M-d H:mm:ss");
    private CalendarRangeInfo handleTable(Element table) {
        Elements tableDataNodes = table.select("td");

        String termTitleText = tableDataNodes
                .first()
                .select("b")
                .text()
                .replace("青果软件学院","");

        String[] termRangeInfoSplited = tableDataNodes
                .get(1)
                .text()
                .replaceAll("[()]","")
                .split("至");

        try {
            return new CalendarRangeInfo(
                    termTitleText,
                    generalDateFormat.parse(termRangeInfoSplited[0]),
                    generalDateFormat.parse(termRangeInfoSplited[1])
            );
        } catch (ParseException e) {
            throw new RuntimeException("Date parse failed.");
        }
    }

    private final static String FIELD_CALENDAR_INFO = "termCalendarInfo";
    public static List<CalendarRangeInfo> getCalendarRangeInfo(ResultItems resultItems) {
        return resultItems.get(FIELD_CALENDAR_INFO);
    }
}
