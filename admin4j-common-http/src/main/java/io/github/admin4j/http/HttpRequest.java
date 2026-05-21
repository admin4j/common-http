package io.github.admin4j.http;

import com.admin4j.json.JSONUtil;
import io.github.admin4j.http.curl.CurlParser;
import io.github.admin4j.http.exception.HttpException;
import io.github.admin4j.http.util.core.*;
import okhttp3.*;
import okhttp3.internal.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 增强型 Fluent HTTP 请求构建器，统一原始请求和 JSON 能力。
 * <p>
 * 该类为新简化 API 的核心入口，通过链式调用构建 HTTP 请求并执行。
 * 支持 query 参数、自定义 header、JSON body、表单、multipart 文件上传、
 * 按请求级别的超时配置以及同步/异步执行。
 * </p>
 *
 * <pre>{@code
 * // GET 请求示例
 * HttpResponse resp = HttpRequest.get("https://api.example.com/users")
 *     .query("page", 1)
 *     .header("Authorization", "Bearer token")
 *     .execute();
 *
 * // POST JSON 请求示例
 * HttpResponse resp = HttpRequest.post("https://api.example.com/users")
 *     .body(userObj)
 *     .timeout(10)
 *     .execute();
 *
 * // 文件上传示例
 * HttpResponse resp = HttpRequest.post("https://api.example.com/upload")
 *     .multipart("file", new File("/path/to/file.png"))
 *     .multipart("description", "avatar image")
 *     .execute();
 * }</pre>
 *
 * @author admin4j
 * @since 2024/1/1
 */
public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final String url;
    private final Method method;
    private final Map<String, Object> queryParams = new LinkedHashMap<>();
    private final Map<String, Object> headerParams = new LinkedHashMap<>();
    private final Map<String, Object> formParams = new LinkedHashMap<>();
    private final Map<String, Object> multipartParams = new LinkedHashMap<>();
    private Object body;
    private MediaTypeEnum mediaType;
    private long readTimeoutSeconds = -1;
    private long connectTimeoutSeconds = -1;

    /**
     * 包内构造器，通过 URL 和 HTTP 方法创建请求构建器。
     *
     * @param url    请求 URL
     * @param method HTTP 方法
     */
    HttpRequest(String url, Method method) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url must not be null or empty");
        }
        if (method == null) {
            throw new IllegalArgumentException("method must not be null");
        }
        this.url = url;
        this.method = method;
    }

    // ======================== Static Factory Methods ========================

    /**
     * 创建 GET 请求构建器。
     *
     * @param url 请求 URL
     * @return HttpRequest 实例
     */
    public static HttpRequest get(String url) {
        return new HttpRequest(url, Method.GET);
    }

    /**
     * 创建 POST 请求构建器。
     *
     * @param url 请求 URL
     * @return HttpRequest 实例
     */
    public static HttpRequest post(String url) {
        return new HttpRequest(url, Method.POST);
    }

    /**
     * 创建 PUT 请求构建器。
     *
     * @param url 请求 URL
     * @return HttpRequest 实例
     */
    public static HttpRequest put(String url) {
        return new HttpRequest(url, Method.PUT);
    }

    /**
     * 创建 DELETE 请求构建器。
     *
     * @param url 请求 URL
     * @return HttpRequest 实例
     */
    public static HttpRequest delete(String url) {
        return new HttpRequest(url, Method.DELETE);
    }

    /**
     * 创建 PATCH 请求构建器。
     *
     * @param url 请求 URL
     * @return HttpRequest 实例
     */
    public static HttpRequest patch(String url) {
        return new HttpRequest(url, Method.PATCH);
    }

    /**
     * 创建 HEAD 请求构建器。
     *
     * @param url 请求 URL
     * @return HttpRequest 实例
     */
    public static HttpRequest head(String url) {
        return new HttpRequest(url, Method.HEAD);
    }

    /**
     * 创建指定方法的请求构建器。
     *
     * @param url    请求 URL
     * @param method HTTP 方法
     * @return HttpRequest 实例
     */
    public static HttpRequest of(String url, Method method) {
        return new HttpRequest(url, method);
    }

    /**
     * 由 curl 命令字符串构建 HttpRequest。
     * <p>支持的 curl 选项详见 {@link CurlParser}。</p>
     *
     * @param curlCommand curl 命令行字符串
     * @return HttpRequest 实例
     */
    public static HttpRequest fromCurl(String curlCommand) {
        return CurlParser.parse(curlCommand);
    }

    // ======================== Query Parameters ========================

    /**
     * 添加查询参数。
     *
     * @param key   参数名
     * @param value 参数值
     * @return this
     */
    public HttpRequest query(String key, Object value) {
        if (key != null && value != null) {
            queryParams.put(key, value);
        }
        return this;
    }

    /**
     * 批量添加查询参数。
     *
     * @param params 参数 Map
     * @return this
     */
    public HttpRequest queryMap(Map<String, Object> params) {
        if (params != null) {
            queryParams.putAll(params);
        }
        return this;
    }

    // ======================== Headers ========================

    /**
     * 添加请求头。
     *
     * @param key   头名称
     * @param value 头值
     * @return this
     */
    public HttpRequest header(String key, String value) {
        if (key != null && value != null) {
            headerParams.put(key, value);
        }
        return this;
    }

    /**
     * 批量添加请求头。
     *
     * @param headers 头 Map
     * @return this
     */
    public HttpRequest headers(Map<String, Object> headers) {
        if (headers != null) {
            headerParams.putAll(headers);
        }
        return this;
    }

    // ======================== Body ========================

    /**
     * 设置 JSON 请求体。
     * <p>
     * 对象将通过 {@code JSONUtil.toJSONString()} 序列化为 JSON 字符串。
     * 如果未显式设置 mediaType，将自动使用 {@link MediaTypeEnum#JSON}。
     * </p>
     *
     * @param body 请求体对象
     * @return this
     */
    public HttpRequest body(Object body) {
        this.body = body;
        return this;
    }

    // ======================== Form Fields ========================

    /**
     * 添加表单字段（application/x-www-form-urlencoded）。
     *
     * @param key   字段名
     * @param value 字段值
     * @return this
     */
    public HttpRequest form(String key, Object value) {
        if (key != null && value != null) {
            formParams.put(key, value);
        }
        return this;
    }

    /**
     * 批量添加表单字段。
     *
     * @param params 字段 Map
     * @return this
     */
    public HttpRequest formMap(Map<String, Object> params) {
        if (params != null) {
            formParams.putAll(params);
        }
        return this;
    }

    // ======================== Multipart Fields ========================

    /**
     * 添加 multipart 字段（支持 File、byte[]、String）。
     *
     * @param key   字段名
     * @param value 字段值（支持 File、byte[]、String 类型）
     * @return this
     */
    public HttpRequest multipart(String key, Object value) {
        if (key != null && value != null) {
            multipartParams.put(key, value);
        }
        return this;
    }

    /**
     * 批量添加 multipart 字段。
     *
     * @param params 字段 Map
     * @return this
     */
    public HttpRequest multipartMap(Map<String, Object> params) {
        if (params != null) {
            multipartParams.putAll(params);
        }
        return this;
    }

    // ======================== Configuration ========================

    /**
     * 显式设置请求体的 MediaType。
     *
     * @param mediaType MediaType 枚举值
     * @return this
     */
    public HttpRequest mediaType(MediaTypeEnum mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    /**
     * 设置本次请求的读取超时时间。
     *
     * @param seconds 超时秒数
     * @return this
     */
    public HttpRequest timeout(long seconds) {
        this.readTimeoutSeconds = seconds;
        return this;
    }

    /**
     * 设置本次请求的连接超时时间。
     *
     * @param seconds 超时秒数
     * @return this
     */
    public HttpRequest connectTimeout(long seconds) {
        this.connectTimeoutSeconds = seconds;
        return this;
    }

    /**
     * 设置本次请求的 User-Agent。
     *
     * @param ua User-Agent 字符串
     * @return this
     */
    public HttpRequest userAgent(String ua) {
        if (ua != null) {
            headerParams.put(HttpHeaderKey.USER_AGENT, ua);
        }
        return this;
    }

    /**
     * 设置本次请求的 Referer。
     *
     * @param ref Referer 字符串
     * @return this
     */
    public HttpRequest referer(String ref) {
        if (ref != null) {
            headerParams.put(HttpHeaderKey.REFERER, ref);
        }
        return this;
    }

    // ======================== Execution ========================

    /**
     * 同步执行请求并返回 {@link HttpResponse}。
     *
     * @return 响应封装对象
     * @throws HttpException 如果请求执行过程中发生异常
     */
    public HttpResponse execute() {
        try {
            Call call = buildCall();
            Response response = call.execute();
            return new HttpResponse(response);
        } catch (IOException e) {
            throw new HttpException("Failed to execute HTTP request: " + e.getMessage(), e);
        }
    }

    /**
     * 执行请求并返回响应体的 InputStream，用于流式下载。
     * <p>
     * 注意：调用者需负责关闭返回的 InputStream。
     * </p>
     *
     * @return 响应体输入流
     * @throws HttpException 如果请求失败或响应状态非 2xx
     */
    public InputStream download() {
        try {
            Call call = buildCall();
            Response response = call.execute();
            if (!response.isSuccessful()) {
                ResponseBody errorBody = response.body();
                String errorMsg = errorBody != null ? errorBody.string() : "unknown error";
                response.close();
                throw new HttpException("Download failed with status " + response.code() + ": " + errorMsg);
            }
            ResponseBody body = response.body();
            if (body == null) {
                response.close();
                return null;
            }
            return body.byteStream();
        } catch (IOException e) {
            throw new HttpException("Failed to download: " + e.getMessage(), e);
        }
    }

    /**
     * 执行请求并将响应体保存到指定文件路径。
     *
     * @param path 目标文件路径
     * @throws IOException   如果文件写入失败
     * @throws HttpException 如果请求执行失败或响应状态非 2xx
     */
    public void download(String path) throws IOException {
        try (InputStream is = download()) {
            if (is == null) {
                throw new IOException("Response body is empty, nothing to download");
            }
            Files.copy(is, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 异步执行请求，通过 Consumer 回调处理 {@link HttpResponse}。
     * <p>
     * 失败时仅记录错误日志，不会抛出异常。如需自定义错误处理，
     * 请使用 {@link #executeAsync(Consumer, Consumer)} 重载方法。
     * </p>
     *
     * @param consumer 响应消费者回调
     */
    public void executeAsync(Consumer<HttpResponse> consumer) {
        executeAsync(consumer, e -> log.error("Async HTTP request failed", e));
    }

    /**
     * Execute request asynchronously with success and error handlers.
     *
     * @param onSuccess called on successful response
     * @param onError   called on failure (network error, timeout, etc.)
     */
    public void executeAsync(Consumer<HttpResponse> onSuccess, Consumer<HttpException> onError) {
        Call call = buildCall();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError.accept(new HttpException("Async request failed: " + e.getMessage(), e));
            }

            @Override
            public void onResponse(Call call, Response response) {
                HttpResponse httpResponse = new HttpResponse(response);
                onSuccess.accept(httpResponse);
            }
        });
    }

    /**
     * 异步执行请求，使用 OkHttp 原生 Callback。
     *
     * @param callback OkHttp Callback
     * @throws HttpException 如果构建请求时发生异常
     */
    public void executeAsync(Callback callback) {
        Call call = buildCall();
        call.enqueue(callback);
    }

    // ======================== Internal Helpers ========================

    /**
     * 构建 OkHttp Call 对象。
     */
    private Call buildCall() {
        OkHttpClient client = getClient();
        Request request = buildOkHttpRequest();
        return client.newCall(request);
    }

    /**
     * 获取 OkHttpClient 实例，如有自定义超时则基于默认 client 派生。
     */
    private OkHttpClient getClient() {
        OkHttpClient defaultClient = HttpDefaultConfig.getClient();
        if (readTimeoutSeconds < 0 && connectTimeoutSeconds < 0) {
            return defaultClient;
        }
        OkHttpClient.Builder builder = defaultClient.newBuilder();
        if (readTimeoutSeconds >= 0) {
            builder.readTimeout(readTimeoutSeconds, TimeUnit.SECONDS);
        }
        if (connectTimeoutSeconds >= 0) {
            builder.connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS);
        }
        return builder.build();
    }

    /**
     * 构建完整的 OkHttp Request 对象。
     */
    private Request buildOkHttpRequest() {
        String fullUrl = buildFullUrl();
        Request.Builder reqBuilder = new Request.Builder().url(fullUrl);

        // 设置默认 headers (User-Agent, Referer)
        HttpConfig defaultConfig = HttpDefaultConfig.get();
        if (!headerParams.containsKey(HttpHeaderKey.USER_AGENT)) {
            reqBuilder.header(HttpHeaderKey.USER_AGENT, defaultConfig.getUserAgent());
        }
        if (!headerParams.containsKey(HttpHeaderKey.REFERER)
                && defaultConfig.getReferer() != null
                && !defaultConfig.getReferer().isEmpty()) {
            reqBuilder.header(HttpHeaderKey.REFERER, defaultConfig.getReferer());
        }

        // 设置自定义 headers
        for (Map.Entry<String, Object> entry : headerParams.entrySet()) {
            reqBuilder.header(entry.getKey(), parameterToString(entry.getValue()));
        }

        // GET/HEAD 不允许 body
        if (!HttpMethod.permitsRequestBody(method.name())) {
            return reqBuilder.method(method.name(), null).build();
        }

        // 构建 RequestBody
        MediaTypeEnum resolvedType = resolveMediaType();
        RequestBody reqBody = buildRequestBody(resolvedType);

        return reqBuilder.method(method.name(), reqBody).build();
    }

    /**
     * 构建完整的 URL（包含 query 参数）。
     */
    private String buildFullUrl() {
        if (queryParams.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        String prefix = url.contains("?") ? "&" : "?";
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            if (entry.getValue() != null) {
                if (prefix != null) {
                    sb.append(prefix);
                    prefix = null;
                } else {
                    sb.append("&");
                }
                sb.append(encodeUrl(entry.getKey()))
                        .append("=")
                        .append(encodeUrl(parameterToString(entry.getValue())));
            }
        }
        return sb.toString();
    }

    /**
     * 推断请求的 MediaType。
     */
    private MediaTypeEnum resolveMediaType() {
        if (mediaType != null) {
            return mediaType;
        }
        if (!multipartParams.isEmpty()) {
            return MediaTypeEnum.FORM_DATA;
        }
        if (!formParams.isEmpty()) {
            return MediaTypeEnum.FORM;
        }
        return MediaTypeEnum.JSON;
    }

    /**
     * 根据 MediaType 构建 RequestBody。
     */
    private RequestBody buildRequestBody(MediaTypeEnum resolvedType) {
        if (MediaTypeEnum.FORM_DATA.equals(resolvedType)) {
            return buildMultipartBody();
        }
        if (MediaTypeEnum.FORM.equals(resolvedType)) {
            return buildFormBody();
        }
        // JSON or other types
        return buildJsonBody(resolvedType);
    }

    /**
     * 构建 multipart 请求体。
     */
    private RequestBody buildMultipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MediaTypeEnum.FORM_DATA.getMediaType());

        for (Map.Entry<String, Object> entry : multipartParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof File) {
                File file = (File) value;
                MediaType fileMediaType = guessContentType(file);
                Headers partHeaders = Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"" + key + "\"; filename=\"" + file.getName() + "\""
                );
                builder.addPart(partHeaders, RequestBody.create(fileMediaType, file));
            } else if (value instanceof byte[]) {
                Headers partHeaders = Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"" + key + "\"; filename=\"" + key + "\""
                );
                builder.addPart(partHeaders, RequestBody.create(MediaTypeEnum.OCTET_STREAM.getMediaType(), (byte[]) value));
            } else {
                Headers partHeaders = Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"" + key + "\""
                );
                builder.addPart(partHeaders, RequestBody.create(null, parameterToString(value)));
            }
        }
        return builder.build();
    }

    /**
     * 构建 form-urlencoded 请求体。
     */
    private RequestBody buildFormBody() {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : formParams.entrySet()) {
            builder.add(entry.getKey(), parameterToString(entry.getValue()));
        }
        return builder.build();
    }

    /**
     * 构建 JSON 或其他类型的请求体。
     */
    private RequestBody buildJsonBody(MediaTypeEnum resolvedType) {
        okhttp3.MediaType okMediaType = resolvedType.getMediaType();
        if (body == null) {
            if (Method.DELETE.equals(method)) {
                return null;
            }
            return RequestBody.create(okMediaType, "");
        }
        if (body instanceof byte[]) {
            return RequestBody.create(okMediaType, (byte[]) body);
        }
        if (body instanceof File) {
            return RequestBody.create(okMediaType, (File) body);
        }
        if (body instanceof String) {
            return RequestBody.create(okMediaType, (String) body);
        }
        String json = JSONUtil.toJSONString(body);
        return RequestBody.create(okMediaType, json);
    }

    /**
     * 猜测文件的 Content-Type。
     */
    private MediaType guessContentType(File file) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        if (contentType == null) {
            return MediaTypeEnum.OCTET_STREAM.getMediaType();
        }
        return MediaType.parse(contentType);
    }

    /**
     * URL 编码。
     */
    private String encodeUrl(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    /**
     * 将参数对象转换为字符串。
     */
    private String parameterToString(Object param) {
        if (param == null) {
            return "";
        }
        if (param instanceof Date) {
            String jsonStr = JSONUtil.toJSONString(param);
            return jsonStr.substring(1, jsonStr.length() - 1);
        }
        if (param instanceof Collection) {
            StringBuilder b = new StringBuilder();
            for (Object o : (Collection<?>) param) {
                if (b.length() > 0) {
                    b.append(",");
                }
                b.append(o);
            }
            return b.toString();
        }
        if (param instanceof String) {
            return (String) param;
        }
        return String.valueOf(param);
    }
}
