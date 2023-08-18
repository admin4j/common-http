package io.github.admin4j.http.core;

import lombok.Data;
import okhttp3.logging.HttpLoggingInterceptor;

import java.net.Proxy;

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
     * 日志名称
     */
    private String logName = "";

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
    private String referer = "";
    /**
     * 是否支持cookie
     */
    private boolean cookie = false;
    /**
     * 是否共用连接池
     */
    private boolean commonConnectionPool = true;
    private ProxyConfig proxy;


    @Data
    public static class ProxyConfig {

        private Proxy.Type type = Proxy.Type.HTTP;
        private String host;
        private Integer port = 80;
        private String userName;
        private String password;
    }
}
