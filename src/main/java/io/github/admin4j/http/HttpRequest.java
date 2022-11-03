package io.github.admin4j.http;

import io.github.admin4j.http.core.HttpHeaderKey;
import io.github.admin4j.http.core.MediaTypeEnum;
import io.github.admin4j.http.core.Method;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/10/14 15:43
 */
//@Data
public class HttpRequest {

    private Method method;
    private MediaTypeEnum mediaType;
    private String url;
    private Map<String, Object> form = new HashMap<>();
    private Map<String, Object> headers = new HashMap<>();
    private Object body;

    public static HttpRequest post(String url) {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.url = url;
        httpRequest.method = Method.POST;
        return httpRequest;
    }

    public static HttpRequest get(String url) {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.url = url;
        httpRequest.method = Method.GET;
        return httpRequest;
    }

    public HttpRequest userAgent(String userAgent) {

        this.headers.put(HttpHeaderKey.USER_AGENT, userAgent);
        return this;
    }

    public HttpRequest referer(String referer) {

        this.headers.put(HttpHeaderKey.REFERER, referer);
        return this;
    }

    public HttpRequest header(String key, String value) {

        this.headers.put(key, value);
        return this;
    }

    public HttpRequest headers(Map<String, Object> headers) {

        this.headers = headers;
        return this;
    }

    public HttpRequest form(String key, Object value) {

        this.mediaType = MediaTypeEnum.FORM;
        form.put(key, value);
        return this;
    }

    public HttpRequest body(Object body) {

        this.mediaType = MediaTypeEnum.JSON;
        this.body = body;
        return this;
    }

    public Response execute() {

        return null;
    }

    public <T> T execute(Class<T> clas) {

        return null;
    }
}
