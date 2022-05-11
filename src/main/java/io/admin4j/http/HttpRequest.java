package io.admin4j.http;

import io.admin4j.http.core.AbstractHttpRequest;
import io.admin4j.http.core.HttpHeaderKey;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/4/21 11:27
 */
@Accessors
public class HttpRequest extends AbstractHttpRequest {

    public static final io.admin4j.http.HttpConfig DEFAULT_HTTP_CONFIG = new io.admin4j.http.HttpConfig();
    private static volatile OkHttpClient SINGLETON_CLIENT = null;
    @Getter
    @Setter
    protected MediaTypeEnum mediaTypeEnum;

    protected OkHttpClient httpClient;
    @Getter
    @Setter
    protected String userAgent;

    public HttpRequest() {
        init();
    }

    public HttpRequest(io.admin4j.http.HttpConfig config) {
        httpClient = config.getHttpClient();
        init();
    }

    public HttpRequest(OkHttpClient.Builder builder) {
        httpClient = builder.build();
        init();
    }


    @Override
    public OkHttpClient getHttpClient() {
        if (null == SINGLETON_CLIENT) {

            synchronized (HttpRequest.class) {
                if (null == SINGLETON_CLIENT) {
                    SINGLETON_CLIENT = DEFAULT_HTTP_CONFIG.getHttpClient();
                }
            }
        }
        return SINGLETON_CLIENT;
    }

    public void init() {

        if (StringUtils.isNotBlank(getUserAgent())) {
            defaultHeaderMap.put(HttpHeaderKey.USER_AGENT, getUserAgent());
        }
    }

    @Override
    public String serializeJSON(Object obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public <T> T deserializeJSON(InputStream in, Type returnType) throws IOException {
        return JSON.parseObject(in, charset, returnType);
    }

    @Override
    public <T> T post(String url, MediaTypeEnum mediaTypeEnum, Object body, Map<String, Object> formParams, Map<String, Object> headerParams, Type returnType) {
        if (mediaTypeEnum == null) {
            mediaTypeEnum = getMediaTypeEnum();
        }
        return super.post(url, mediaTypeEnum, body, formParams, headerParams, returnType);
    }

    public <T> T post(String url, Object body, Map<String, Object> formParams, Map<String, Object> headerParams, Type returnType) {
        return super.post(url, mediaTypeEnum, body, formParams, headerParams, returnType);
    }
}
