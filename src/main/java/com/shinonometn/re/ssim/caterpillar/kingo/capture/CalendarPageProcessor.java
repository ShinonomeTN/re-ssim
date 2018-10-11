package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

        page.putField("termCalendarInfo",info);
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
}
