package io.github.admin4j.http;

import com.admin4j.json.JSONUtil;
import io.github.admin4j.http.exception.HttpException;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * HTTP 响应包装类，封装 OkHttp 的 {@link Response} 对象，提供便捷的响应处理方法。
 * <p>
 * 该类作为新 fluent API 中 {@code execute()} 方法的返回类型，
 * 支持将响应体转换为 String、byte[]、InputStream、POJO、List 和 Map 等多种格式。
 * </p>
 * <p>
 * 注意：OkHttp 的响应体只能被消费一次，本类会在首次读取时缓存响应体字符串，
 * 后续的 JSON 反序列化调用将复用缓存内容。
 * </p>
 *
 * @author admin4j
 * @since 2024/1/1
 */
public class HttpResponse {

    private final Response rawResponse;
    private String cachedBodyString;
    private byte[] cachedBodyBytes;
    private boolean bodyConsumed = false;

    /**
     * 构造 HttpResponse 实例。
     *
     * @param response OkHttp 原始响应对象，不能为 null
     */
    public HttpResponse(Response response) {
        if (response == null) {
            throw new IllegalArgumentException("response must not be null");
        }
        this.rawResponse = response;
    }

    /**
     * 获取响应体的字符串形式。
     * <p>首次调用时会读取并缓存响应体，后续调用返回缓存值。</p>
     *
     * @return 响应体字符串，如果响应体为空则返回 null
     * @throws HttpException 如果读取响应体时发生 IO 异常
     */
    public String asString() {
        ensureBodyCached();
        return cachedBodyString;
    }

    /**
     * 获取响应体的字节数组形式。
     *
     * @return 响应体字节数组，如果响应体为空则返回 null
     * @throws HttpException 如果读取响应体时发生 IO 异常
     */
    public byte[] asBytes() {
        ensureBytesCached();
        return cachedBodyBytes;
    }

    /**
     * 获取响应体的输入流形式。
     * <p>返回基于缓存字节数组的 {@link ByteArrayInputStream}，可多次调用。</p>
     *
     * @return 响应体输入流，如果响应体为空则返回 null
     * @throws HttpException 如果读取响应体时发生 IO 异常
     */
    public InputStream asStream() {
        ensureBytesCached();
        if (cachedBodyBytes == null) {
            return null;
        }
        return new ByteArrayInputStream(cachedBodyBytes);
    }

    /**
     * 将响应体 JSON 反序列化为指定类型的对象。
     *
     * @param clazz 目标类型
     * @param <T>   目标泛型类型
     * @return 反序列化后的对象
     * @throws HttpException 如果读取响应体或 JSON 解析失败
     */
    public <T> T asBean(Class<T> clazz) {
        String body = asString();
        if (body == null || body.isEmpty()) {
            return null;
        }
        try {
            return JSONUtil.parseObject(body, clazz);
        } catch (Exception e) {
            throw new HttpException("Failed to deserialize response body to " + clazz.getName(), e);
        }
    }

    /**
     * 将响应体 JSON 反序列化为指定类型的 List。
     *
     * @param clazz 列表元素的类型
     * @param <T>   列表元素泛型类型
     * @return 反序列化后的 List
     * @throws HttpException 如果读取响应体或 JSON 解析失败
     */
    public <T> List<T> asList(Class<T> clazz) {
        String body = asString();
        if (body == null || body.isEmpty()) {
            return null;
        }
        try {
            return JSONUtil.parseList(body, clazz);
        } catch (Exception e) {
            throw new HttpException("Failed to deserialize response body to List<" + clazz.getName() + ">", e);
        }
    }

    /**
     * 将响应体 JSON 反序列化为 Map。
     *
     * @return 反序列化后的 Map，key 为 String，value 为 Object
     * @throws HttpException 如果读取响应体或 JSON 解析失败
     */
    public Map<String, Object> asMap() {
        String body = asString();
        if (body == null || body.isEmpty()) {
            return null;
        }
        try {
            return JSONUtil.parseMap(body);
        } catch (Exception e) {
            throw new HttpException("Failed to deserialize response body to Map", e);
        }
    }

    /**
     * 获取 HTTP 状态码。
     *
     * @return HTTP 响应状态码
     */
    public int getCode() {
        return rawResponse.code();
    }

    /**
     * 获取响应头信息。
     *
     * @return 响应头 Map，key 为头名称，value 为对应的值列表
     */
    public Map<String, List<String>> getHeaders() {
        return rawResponse.headers().toMultimap();
    }

    /**
     * 判断响应是否成功（HTTP 状态码为 2xx）。
     *
     * @return 如果状态码在 200-299 范围内则返回 true
     */
    public boolean isSuccessful() {
        return rawResponse.isSuccessful();
    }

    /**
     * 获取底层的 OkHttp {@link Response} 对象。
     *
     * @return 原始 OkHttp Response
     */
    public Response getRaw() {
        return rawResponse;
    }

    /**
     * 关闭响应，释放资源。
     * 调用 response.body().string() 或 response.body().bytes() 等方法时，它们内部已经自动关闭了响应体。因此，在如下代码中不需要也不能再手动调用 response.body().close()，否则会引发 IOException：
     */
    public void close() {
        rawResponse.close();
    }

    /**
     * 确保响应体字符串已缓存。
     */
    private void ensureBodyCached() {
        if (bodyConsumed) {
            return;
        }
        bodyConsumed = true;
        ResponseBody body = rawResponse.body();
        if (body == null) {
            cachedBodyString = null;
            return;
        }
        try {
            cachedBodyString = body.string();
        } catch (IOException e) {
            throw new HttpException("Failed to read response body", e);
        }
    }

    /**
     * 确保响应体字节数组已缓存。
     */
    private void ensureBytesCached() {
        if (cachedBodyBytes != null) {
            return;
        }
        if (bodyConsumed) {
            // 字符串已缓存，从字符串转换
            if (cachedBodyString != null) {
                cachedBodyBytes = cachedBodyString.getBytes(StandardCharsets.UTF_8);
            }
            return;
        }
        // 尚未消费响应体，直接读取字节
        bodyConsumed = true;
        ResponseBody body = rawResponse.body();
        if (body == null) {
            cachedBodyBytes = null;
            cachedBodyString = null;
            return;
        }
        try {
            cachedBodyBytes = body.bytes();
            cachedBodyString = new String(cachedBodyBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new HttpException("Failed to read response body", e);
        }
    }
}
