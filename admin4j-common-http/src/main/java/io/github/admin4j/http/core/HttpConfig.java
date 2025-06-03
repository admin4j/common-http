package io.github.admin4j.http.core;

import lombok.Data;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;

import java.net.Proxy;
import java.util.List;

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

    /**
     * 代理配置
     */
    private ProxyConfig proxy;

    /**
     * 请求协议
     * - HTTP_1_0: HTTP/1.0
     * - HTTP_1_1: HTTP/1.1
     * - HTTP_2: HTTP/2
     * <p>
     * 按优先顺序排列。如果列表包含 Protocol.H2_PRIOR_KNOWLEDGE ，则该协议必须是唯一的协议，并且不支持 HTTPS URL。
     * 否则，列表必须包含 Protocol. HTTP_1_1。该列表不得包含 null 或 Protocol. HTTP_1_0
     */
    private List<Protocol> protocols;


    /**
     * 代理配置
     */
    @Data
    public static class ProxyConfig {

        private Proxy.Type type = Proxy.Type.HTTP;
        private String host;
        private Integer port = 80;
        private String userName;
        private String password;
    }
}
