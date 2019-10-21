package com.shinonometn.re.ssim.data.kingo.application.caterpillar.base.kingo.capture;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        page.putField("cookies", page.getHeaders().get("Set-Cookie"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    private static final String FIELD_IS_READY = "ready";
    private static final String FIELD_FROM_FIELDS = "formFields";
    private static final String FIELD_COOKIES = "cookies";

    @SuppressWarnings("unchecked")
    public static Boolean getIsReady(Map<String,Object> resultItems) {
        return (Boolean) resultItems.getOrDefault(FIELD_IS_READY, false);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getFormFields(Map<String,Object> resultItems) {
        return (Map<String, Object>) resultItems.get(FIELD_FROM_FIELDS);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getCookie(Map<String,Object> resultItems) {
        List<String> cookies = (List<String>) resultItems.get(FIELD_COOKIES);

        if(cookies == null) return null;

        return cookies
                .stream()
                .flatMap(s -> Stream.of(s.split(";")))
                .flatMap(s -> Stream.of(new String[][]{s.split("=")}))
                .collect(Collectors.toMap(ss -> ss[0], ss -> ss[1]));
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
