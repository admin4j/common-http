package io.github.admin4j.http.util.core;

import lombok.Data;

/**
 * @author andanyang
 * @since 2022/4/21 11:32
 */
@Data
public class HttpLogConfig {


    // ======================== Request Log Configuration ========================

    /**
     * 是否开启请求日志
     */
    private boolean logEnabled = false;
    /**
     * 是否打印请求头
     */
    private boolean logRequestHeaders = false;
    /**
     * 是否打印请求URL
     */
    private boolean logRequestUrl = true;
    /**
     * 是否打印请求参数（query params 和 body）
     */
    private boolean logRequestParams = true;
    /**
     * 是否打印返回参数
     */
    private boolean logResponseBody = true;
    /**
     * 是否打印返回header
     */
    private boolean logResponseHeaders = false;
    /**
     * 失败时是否打印日志
     */
    private boolean logOnFailure = true;

    /**
     * 创建当前配置的深拷贝副本。
     *
     * @return 新的 HttpLogConfig 实例
     */
    public HttpLogConfig copy() {
        HttpLogConfig copy = new HttpLogConfig();
        copy.setLogEnabled(this.logEnabled);
        copy.setLogRequestHeaders(this.logRequestHeaders);
        copy.setLogRequestUrl(this.logRequestUrl);
        copy.setLogRequestParams(this.logRequestParams);
        copy.setLogResponseBody(this.logResponseBody);
        copy.setLogResponseHeaders(this.logResponseHeaders);
        copy.setLogOnFailure(this.logOnFailure);
        return copy;
    }

}
