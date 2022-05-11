package io.admin4j.http;

import io.admin4j.http.core.HttpLogger;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.time.Duration;

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
    private String userAgent = "OKHTTP";

    public OkHttpClient getHttpClient() {

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(getLoggLevel());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(isFollowRedirects())
                .addNetworkInterceptor(logInterceptor)
                .readTimeout(Duration.ofSeconds(getReadTimeout()))
                .connectTimeout(Duration.ofSeconds(getReadTimeout()));
        return builder.build();
    }
}
