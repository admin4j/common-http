package io.admin4j.http;

import io.admin4j.http.core.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

class AbstractHttpRequest {

    HttpRequest httpRequest;

    @BeforeEach
    void init() {

        HttpConfig httpConfig = new HttpConfig();
        httpRequest = new HttpRequest(httpConfig);
    }

    @Test
    void escapeString() throws UnsupportedEncodingException {

        String m = MediaTypeEnum.JSON.getMediaType().toString();
        System.out.println(m);


        HttpConfig httpConfig = new HttpConfig();
        HttpRequest httpRequest = new HttpRequest(httpConfig);

        String str = "abc=adc&acd+3";
        String utf8 = URLEncoder.encode(str, "utf8");
        System.out.println(utf8);
        String s = httpRequest.escapeString(str);
        System.out.println(s);
    }

    @Test
    void buildUrl() {

        Pair<?>[] ps = new Pair[1];
        ps[0] = Pair.of("name", "value");
        String buildUrl = httpRequest.buildUrl("https://blog.csdn.net/", ps, null);
        System.out.println("buildUrl = " + buildUrl);
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("test", "啊啊哈");
        map.put("test1", "1");
        map.put("test2", "2");
        map.put("test3", "3");
        buildUrl = httpRequest.buildUrl("https://blog.csdn.net/?", null, map);
        System.out.println("buildUrl = " + buildUrl);
    }

    @Test
    void mediaTypeEnum() {
        MediaTypeEnum of = MediaTypeEnum.of("application/json");
        System.out.println("of = " + of);
    }

}
