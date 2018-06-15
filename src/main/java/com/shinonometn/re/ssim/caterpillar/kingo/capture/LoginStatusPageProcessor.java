package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class LoginStatusPageProcessor implements PageProcessor {

    private final Site site;

    public LoginStatusPageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {

        Element element = page.getHtml().getDocument().getElementById("txt_yzm");
        page.putField("isLogin", element.parent().attr("style").equals("display:none"));

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        final String classInfoQueryPage = "http://jwgl.lnc.edu.cn/ZNPK/KBFB_LessonSel.aspx";

        Site site = Site.me()
                .setRetryTimes(10)
                .setSleepTime(500)
                .setTimeOut(10000)
                .addHeader("Referer", classInfoQueryPage)
                .setCharset("GBK")
                .setUserAgent("Windows 3.1/DOS IE5");

        Spider.create(new LoginStatusPageProcessor(site))
                .addUrl(classInfoQueryPage)
                .run();
    }
}
