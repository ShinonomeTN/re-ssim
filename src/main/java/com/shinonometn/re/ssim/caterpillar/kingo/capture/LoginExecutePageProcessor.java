package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoginExecutePageProcessor implements PageProcessor {

    private final Site site;

    public LoginExecutePageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        Element element = page.getHtml().getDocument().getElementById("divLogNote").child(0);
        String reg = ".*加载权限数据.*";
        page.putField("loginResult", element.text().matches(reg));

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        final String loginPageAddress = "http://jwgl.lnc.edu.cn/_data/index_LOGIN.aspx";

        Site site = Site.me()
                .setDomain("jwgl.lnc.edu.cn")
                .setTimeOut(5000)
                .setRetryTimes(3)
                .setSleepTime(500)
                .setCharset("GBK")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8")
                .addHeader("Referer", loginPageAddress);

        Map<String, Object> items = new HashMap<>();

        Spider.create(new LoginPreparePageProcessor("14601120234", "14601120234", "STU", site))
                .addUrl(loginPageAddress)
                .addPipeline((r, t) -> items.putAll(r.getAll()))
                .run();

        if ((Boolean) items.get("ready")) {
            Map<String, Object> formFields = (Map<String, Object>) items.get("formFields");
            Map<String, String> cookies = ((List<String>) items.get("cookies"))
                    .stream()
                    .flatMap(s -> Stream.of(s.split(";")))
                    .flatMap(s -> Stream.of(new String[][]{s.split("=")}))
                    .collect(Collectors.toMap(ss -> ss[0], ss -> ss[1]));

            Request loginRequest = new Request(loginPageAddress);
            loginRequest.setMethod(HttpConstant.Method.POST);
            site.addCookie("ASP.NET_SessionId", cookies.get("ASP.NET_SessionId"));
            loginRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK");

            loginRequest.setRequestBody(HttpRequestBody.form(formFields, "gbk"));

            Spider.create(new LoginExecutePageProcessor(site))
                    .addRequest(loginRequest)
                    .run();
        }
    }
}