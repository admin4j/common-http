package io.github.admin4j.http.core;

import io.github.admin4j.http.factory.HttpClientFactory;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

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
    /**
     * 默认的公共链接池
     */
    private static volatile ConnectionPool DEFAULT_CONNECTIONPOOL;

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

    public static ConnectionPool getConnectionPool() {
        if (DEFAULT_CONNECTIONPOOL == null) {
            synchronized (HttpDefaultConfig.class) {
                if (DEFAULT_CONNECTIONPOOL == null) {
                    DEFAULT_CONNECTIONPOOL = new ConnectionPool(HttpDefaultConfig.get().getMaxIdleConnections(), HttpDefaultConfig.get().getKeepAliveDuration(), TimeUnit.SECONDS);
                }
            }
        }
        return DEFAULT_CONNECTIONPOOL;
    }

    public static void setConnectionPool(ConnectionPool connectionPool) {

        DEFAULT_CONNECTIONPOOL = connectionPool;
    }
}
