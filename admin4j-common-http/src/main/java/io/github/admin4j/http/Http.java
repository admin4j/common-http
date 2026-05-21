package io.github.admin4j.http;

import io.github.admin4j.http.curl.CurlParser;
import io.github.admin4j.http.util.core.HttpConfig;
import io.github.admin4j.http.util.core.HttpDefaultConfig;
import io.github.admin4j.http.util.core.Method;

import java.util.function.Consumer;

/**
 * Unified HTTP client entry point. Provides a simple, fluent API for making HTTP requests.
 *
 * <h3>Usage Examples:</h3>
 * <pre>{@code
 * // Simple GET
 * HttpResponse resp = Http.get("https://api.example.com/users").execute();
 * String body = resp.asString();
 *
 * // GET with JSON parsing
 * User user = Http.get("https://api.example.com/user/1")
 *     .execute()
 *     .asBean(User.class);
 *
 * // POST JSON
 * Http.post("https://api.example.com/users")
 *     .body(newUser)
 *     .execute();
 *
 * // POST Form
 * Http.post("https://api.example.com/login")
 *     .form("username", "admin")
 *     .form("password", "secret")
 *     .execute();
 *
 * // File download
 * Http.get("https://example.com/file.zip").download("/tmp/file.zip");
 *
 * // Global configuration
 * Http.setConfig(config -> {
 *     config.setReadTimeout(60);
 *     config.setConnectTimeout(30);
 * });
 * }</pre>
 *
 * @author admin4j
 * @since 0.10.0
 */
public final class Http {

    private Http() {
        // utility class, no instantiation
    }

    // ======================== Factory Methods ========================

    /**
     * 创建 GET 请求。
     *
     * @param url 请求 URL
     * @return HttpRequest 构建器实例
     */
    public static HttpRequest get(String url) {
        return new HttpRequest(url, Method.GET);
    }

    /**
     * 创建 POST 请求。
     *
     * @param url 请求 URL
     * @return HttpRequest 构建器实例
     */
    public static HttpRequest post(String url) {
        return new HttpRequest(url, Method.POST);
    }

    /**
     * 创建 PUT 请求。
     *
     * @param url 请求 URL
     * @return HttpRequest 构建器实例
     */
    public static HttpRequest put(String url) {
        return new HttpRequest(url, Method.PUT);
    }

    /**
     * 创建 DELETE 请求。
     *
     * @param url 请求 URL
     * @return HttpRequest 构建器实例
     */
    public static HttpRequest delete(String url) {
        return new HttpRequest(url, Method.DELETE);
    }

    /**
     * 创建 PATCH 请求。
     *
     * @param url 请求 URL
     * @return HttpRequest 构建器实例
     */
    public static HttpRequest patch(String url) {
        return new HttpRequest(url, Method.PATCH);
    }

    /**
     * 创建 HEAD 请求。
     *
     * @param url 请求 URL
     * @return HttpRequest 构建器实例
     */
    public static HttpRequest head(String url) {
        return new HttpRequest(url, Method.HEAD);
    }

    /**
     * 创建指定 HTTP 方法的请求。
     *
     * @param url    请求 URL
     * @param method HTTP 方法
     * @return HttpRequest 构建器实例
     */
    public static HttpRequest request(String url, Method method) {
        return new HttpRequest(url, method);
    }

    /**
     * 由 curl 命令字符串构建 HttpRequest。
     * <p>支持的 curl 选项详见 {@link CurlParser}。</p>
     *
     * <pre>{@code
     * HttpResponse resp = Http.curl("curl -X POST 'https://api.example.com' " +
     *         "-H 'Content-Type: application/json' " +
     *         "-d '{\"name\":\"admin\"}'").execute();
     * }</pre>
     *
     * @param curlCommand curl 命令行字符串
     * @return HttpRequest 构建器实例
     */
    public static HttpRequest curl(String curlCommand) {
        return CurlParser.parse(curlCommand);
    }

    // ======================== Global Configuration ========================

    /**
     * 设置全局 HTTP 配置。
     *
     * @param config 配置对象
     */
    public static void setConfig(HttpConfig config) {
        HttpDefaultConfig.set(config);
    }

    /**
     * 以函数式风格设置全局 HTTP 配置。
     * <p>
     * 创建一个新的 {@link HttpConfig} 实例，应用配置逻辑后设为全局配置。
     * </p>
     *
     * @param configurer 配置消费者
     */
    public static void setConfig(Consumer<HttpConfig> configurer) {
        HttpConfig config = new HttpConfig();
        configurer.accept(config);
        HttpDefaultConfig.set(config);
    }

    /**
     * 获取当前全局 HTTP 配置。
     *
     * @return 当前配置对象
     */
    public static HttpConfig getConfig() {
        return HttpDefaultConfig.get();
    }
}
