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
}
