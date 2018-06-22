package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class LoginExecutePageProcessor implements PageProcessor {

    private final Site site;

    public LoginExecutePageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        Element element = page.getHtml().getDocument().getElementById("divLogNote");

        if(element != null){
            element = element.child(0);
            String reg = ".*加载权限数据.*";
            page.putField("loginResult", element.text().matches(reg));
        }else{
            page.putField("loginResult", false);
        }

    }

    @Override
    public Site getSite() {
        return site;
    }
}