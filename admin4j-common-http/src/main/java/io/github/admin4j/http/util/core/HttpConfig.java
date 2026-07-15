package io.github.admin4j.http.util.core;

import lombok.Data;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;

import java.net.Proxy;
import java.util.ArrayList;
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
    private HttpProxyConfig proxy;

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
     * 日志配置
     */
    private HttpLogConfig logConfig = new HttpLogConfig();

    // IMPORTANT: 新增字段时必须同步更新此方法
    public HttpConfig copy() {
        HttpConfig copy = new HttpConfig();
        copy.loggLevel = this.loggLevel;
        copy.logName = this.logName;
        copy.readTimeout = this.readTimeout;
        copy.connectTimeout = this.connectTimeout;
        copy.followRedirects = this.followRedirects;
        copy.maxIdleConnections = this.maxIdleConnections;
        copy.keepAliveDuration = this.keepAliveDuration;
        copy.userAgent = this.userAgent;
        copy.referer = this.referer;
        copy.cookie = this.cookie;
        copy.commonConnectionPool = this.commonConnectionPool;
        copy.proxy = this.proxy != null ? this.proxy.copy() : null;
        copy.protocols = this.protocols != null ? new ArrayList<>(this.protocols) : null;
        copy.logConfig = this.logConfig != null ? this.logConfig.copy() : null;
        return copy;
    }

    /**
     * 代理配置
     */
    @Data
    public static class HttpProxyConfig {

        private Proxy.Type type = Proxy.Type.HTTP;
        private String host;
        private Integer port = 80;
        private String userName;
        private String password;

        /**
         * 创建当前代理配置的副本。
         *
         * @return 新的 HttpProxyConfig 实例
         */
        public HttpProxyConfig copy() {
            HttpProxyConfig copy = new HttpProxyConfig();
            copy.setType(this.type);
            copy.setHost(this.host);
            copy.setPort(this.port);
            copy.setUserName(this.userName);
            copy.setPassword(this.password);
            return copy;
        }

    }
}
