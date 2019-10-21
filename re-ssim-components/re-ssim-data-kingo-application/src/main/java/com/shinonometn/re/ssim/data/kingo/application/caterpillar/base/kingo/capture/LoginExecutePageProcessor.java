package com.shinonometn.re.ssim.data.kingo.application.caterpillar.base.kingo.capture;

import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
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
            page.putField(FIELD_LOGIN_RESULT, element.text().matches(reg));
            page.putField(FIELD_TEXT, element.text());
        }else{
            page.putField(FIELD_LOGIN_RESULT, false);
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    private final static String FIELD_LOGIN_RESULT = "loginResult";
    public static Boolean getIsLogin(ResultItems resultItems) {
        return (Boolean) resultItems.getAll().getOrDefault(FIELD_LOGIN_RESULT,false);
    }

    private final static String FIELD_TEXT = "reason";
    public static String getReason(ResultItems resultItems) {
        return resultItems.get(FIELD_TEXT);
    }
}