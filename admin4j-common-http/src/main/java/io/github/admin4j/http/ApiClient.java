package io.github.admin4j.http;

import com.admin4j.json.JSONUtil;
import io.github.admin4j.http.core.*;
import io.github.admin4j.http.factory.HttpClientFactory;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/4/21 11:27
 */
@Accessors
@Slf4j
public class ApiClient extends AbstractHttpRequest {
    public ApiClient() {
        super();
    }

    public ApiClient(HttpConfig httpConfig) {

        if (StringUtils.isBlank(httpConfig.getLogName())) {
            httpConfig.setLogName(loggerName());
        }
        okHttpClient = HttpClientFactory.okHttpClient(httpConfig);
        this.followRedirects = httpConfig.isFollowRedirects();
        headerMap.put(HttpHeaderKey.USER_AGENT, httpConfig.getUserAgent());
        headerMap.put(HttpHeaderKey.REFERER, httpConfig.getReferer());
    }

    protected String loggerName() {
        return this.getClass().getName();
    }

    @Override
    public String serializeJSON(Object obj) {
        return JSONUtil.toJSONString(obj);
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

    public void asyncPostForm(String url, Map<String, Object> formParams, Callback callback) {

        asyncPost(url, MediaTypeEnum.FORM, null, formParams, null, callback);
    }

    public Response postFormData(String url, Map<String, Object> formParams) {

        return post(url, MediaTypeEnum.FORM_DATA, null, formParams, null);
    }

    public void asyncPostFormData(String url, Map<String, Object> formParams, Callback callback) {

        asyncPost(url, MediaTypeEnum.FORM_DATA, null, formParams, null, callback);
    }

    public Response post(String url, Object body) {

        return post(url, MediaTypeEnum.JSON, body, null, null);
    }

    public void asyncPost(String url, Object body, Callback callback) {

        asyncPost(url, MediaTypeEnum.JSON, body, null, null, callback);
    }

    public InputStream down(String url) {
        return executeByteStream(url, Method.GET, null, null, null, null, null, null);
    }
}
