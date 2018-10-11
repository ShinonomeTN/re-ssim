package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class CourseDetailsPageProcessorTest {

    @Test
    public void testGetASubject() {
        final String loginPageAddress = "http://jwgl.lnc.edu.cn/_data/index_LOGIN.aspx";

        Site site = Site.me()
                .setDomain("jwgl.lnc.edu.cn")
                .setTimeOut(10000)
                .setRetryTimes(81)
                .setSleepTime(500)
                .setCharset("GBK")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/602.4.8 (KHTML, like Gecko) Version/10.0.3 Safari/602.4.8")
                .addHeader("Referer", loginPageAddress);

        Map<String, Object> items = new HashMap<>();

        String username = System.getProperty("re.ssim.test.kingo.username");
        String password = System.getProperty("re.ssim.test.kingo.password");

        Spider.create(new LoginPreparePageProcessor(username, password, "STU", site))
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

            site.addCookie("ASP.NET_SessionId", cookies.get("ASP.NET_SessionId"));

            Request loginRequest = new Request(loginPageAddress);
            loginRequest.setMethod(HttpConstant.Method.POST);
            loginRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK");

            loginRequest.setRequestBody(HttpRequestBody.form(formFields, "gbk"));

            Spider.create(new LoginExecutePageProcessor(site))
                    .addRequest(loginRequest)
                    .run();

            File output = new File("./subject.json");

            Map<String, Object> form = new HashMap<>();
            form.put("gs", "2");
            form.put("txt_yzm", "");
            form.put("Sel_XNXQ", "20171");
            form.put("Sel_KC", "021431");

            final String subjectQueryPage = "http://jwgl.lnc.edu.cn/ZNPK/KBFB_LessonSel_rpt.aspx";
            final String classInfoQueryPage = "http://jwgl.lnc.edu.cn/ZNPK/KBFB_LessonSel.aspx";

            Request request = new Request(subjectQueryPage);
            request.setMethod(HttpConstant.Method.POST);
            request.setRequestBody(HttpRequestBody.form(form, "gbk"));

            site.getHeaders().put("Referer", classInfoQueryPage);

            Spider.create(new CourseDetailsPageProcessor(site))
                    .addRequest(request)
                    .addPipeline((r, t) -> {
                        try {
                            new ObjectMapper().writeValue(output, r.get("subject"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .run();
        }
    }

}