package io.github.admin4j.http.util.core;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author andanyang
 * @since 2022/5/10 14:24
 */
public class HttpLogger implements HttpLoggingInterceptor.Logger {
    private static final Marker MARKER = MarkerFactory.getMarker("OKHTTP");
    private static final Logger STATIC_LOG = LoggerFactory.getLogger(HttpLogger.class);
    private static final long MAX_BODY_PEEK_BYTES = 4096;

    private final Logger logger;

    public HttpLogger() {
        this.logger = LoggerFactory.getLogger(HttpLogger.class);
    }

    public HttpLogger(Logger logger) {
        if (logger != null) {
            this.logger = logger;
        } else {
            this.logger = LoggerFactory.getLogger(HttpLogger.class);
        }
    }

    public HttpLogger(String logName) {

        if (StringUtils.isBlank(logName)) {
            this.logger = LoggerFactory.getLogger(HttpLogger.class);
        } else {
            this.logger = LoggerFactory.getLogger(logName);
        }
    }

    @Override
    public void log(String message) {

        logger.debug(MARKER, message);
    }

    // ======================== Structured Request Logging ========================

    /**
     * 打印请求日志（请求发出前调用）。
     *
     * @param config  当前配置
     * @param request OkHttp 请求对象
     */
    public static void logRequest(HttpConfig config, Request request) {
        if (config == null || !config.isLogEnabled() || request == null) {
            return;
        }
        StringBuilder sb = new StringBuilder("HTTP Request ");
        appendMethodAndUrl(sb, config, request);
        if (config.isLogRequestHeaders()) {
            sb.append("\n  Headers: ").append(request.headers());
        }
        if (config.isLogRequestParams()) {
            RequestBody body = request.body();
            if (body != null) {
                try {
                    Buffer buffer = new Buffer();
                    body.writeTo(buffer);
                    sb.append("\n  Body: ").append(buffer.readUtf8());
                } catch (Exception e) {
                    sb.append("\n  Body: [unable to read]");
                }
            }
        }
        STATIC_LOG.info(MARKER, sb.toString());
    }

    /**
     * 打印响应日志（请求成功后调用）。
     *
     * @param config     当前配置
     * @param request    OkHttp 请求对象
     * @param response   OkHttp 响应对象
     * @param durationMs 请求耗时（毫秒）
     */
    public static void logResponse(HttpConfig config, Request request, Response response, long durationMs) {
        if (config == null || !config.isLogEnabled() || request == null || response == null) {
            return;
        }
        StringBuilder sb = new StringBuilder("HTTP Response ");
        appendMethodAndUrl(sb, config, request);
        sb.append(" -> ").append(response.code()).append(" (").append(durationMs).append("ms)");
        if (config.isLogResponseHeaders()) {
            sb.append("\n  Headers: ").append(response.headers());
        }
        if (config.isLogResponseBody()) {
            try {
                ResponseBody body = response.peekBody(MAX_BODY_PEEK_BYTES);
                String bodyStr = body.string();
                if (body.contentLength() > MAX_BODY_PEEK_BYTES || bodyStr.length() >= MAX_BODY_PEEK_BYTES) {
                    bodyStr = bodyStr.substring(0, Math.min(bodyStr.length(), (int) MAX_BODY_PEEK_BYTES)) + "... (truncated)";
                }
                sb.append("\n  Body: ").append(bodyStr);
            } catch (Exception e) {
                sb.append("\n  Body: [unable to read]");
            }
        }
        STATIC_LOG.info(MARKER, sb.toString());
    }

    /**
     * 打印 HTTP 响应失败日志（非 2xx 状态码时调用）。
     *
     * @param config   当前配置
     * @param request  OkHttp 请求对象
     * @param response OkHttp 响应对象
     */
    public static void logFailureResponse(HttpConfig config, Request request, Response response) {
        if (config == null || !config.isLogEnabled() || !config.isLogOnFailure()) {
            return;
        }
        StringBuilder sb = new StringBuilder("HTTP Request Failed ");
        appendMethodAndUrl(sb, config, request);
        sb.append(" -> ").append(response.code());
        if (config.isLogResponseBody()) {
            try {
                ResponseBody body = response.peekBody(MAX_BODY_PEEK_BYTES);
                String bodyStr = body.string();
                if (body.contentLength() > MAX_BODY_PEEK_BYTES || bodyStr.length() >= MAX_BODY_PEEK_BYTES) {
                    bodyStr = bodyStr.substring(0, Math.min(bodyStr.length(), (int) MAX_BODY_PEEK_BYTES)) + "... (truncated)";
                }
                sb.append("\n  Body: ").append(bodyStr);
            } catch (Exception e) {
                sb.append("\n  Body: [unable to read]");
            }
        }
        STATIC_LOG.error(MARKER, sb.toString());
    }

    /**
     * 打印请求失败日志。
     *
     * @param config  当前配置
     * @param request OkHttp 请求对象
     * @param e       异常
     */
    public static void logFailure(HttpConfig config, Request request, Exception e) {
        if (config == null || !config.isLogEnabled() || !config.isLogOnFailure() || request == null) {
            return;
        }
        StringBuilder sb = new StringBuilder("HTTP Request Failed ");
        appendMethodAndUrl(sb, config, request);
        STATIC_LOG.error(MARKER, sb.toString(), e);
    }

    /**
     * 拼接方法名和 URL 到日志输出。
     * 当 logRequestUrl=true 时输出 [METHOD URL]，否则仅输出 [METHOD]。
     */
    private static void appendMethodAndUrl(StringBuilder sb, HttpConfig config, Request request) {
        if (config.isLogRequestUrl()) {
            sb.append("[").append(request.method()).append(" ").append(request.url()).append("]");
        } else {
            sb.append("[").append(request.method()).append("]");
        }
    }
}
