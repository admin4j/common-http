package io.github.admin4j.http.core;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.util.Map;

/**
 * @author andanyang
 * @since 2022/4/21 13:28
 */
@Slf4j
public abstract class AbstractHttpRequest extends AbstractHttpExecute {


    /**
     * 每个实例可以自带的OkHttpClient
     */
    protected OkHttpClient okHttpClient;


    public AbstractHttpRequest() {

        HttpConfig httpConfig = HttpDefaultConfig.get();
        headerMap.put(HttpHeaderKey.USER_AGENT, httpConfig.getUserAgent());
        headerMap.put(HttpHeaderKey.REFERER, httpConfig.getReferer());
        init();
    }


    /**
     * 初始化数据 baseUrl 等
     */
    protected void init() {

    }

    @Override
    public OkHttpClient getHttpClient() {
        return okHttpClient == null ? HttpDefaultConfig.getClient() : okHttpClient;
    }

    // ======================= GET POST ===============
    public Response get(String path, Map<String, Object> queryMap, Pair<?>... queryParams) {

        Call call = buildGet(path, queryMap, queryParams);
        return execute(call);
    }

    public Response post(String url,
                         MediaTypeEnum mediaTypeEnum,
                         Object body,
                         Map<String, Object> formParams,
                         Map<String, Object> headerParams) {

        Call call = buildPost(url, mediaTypeEnum, body, formParams, headerParams);
        return execute(call);
    }

    public void asyncPost(String url,
                          MediaTypeEnum mediaTypeEnum,
                          Object body,
                          Map<String, Object> formParams,
                          Map<String, Object> headerParams,
                          Callback callback) {

        Call call = buildPost(url, mediaTypeEnum, body, formParams, headerParams);
        executeAsync(call, callback);
    }
}
