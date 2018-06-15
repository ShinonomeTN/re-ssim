package com.shinonometn.re.ssim.caterpillar.kingo.capture;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginPreparePageProcessor implements PageProcessor {

    private final String username;
    private final String password;
    private final String role;


    private final Site site;

    public LoginPreparePageProcessor(String username, String password, String role, Site site) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.site = site;
    }

    @Override
    public void process(Page page) {

        FormElement formElement = (FormElement) page.getHtml().getDocument()
                .getElementsByTag("form")
                .first();

        // Check if login is available
        if (formElement == null || !formElement.tag().getName().equals("form")) {
            page.putField("ready", false);
            return;
        }

        // parse form data
        Map<String, String> formFields = new HashMap<>();
        for (Element element : formElement.elements()) {
            if (element.hasAttr("name")) {
                String attrName = element.attr("name");
                switch (attrName) {
                    case "pcInfo":
                        formFields.put(attrName, site.getUserAgent());
                        break;
                    case "dsdsdsdsdxcxdfgfg":
                        formFields.put(attrName, getEncryptedPassword(username, password));
                        break;
                    case "Sel_Type":
                        formFields.put(attrName, role);
                        break;
                    case "txt_asmcdefsddsd":
                        formFields.put(attrName, username);
                        break;
                    case "typeName":
                        Elements select = formElement.select("select");
                        select.stream()
                                .filter(e -> e.attr("value").equals(role))
                                .forEach(e -> formFields.put(attrName, e.text()));
                        break;
                    default:
                        formFields.put(attrName, element.attr("value"));
                        break;
                }
            }
        }

        page.putField("ready", true);
        page.putField("formFields", formFields);
        page.putField("cookies",page.getHeaders().get("Set-Cookie"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        final String loginPageAddress = "http://jwgl.lnc.edu.cn/_data/index_LOGIN.aspx";

        Site site = Site.me()
                .setRetryTimes(10)
                .setSleepTime(500)
                .setTimeOut(10000)
                .addHeader("Referer", loginPageAddress)
                .setCharset("GBK")
                .setUserAgent("Windows 3.1/DOS IE5");

        Spider.create(new LoginPreparePageProcessor("14601120234", "14601120234", "STU", site))
                .addUrl(loginPageAddress)
                .run();
    }

    /*
     *
     * private procedures
     *
     * */

    private static String getEncryptedPassword(String username, String password) {
        return md5(username + md5(password).substring(0, 30).toUpperCase() + "12749").substring(0, 30).toUpperCase();
    }

    private static String md5(String s) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignore) {

        }
        // 计算md5函数
        assert md != null;
        md.update(s.getBytes());
        // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
        return new BigInteger(1, md.digest()).toString(16);
    }
}
