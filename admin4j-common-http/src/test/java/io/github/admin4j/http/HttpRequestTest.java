package io.github.admin4j.http;

import io.github.admin4j.http.exception.HttpException;
import io.github.admin4j.http.util.core.HttpConfig;
import io.github.admin4j.http.util.core.HttpHeaderKey;
import io.github.admin4j.http.util.core.HttpLogConfig;
import okhttp3.Protocol;
import org.junit.jupiter.api.Test;

import java.net.Proxy;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 迁移到新 {@link Http} 流式 API 的 HttpRequest 风格示例。
 *
 * @author andanyang
 * @since 2022/11/8 16:23
 */
class HttpRequestTest {

    @Test
    void get() {
        HttpResponse resp = Http.get("https://search.gitee.com/?skin=rec&type=repository")
                .query("q", "admin4j")
                .header(HttpHeaderKey.USER_AGENT, "admin4j")
                .execute();
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
    }

    @Test
    void post() {
        HttpResponse resp = Http.post("https://uatservice-etax.one.th/etaxbatchweb/etaxsignbatch")
                .query("q", "admin4j")
                .header(HttpHeaderKey.USER_AGENT, "admin4j")
                .form("username", "admin")
                .form("password", "admin123")
                .execute();
        assertNotNull(resp);
    }

    @Test
    void postError() {
        // 模拟 http 异常
        try {
            HttpResponse resp = Http.post("https://uatservice-etax.one.th/etaxbatchweb/etaxsignbatc")
                    .query("q", "admin4j")
                    .header(HttpHeaderKey.USER_AGENT, "admin4j")
                    .form("username", "admin")
                    .form("password", "admin123")
                    .execute();
            System.out.println("response code = " + resp.getCode());
        } catch (HttpException e) {
            System.out.println("e = " + e.getMessage());
        }
    }

    // ======================== copy/clone 单元测试 ========================

    @Test
    void testHttpLogConfigCopy() {
        HttpLogConfig original = new HttpLogConfig();
        original.setLogEnabled(true);
        original.setLogRequestHeaders(true);
        original.setLogRequestUrl(false);
        original.setLogRequestParams(false);
        original.setLogResponseBody(false);
        original.setLogResponseHeaders(true);
        original.setLogOnFailure(false);

        HttpLogConfig copy = original.copy();

        // 验证所有字段正确拷贝
        assertTrue(copy.isLogEnabled());
        assertTrue(copy.isLogRequestHeaders());
        assertFalse(copy.isLogRequestUrl());
        assertFalse(copy.isLogRequestParams());
        assertFalse(copy.isLogResponseBody());
        assertTrue(copy.isLogResponseHeaders());
        assertFalse(copy.isLogOnFailure());

        // 验证独立性：修改 copy 不影响 original
        copy.setLogEnabled(false);
        assertTrue(original.isLogEnabled());
    }

    @Test
    void testHttpLogConfigCopyClone() {
        HttpLogConfig original = new HttpLogConfig();
        original.setLogEnabled(true);

        HttpLogConfig copied = original.copy();

        assertTrue(copied.isLogEnabled());
        assertNotSame(original, copied);
    }

    @Test
    void testHttpConfigCopy() {
        HttpConfig original = new HttpConfig();
        original.setLogName("test-log");
        original.setReadTimeout(60);
        original.setConnectTimeout(15);
        original.setUserAgent("TestAgent");
        original.setCookie(true);
        original.setProtocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2));

        // 设置 logConfig
        original.getLogConfig().setLogEnabled(true);
        original.getLogConfig().setLogRequestHeaders(true);

        // 设置 proxy
        HttpConfig.HttpProxyConfig proxy = new HttpConfig.HttpProxyConfig();
        proxy.setHost("127.0.0.1");
        proxy.setPort(8080);
        proxy.setUserName("user");
        proxy.setPassword("pass");
        original.setProxy(proxy);

        HttpConfig copy = original.copy();

        // 验证基本字段
        assertEquals("test-log", copy.getLogName());
        assertEquals(60, copy.getReadTimeout());
        assertEquals(15, copy.getConnectTimeout());
        assertEquals("TestAgent", copy.getUserAgent());
        assertTrue(copy.isCookie());

        // 验证 logConfig 深拷贝
        assertTrue(copy.getLogConfig().isLogEnabled());
        assertTrue(copy.getLogConfig().isLogRequestHeaders());
        assertNotSame(original.getLogConfig(), copy.getLogConfig());
        copy.getLogConfig().setLogEnabled(false);
        assertTrue(original.getLogConfig().isLogEnabled());

        // 验证 proxy 深拷贝
        assertNotNull(copy.getProxy());
        assertEquals("127.0.0.1", copy.getProxy().getHost());
        assertEquals(8080, copy.getProxy().getPort());
        assertEquals("user", copy.getProxy().getUserName());
        assertEquals("pass", copy.getProxy().getPassword());
        assertNotSame(original.getProxy(), copy.getProxy());
        copy.getProxy().setHost("192.168.1.1");
        assertEquals("127.0.0.1", original.getProxy().getHost());

        // 验证 protocols 深拷贝
        assertNotNull(copy.getProtocols());
        assertEquals(2, copy.getProtocols().size());
        assertNotSame(original.getProtocols(), copy.getProtocols());
    }

    @Test
    void testHttpConfigCopyIndependence() {
        HttpConfig original = new HttpConfig();
        original.setLogName("copy-test");

        HttpConfig copied = original.copy();

        assertEquals("copy-test", copied.getLogName());
        assertNotSame(original, copied);
    }

    @Test
    void testHttpConfigCopyWithNullFields() {
        HttpConfig original = new HttpConfig();
        original.setProxy(null);
        original.setProtocols(null);
        original.setLogConfig(null);

        HttpConfig copy = original.copy();

        assertNull(copy.getProxy());
        assertNull(copy.getProtocols());
        assertNull(copy.getLogConfig());
    }

    @Test
    void testHttpProxyConfigCopy() {
        HttpConfig.HttpProxyConfig original = new HttpConfig.HttpProxyConfig();
        original.setType(Proxy.Type.SOCKS);
        original.setHost("proxy.example.com");
        original.setPort(1080);
        original.setUserName("admin");
        original.setPassword("secret");

        HttpConfig.HttpProxyConfig copy = original.copy();

        assertEquals(Proxy.Type.SOCKS, copy.getType());
        assertEquals("proxy.example.com", copy.getHost());
        assertEquals(1080, copy.getPort());
        assertEquals("admin", copy.getUserName());
        assertEquals("secret", copy.getPassword());

        // 验证独立性
        copy.setHost("other.com");
        assertEquals("proxy.example.com", original.getHost());
    }
}
