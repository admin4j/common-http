package io.github.admin4j.http.factory;

import io.github.admin4j.http.HttpConfig;
import io.github.admin4j.http.core.HttpLogger;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author andanyang
 * @since 2022/10/9 17:39
 */
public class HttpClientFactory {

    public static OkHttpClient okHttpClient(HttpConfig httpConfig) {

        ConnectionPool connectionPool = new ConnectionPool(httpConfig.getMaxIdleConnections(), httpConfig.getKeepAliveDuration(), TimeUnit.SECONDS);

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(httpConfig.getLoggLevel());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .followRedirects(httpConfig.isFollowRedirects())
                .addNetworkInterceptor(logInterceptor)
                .readTimeout(Duration.ofSeconds(httpConfig.getReadTimeout()))
                .connectTimeout(Duration.ofSeconds(httpConfig.getReadTimeout()));
        return builder.build();
    }
}
