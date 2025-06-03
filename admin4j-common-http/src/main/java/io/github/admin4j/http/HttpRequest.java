package io.github.admin4j.http;

import io.github.admin4j.http.core.HttpHeaderKey;
import io.github.admin4j.http.core.MediaTypeEnum;
import io.github.admin4j.http.core.Method;
import io.github.admin4j.http.core.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/10/14 15:43
 */
@Getter
public class HttpRequest {

    private Method method;
    private MediaTypeEnum mediaType;
    private Map<String, Object> queryMap;
    private Pair<?>[] queryParams;
    private String url;
    @Setter
    @Accessors
    private Map<String, Object> form;

    private Map<String, Object> headers;
    @Setter
    @Accessors
    private Object body;

    public static HttpRequest get(String url) {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.url = url;
        httpRequest.method = Method.GET;
        return httpRequest;
    }

    public static HttpRequest post(String url) {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.url = url;
        httpRequest.method = Method.POST;
        return httpRequest;
    }

    public static HttpRequest put(String url) {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.url = url;
        httpRequest.method = Method.PUT;
        return httpRequest;
    }

    public static HttpRequest delete(String url) {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.url = url;
        httpRequest.method = Method.DELETE;
        return httpRequest;
    }

    public static HttpRequest patch(String url) {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.url = url;
        httpRequest.method = Method.PATCH;
        return httpRequest;
    }

    /**
     * 设置请求方法
     *
     * @param method
     * @return
     */
    public HttpRequest method(Method method) {
        this.method = method;
        return this;
    }

    public HttpRequest mediaType(MediaTypeEnum mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public HttpRequest userAgent(String userAgent) {
        if (headers == null) {
            headers = new HashMap<>(8);
        }
        this.headers.put(HttpHeaderKey.USER_AGENT, userAgent);
        return this;
    }

    public HttpRequest referer(String referer) {
        if (headers == null) {
            headers = new HashMap<>(8);
        }
        this.headers.put(HttpHeaderKey.REFERER, referer);
        return this;
    }

    public HttpRequest header(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>(8);
        }
        this.headers.put(key, value);
        return this;
    }

    public HttpRequest headers(Map<String, Object> headers) {

        if (headers == null) {
            headers = new HashMap<>(8);
        }
        this.headers = headers;
        return this;
    }


    public HttpRequest queryMap(String key, String value) {

        if (queryMap == null) {
            queryMap = new HashMap<>(8);
        }
        this.queryMap.put(key, value);
        return this;
    }

    public HttpRequest queryParams(Pair<?>... queryParams) {

        this.queryParams = queryParams;
        return this;
    }

    public HttpRequest form(String key, Object value) {

        this.mediaType = MediaTypeEnum.FORM;
        if (form == null) {
            form = new HashMap<>(8);
        }
        form.put(key, value);
        return this;
    }

    public HttpRequest body(Object body) {

        this.mediaType = MediaTypeEnum.JSON;
        this.body = body;
        return this;
    }

    ////============= 发送http 请求 =================
    //
    //public Response execute() {
    //
    //    ApiClient apiClient = HttpUtil.getClient();
    //    Call call = apiClient.buildCall(url, method, mediaType, queryParams, queryMap, body, form, headers);
    //    return apiClient.execute(call);
    //}
    //
    //public void asyncExecute(Callback callback) {
    //
    //    ApiClient apiClient = HttpUtil.getClient();
    //    Call call = apiClient.buildCall(url, method, mediaType, queryParams, queryMap, body, form, headers);
    //    apiClient.executeAsync(call, callback);
    //}
    //
    //public <T> T execute(Class<T> clas) throws IOException {
    //
    //    @Cleanup InputStream is = Objects.requireNonNull(execute().body()).byteStream();
    //    return JSONUtil.parseObject(is, clas);
    //}
}
