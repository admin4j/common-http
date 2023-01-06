package io.github.admin4j.http.core;

import io.github.admin4j.http.factory.HttpClientFactory;
import okhttp3.OkHttpClient;

/**
 * @author andanyang
 * @since 2022/11/3 16:32
 */
public class HttpDefaultConfig {

    /**
     * 默认的单列OkhttpClient
     */
    private static OkHttpClient DEFAULT_HTTP_CLIENT;
    private static HttpConfig DEFAULT_CONFIG;

    public static HttpConfig get() {
        if (DEFAULT_CONFIG == null) {
            DEFAULT_CONFIG = new HttpConfig();
        }
        return DEFAULT_CONFIG;
    }

    public static void set(HttpConfig httpConfig) {
        DEFAULT_CONFIG = httpConfig;
    }

    public static OkHttpClient getClient() {
        if (DEFAULT_HTTP_CLIENT == null) {
            DEFAULT_HTTP_CLIENT = HttpClientFactory.okHttpClient(HttpDefaultConfig.get());
        }
        return DEFAULT_HTTP_CLIENT;
    }

    public static void setClient(OkHttpClient httpClient) {
        DEFAULT_HTTP_CLIENT = httpClient;
    }
}
