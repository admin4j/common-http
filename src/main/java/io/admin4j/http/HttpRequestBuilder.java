package io.admin4j.http;

import io.admin4j.http.core.HttpHeaderKey;
import io.admin4j.http.core.Method;
import lombok.Getter;
import okhttp3.Call;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/5/10 15:26
 */
@Getter
public class HttpRequestBuilder {
    private String url;
    private Method method;
    private Map<String, Object> header;
    private Object body;
    private Map<String, Object> form;
    private MediaTypeEnum mediaType;

    HttpRequestBuilder(String url, Method method) {
        this.url = url;
        this.method = method;
    }

    public HttpRequestBuilder addHeader(String key, Object value) {
        if (header == null) {
            header = new HashMap<String, Object>(8);
        }
        header.put(key, value);
        return this;
    }

    public HttpRequestBuilder header(Map<String, Object> header) {
        this.header = header;
        if (mediaType != null) {
            mediaType(mediaType);
        }
        return this;
    }

    public HttpRequestBuilder addForm(String key, Object value) {
        if (form == null) {
            form = new HashMap<String, Object>(8);
        }
        form.put(key, value);
        return this;
    }

    public HttpRequestBuilder form(Map<String, Object> form) {
        this.form = form;
        return this;
    }

    public HttpRequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    public HttpRequestBuilder mediaType(MediaTypeEnum mediaType) {
        this.mediaType = mediaType;
        addHeader(HttpHeaderKey.CONTENT_TYPE, mediaType);
        return this;
    }

    private Call buildCall() {
        if (mediaType != null) {
            addHeader(HttpHeaderKey.CONTENT_TYPE, mediaType.toString());
        }
        return HttpUtil.getHttpRequest().buildCall(url, method, this.body, form, header);
    }

    public ResponseBody execute() throws IOException {

        return buildCall().execute().body();
    }

    public <T> T execute(Type returnType) {

        Call call = buildCall();
        return HttpUtil.getHttpRequest().execute(call, returnType);
    }
}
