package io.github.admin4j.http.util.core;

import okhttp3.*;
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
public class HttpLogger {
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

    // ======================== Structured Request Logging ========================

    /**
     * 打印请求日志（请求发出前调用）。
     *
     * @param config  当前配置
     * @param request OkHttp 请求对象
     */
    public static void logRequest(HttpConfig config, Request request) {
        if (config == null || !config.getLogConfig().isLogEnabled() || request == null) {
            return;
        }
        StringBuilder sb = new StringBuilder("HTTP Request ");
        appendMethodAndUrl(sb, config, request);
        if (config.getLogConfig().isLogRequestHeaders()) {
            sb.append("\n- Headers -\n").append(request.headers());
        }
        if (config.getLogConfig().isLogRequestParams()) {
            RequestBody body = request.body();
            if (body != null) {
                appendRequestBody(sb, body);
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
        if (config == null || !config.getLogConfig().isLogEnabled() || request == null || response == null) {
            return;
        }
        StringBuilder sb = new StringBuilder("HTTP Response ");
        appendMethodAndUrl(sb, config, request);
        sb.append(" -> ").append(response.code()).append(" (").append(durationMs).append("ms)");
        if (config.getLogConfig().isLogResponseHeaders()) {
            sb.append("\n  Headers: ").append(response.headers());
        }
        if (config.getLogConfig().isLogResponseBody()) {
            try {
                ResponseBody body = response.peekBody(MAX_BODY_PEEK_BYTES);
                String bodyStr = body.string();
                if (body.contentLength() > MAX_BODY_PEEK_BYTES || bodyStr.length() >= MAX_BODY_PEEK_BYTES) {
                    bodyStr = bodyStr.substring(0, Math.min(bodyStr.length(), (int) MAX_BODY_PEEK_BYTES)) + "... (truncated)";
                }
                sb.append("\n- Body -\n").append(bodyStr);
            } catch (Exception e) {
                sb.append("\n- Body: [unable to read]");
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
    public static void logFailureResponse(HttpConfig config, Request request, Response response, long durationMs) {
        if (config == null || !config.getLogConfig().isLogEnabled() || !config.getLogConfig().isLogOnFailure()) {
            return;
        }
        StringBuilder sb = new StringBuilder("HTTP Response Failed ");
        appendMethodAndUrl(sb, config, request);
        sb.append(" -> ").append(response.code()).append(" (").append(durationMs).append("ms)");
        if (config.getLogConfig().isLogResponseBody()) {
            try {
                ResponseBody body = response.peekBody(MAX_BODY_PEEK_BYTES);
                String bodyStr = body.string();
                if (body.contentLength() > MAX_BODY_PEEK_BYTES || bodyStr.length() >= MAX_BODY_PEEK_BYTES) {
                    bodyStr = bodyStr.substring(0, Math.min(bodyStr.length(), (int) MAX_BODY_PEEK_BYTES)) + "... (truncated)";
                }
                sb.append("\n- Body -\n").append(bodyStr);
            } catch (Exception e) {
                sb.append("\n- Body: [unable to read]");
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
        if (config == null || !config.getLogConfig().isLogEnabled() || !config.getLogConfig().isLogOnFailure() || request == null) {
            return;
        }
        StringBuilder sb = new StringBuilder("HTTP Request Failed ");
        appendMethodAndUrl(sb, config, request);
        STATIC_LOG.error(MARKER, sb.toString(), e);
    }

    /**
     * 将 RequestBody 内容追加到日志输出。
     * 针对 MultipartBody、FormBody 和普通 RequestBody 分别处理。
     */
    private static void appendRequestBody(StringBuilder sb, RequestBody body) {
        try {
            if (body instanceof MultipartBody) {
                appendMultipartBody(sb, (MultipartBody) body);
            } else if (body instanceof FormBody) {
                appendFormBody(sb, (FormBody) body);
            } else {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                sb.append("\n- Body -\n ").append(buffer.readUtf8());
            }
        } catch (Exception e) {
            sb.append("\n- Body : [unable to read]");
        }
    }

    /**
     * 追加 MultipartBody 日志：文件 part 仅打印元信息，普通 part 打印值。
     */
    private static void appendMultipartBody(StringBuilder sb, MultipartBody body) {
        MediaType mediaType = body.type();
        sb.append("\n- Body multipart/").append(mediaType != null ? mediaType.subtype() : "mixed");
        for (int i = 0; i < body.size(); i++) {
            MultipartBody.Part part = body.part(i);
            Headers partHeaders = part.headers();
            String disposition = partHeaders != null ? partHeaders.get("Content-Disposition") : null;
            String name = extractDispositionParam(disposition, "name");
            String filename = extractDispositionParam(disposition, "filename");

            if (filename != null) {
                // 文件类型 part —— 仅打印元信息，不读取文件内容
                RequestBody partBody = part.body();
                MediaType contentType = partBody.contentType();
                long size = -1;
                try {
                    size = partBody.contentLength();
                } catch (Exception ignored) {
                    // ignore
                }
                sb.append("\n    Part: ").append(name != null ? name : "file")
                        .append(" = filename=\"").append(filename).append("\"")
                        .append(", contentType=").append(contentType != null ? contentType : "unknown")
                        .append(", size=").append(formatSize(size));
            } else {
                // 普通表单字段
                try {
                    Buffer buffer = new Buffer();
                    part.body().writeTo(buffer);
                    sb.append("\n    Part: ").append(name != null ? name : "field")
                            .append(" = \"").append(buffer.readUtf8()).append("\"");
                } catch (Exception e) {
                    sb.append("\n    Part: ").append(name != null ? name : "field")
                            .append(" = [unable to read]");
                }
            }
        }
    }

    /**
     * 追加 FormBody 日志：逐个打印表单字段。
     */
    private static void appendFormBody(StringBuilder sb, FormBody body) {
        sb.append("\n- Body form-urlencoded");
        for (int i = 0; i < body.size(); i++) {
            sb.append("\n    Field: ").append(body.name(i))
                    .append(" = \"").append(body.value(i)).append("\"");
        }
    }

    /**
     * 从 Content-Disposition 头中提取指定参数值。
     */
    private static String extractDispositionParam(String disposition, String param) {
        if (disposition == null) {
            return null;
        }
        String search = param + "=\"";
        int start = disposition.indexOf(search);
        if (start < 0) {
            // 尝试不带引号的形式
            search = param + "=";
            start = disposition.indexOf(search);
            if (start < 0) {
                return null;
            }
            start += search.length();
            int end = disposition.indexOf(';', start);
            return end < 0 ? disposition.substring(start).trim() : disposition.substring(start, end).trim();
        }
        start += search.length();
        int end = disposition.indexOf('"', start);
        return end < 0 ? disposition.substring(start) : disposition.substring(start, end);
    }

    /**
     * 格式化文件大小为可读字符串。
     */
    private static String formatSize(long bytes) {
        if (bytes < 0) {
            return "unknown";
        }
        if (bytes < 1024) {
            return bytes + "B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1fKB", bytes / 1024.0);
        }
        return String.format("%.1fMB", bytes / (1024.0 * 1024));
    }

    /**
     * 拼接方法名和 URL 到日志输出。
     * 当 logRequestUrl=true 时输出 [METHOD URL]，否则仅输出 [METHOD]。
     */
    private static void appendMethodAndUrl(StringBuilder sb, HttpConfig config, Request request) {
        if (config.getLogConfig().isLogRequestUrl()) {
            sb.append("[").append(request.method()).append(" ").append(request.url()).append("]");
        } else {
            sb.append("[").append(request.method()).append("]");
        }
    }
}
