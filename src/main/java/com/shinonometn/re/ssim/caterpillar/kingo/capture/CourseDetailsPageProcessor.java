package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import com.shinonometn.re.ssim.caterpillar.kingo.KingoRawCourseParser;
import com.shinonometn.re.ssim.caterpillar.kingo.pojo.Course;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class CourseDetailsPageProcessor implements PageProcessor {

    private final Site site;

    public CourseDetailsPageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        Document document = page.getHtml().getDocument();
        page.putField(FIELD_SUBJECT, KingoRawCourseParser.parseFromHTML(document));
    }

    @Override
    public Site getSite() {
        return site;
    }

    private final static String FIELD_SUBJECT = "subject";

    public static Course getSubject(ResultItems resultItems) {
        return resultItems.get(FIELD_SUBJECT);
    }
}