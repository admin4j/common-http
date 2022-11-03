package io.github.admin4j.http;

import com.alibaba.fastjson.JSON;
import io.github.admin4j.http.core.*;
import io.github.admin4j.http.factory.HttpClientFactory;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.util.Map;

/**
 * @author andanyang
 * @since 2022/4/21 11:27
 */
@Accessors
public class ApiClient extends AbstractHttpRequest {

    @Setter
    private OkHttpClient okHttpClient;

    public ApiClient(HttpConfig httpConfig) {
        super();
        okHttpClient = HttpClientFactory.okHttpClient(httpConfig);
    }

    public ApiClient() {
        super();
        okHttpClient = HttpClientFactory.okHttpClient(HttpDefaultConfig.get());
    }

    @Override
    public OkHttpClient getHttpClient() {
        return okHttpClient;
    }


    @Override
    public String serializeJSON(Object obj) {
        return JSON.toJSONString(obj);
    }


    //=============== request ===============
    public Response get(String path, Map<String, Object> queryMap) {
        return get(path, queryMap, null);
    }

    public Response get(String path, Pair<?>... queryParams) {
        return get(path, null, queryParams);
    }


    public Response postForm(String url, Map<String, Object> formParams) {

        return post(url, MediaTypeEnum.FORM, null, formParams, null);
    }

    public Response postFormData(String url, Map<String, Object> formParams) {

        return post(url, MediaTypeEnum.FORM_DATA, null, formParams, null);

    }

    public Response post(String url, Object body) {

        return post(url, MediaTypeEnum.JSON, body, null, null);
    }
}
