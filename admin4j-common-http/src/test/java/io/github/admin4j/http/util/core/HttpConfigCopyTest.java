package io.github.admin4j.http.util.core;

import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Test;

import java.net.Proxy;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpConfig 拷贝功能测试
 */
class HttpConfigCopyTest {

    @Test
    void testCopyBasicFields() {
        HttpConfig config = new HttpConfig();
        config.setLoggLevel(HttpLoggingInterceptor.Level.HEADERS);
        config.setLogName("test-log");
        config.setReadTimeout(60);
        config.setConnectTimeout(45);
        config.setFollowRedirects(true);
        config.setMaxIdleConnections(10);
        config.setKeepAliveDuration(30);
        config.setUserAgent("TestAgent");
        config.setReferer("https://example.com");
        config.setCookie(true);
        config.setCommonConnectionPool(false);

        HttpConfig copy = config.copy();

        assertNotSame(config, copy);
        assertEquals(HttpLoggingInterceptor.Level.HEADERS, copy.getLoggLevel());
        assertEquals("test-log", copy.getLogName());
        assertEquals(60, copy.getReadTimeout());
        assertEquals(45, copy.getConnectTimeout());
        assertTrue(copy.isFollowRedirects());
        assertEquals(10, copy.getMaxIdleConnections());
        assertEquals(30, copy.getKeepAliveDuration());
        assertEquals("TestAgent", copy.getUserAgent());
        assertEquals("https://example.com", copy.getReferer());
        assertTrue(copy.isCookie());
        assertFalse(copy.isCommonConnectionPool());
    }

    @Test
    void testCopyLogConfigDeepCopy() {
        HttpConfig config = new HttpConfig();
        config.getLogConfig().setLogEnabled(true);
        config.getLogConfig().setLogRequestHeaders(true);
        config.getLogConfig().setLogResponseBody(false);

        HttpConfig copy = config.copy();

        // Verify deep copy - values are copied
        assertTrue(copy.getLogConfig().isLogEnabled());
        assertTrue(copy.getLogConfig().isLogRequestHeaders());
        assertFalse(copy.getLogConfig().isLogResponseBody());

        // Modify copy's logConfig should not affect original
        copy.getLogConfig().setLogEnabled(false);
        copy.getLogConfig().setLogRequestHeaders(false);
        assertTrue(config.getLogConfig().isLogEnabled());
        assertTrue(config.getLogConfig().isLogRequestHeaders());
    }

    @Test
    void testCopyProxyDeepCopy() {
        HttpConfig config = new HttpConfig();
        HttpConfig.HttpProxyConfig proxyConfig = new HttpConfig.HttpProxyConfig();
        proxyConfig.setType(Proxy.Type.SOCKS);
        proxyConfig.setHost("proxy.example.com");
        proxyConfig.setPort(8080);
        proxyConfig.setUserName("user");
        proxyConfig.setPassword("pass");
        config.setProxy(proxyConfig);

        HttpConfig copy = config.copy();

        // Verify deep copy
        assertNotNull(copy.getProxy());
        assertEquals(Proxy.Type.SOCKS, copy.getProxy().getType());
        assertEquals("proxy.example.com", copy.getProxy().getHost());
        assertEquals(8080, copy.getProxy().getPort());

        // Modify copy's proxy should not affect original
        copy.getProxy().setHost("other.proxy.com");
        copy.getProxy().setPort(9090);
        assertEquals("proxy.example.com", config.getProxy().getHost());
        assertEquals(8080, config.getProxy().getPort());
    }

    @Test
    void testCopyProtocolsDeepCopy() {
        HttpConfig config = new HttpConfig();
        List<Protocol> protocols = Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2);
        config.setProtocols(protocols);

        HttpConfig copy = config.copy();

        // Verify list is copied
        assertNotNull(copy.getProtocols());
        assertEquals(2, copy.getProtocols().size());
        assertEquals(Protocol.HTTP_1_1, copy.getProtocols().get(0));

        // Modify copy's list should not affect original
        copy.getProtocols().clear();
        assertEquals(2, config.getProtocols().size());
    }

    @Test
    void testCopyWithNullProxy() {
        HttpConfig config = new HttpConfig();
        config.setProxy(null);

        HttpConfig copy = config.copy();

        assertNull(copy.getProxy());
    }

    @Test
    void testCopyWithNullProtocols() {
        HttpConfig config = new HttpConfig();
        config.setProtocols(null);

        HttpConfig copy = config.copy();

        assertNull(copy.getProtocols());
    }

    @Test
    void testCopyReturnsIndependentObject() {
        HttpConfig config = new HttpConfig();
        config.setReadTimeout(100);

        HttpConfig copied = config.copy();

        assertNotSame(config, copied);
        copied.setReadTimeout(200);
        assertEquals(100, config.getReadTimeout());
        assertEquals(200, copied.getReadTimeout());
    }

    @Test
    void testCopyDeepCopyLogConfig() {
        HttpConfig config = new HttpConfig();
        config.getLogConfig().setLogEnabled(true);
        config.getLogConfig().setLogRequestHeaders(true);

        HttpConfig copied = config.copy();

        // Verify values are copied
        assertTrue(copied.getLogConfig().isLogEnabled());
        assertTrue(copied.getLogConfig().isLogRequestHeaders());

        // Modify copied's logConfig should not affect original
        copied.getLogConfig().setLogEnabled(false);
        copied.getLogConfig().setLogRequestHeaders(false);
        assertTrue(config.getLogConfig().isLogEnabled());
        assertTrue(config.getLogConfig().isLogRequestHeaders());
    }

    @Test
    void testLogConfigCopy() {
        HttpLogConfig logConfig = new HttpLogConfig();
        logConfig.setLogEnabled(true);
        logConfig.setLogRequestHeaders(true);
        logConfig.setLogRequestUrl(false);
        logConfig.setLogRequestParams(false);
        logConfig.setLogResponseBody(false);
        logConfig.setLogResponseHeaders(true);
        logConfig.setLogOnFailure(false);

        HttpLogConfig copy = logConfig.copy();

        assertNotSame(logConfig, copy);
        assertTrue(copy.isLogEnabled());
        assertTrue(copy.isLogRequestHeaders());
        assertFalse(copy.isLogRequestUrl());
        assertFalse(copy.isLogRequestParams());
        assertFalse(copy.isLogResponseBody());
        assertTrue(copy.isLogResponseHeaders());
        assertFalse(copy.isLogOnFailure());

        // Modify copy should not affect original
        copy.setLogEnabled(false);
        assertTrue(logConfig.isLogEnabled());

    }

    @Test
    void testHttpProxyConfigCopy() {
        HttpConfig.HttpProxyConfig proxy = new HttpConfig.HttpProxyConfig();
        proxy.setType(Proxy.Type.SOCKS);
        proxy.setHost("proxy.test.com");
        proxy.setPort(3128);
        proxy.setUserName("admin");
        proxy.setPassword("secret");

        HttpConfig.HttpProxyConfig copy = proxy.copy();

        assertNotSame(proxy, copy);
        assertEquals(Proxy.Type.SOCKS, copy.getType());
        assertEquals("proxy.test.com", copy.getHost());
        assertEquals(3128, copy.getPort());
        assertEquals("admin", copy.getUserName());
        assertEquals("secret", copy.getPassword());

        // Modify copy should not affect original
        copy.setHost("other.com");
        assertEquals("proxy.test.com", proxy.getHost());

    }
}
