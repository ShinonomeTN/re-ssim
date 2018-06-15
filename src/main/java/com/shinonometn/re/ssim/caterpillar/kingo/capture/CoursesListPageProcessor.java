package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.stream.Collectors;

public class CoursesListPageProcessor implements PageProcessor {

    private final Site site;

    public CoursesListPageProcessor(Site site) {
        this.site = site;
    }

    public void process(Page page) {

        Element scriptElement = page
                .getHtml()
                .getDocument()
                .getElementsByTag("script")
                .first();

        if (scriptElement == null) return;

        String scriptContent = scriptElement.html();
        String script = scriptContent.substring(scriptContent.indexOf("<"), scriptContent.lastIndexOf(">"));

        page.putField("courses", Jsoup.parse(script)
                .select("select")
                .first()
                .children()
                .stream()
                .filter(e -> !e.text().equals(""))
                .collect(Collectors.toMap(
                        o -> o.attr("value"),
                        Element::text
                ))
        );

    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        final String subjectQueryPath = "http://jwgl.lnc.edu.cn/ZNPK/Private/List_XNXQKC.aspx?xnxq=";
        final String classInfoQueryPath = "http://jwgl.lnc.edu.cn/ZNPK/KBFB_LessonSel.aspx";

        Site site = Site.me()
                .setRetryTimes(10)
                .setSleepTime(500)
                .setTimeOut(10000)
                .addHeader("Referer", classInfoQueryPath)
                .setCharset("GBK")
                .setUserAgent("Windows 3.1/DOS IE5");

        Spider.create(new CoursesListPageProcessor(site))
                .addUrl(subjectQueryPath + "20171")
                .run();

    }
}
