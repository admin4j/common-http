package io.github.admin4j.http;

import io.github.admin4j.http.core.HttpLogger;
import lombok.Data;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author andanyang
 * @since 2022/4/21 11:32
 */
@Data
public class HttpConfig {


    /**
     * 日志等级
     */
    private HttpLoggingInterceptor.Level loggLevel = HttpLoggingInterceptor.Level.BODY;

    /**
     * 读取超时时间，秒
     */
    private long readTimeout = 30;
    /**
     * 链接超时时间
     */
    private long connectTimeout = 30;

    private boolean followRedirects = false;

    /**
     * 最大的连接数
     */
    private int maxIdleConnections = 5;

    /**
     * 最大的kepAlive 时间 秒
     */
    private long keepAliveDuration = 5;

    private String userAgent = "OKHTTP";

    public OkHttpClient buildHttpClient() {

        ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(getLoggLevel());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .followRedirects(isFollowRedirects())
                .addNetworkInterceptor(logInterceptor)
                .readTimeout(Duration.ofSeconds(getReadTimeout()))
                .connectTimeout(Duration.ofSeconds(getReadTimeout()));
        return builder.build();
    }
}
