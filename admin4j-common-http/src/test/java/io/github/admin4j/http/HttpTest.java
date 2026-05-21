package io.github.admin4j.http;

import io.github.admin4j.http.util.core.HttpConfig;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the new fluent HTTP API ({@link Http}, {@link HttpRequest}, {@link HttpResponse}).
 *
 * @author admin4j
 * @since 0.10.0
 */
class HttpTest {

    @Test
    void testGet() {
        // Basic GET request to a public URL
        HttpResponse resp = Http.get("https://httpbin.org/get").execute();
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        assertEquals(200, resp.getCode());
        assertNotNull(resp.asString());
    }

    @Test
    void testGetWithQueryParams() {
        // GET with query parameters
        HttpResponse resp = Http.get("https://httpbin.org/get")
                .query("name", "test")
                .query("page", 1)
                .execute();
        assertTrue(resp.isSuccessful());
        String body = resp.asString();
        assertNotNull(body);
        assertTrue(body.contains("name"));
        assertTrue(body.contains("test"));
        assertTrue(body.contains("page"));
    }

    @Test
    void testGetWithQueryMap() {
        // GET with batch query parameters via queryMap
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        params.put("num", 42);
        HttpResponse resp = Http.get("https://httpbin.org/get")
                .queryMap(params)
                .execute();
        assertTrue(resp.isSuccessful());
        String body = resp.asString();
        assertTrue(body.contains("foo"));
        assertTrue(body.contains("bar"));
    }

    @Test
    void testPostJson() {
        // POST with JSON body
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "admin");
        payload.put("email", "admin@example.com");

        HttpResponse resp = Http.post("https://httpbin.org/post")
                .body(payload)
                .execute();
        assertTrue(resp.isSuccessful());
        assertEquals(200, resp.getCode());
        String body = resp.asString();
        assertNotNull(body);
        assertTrue(body.contains("username"));
        assertTrue(body.contains("admin"));
    }

    @Test
    void testPostForm() {
        // POST with form data (application/x-www-form-urlencoded)
        HttpResponse resp = Http.post("https://httpbin.org/post")
                .form("username", "admin")
                .form("password", "secret123")
                .execute();
        assertTrue(resp.isSuccessful());
        String body = resp.asString();
        assertNotNull(body);
        assertTrue(body.contains("username"));
        assertTrue(body.contains("admin"));
        assertTrue(body.contains("password"));
    }

    @Test
    void testPostFormMap() {
        // POST with batch form data via formMap
        Map<String, Object> formData = new HashMap<>();
        formData.put("key1", "value1");
        formData.put("key2", "value2");

        HttpResponse resp = Http.post("https://httpbin.org/post")
                .formMap(formData)
                .execute();
        assertTrue(resp.isSuccessful());
        String body = resp.asString();
        assertTrue(body.contains("key1"));
        assertTrue(body.contains("value1"));
    }

    @Test
    void testHeaders() {
        // Test custom headers
        HttpResponse resp = Http.get("https://httpbin.org/headers")
                .header("X-Custom-Header", "CustomValue")
                .header("X-Request-Id", "12345")
                .execute();
        assertTrue(resp.isSuccessful());
        String body = resp.asString();
        assertNotNull(body);
        assertTrue(body.contains("X-Custom-Header"));
        assertTrue(body.contains("CustomValue"));
        assertTrue(body.contains("X-Request-Id"));
        assertTrue(body.contains("12345"));
    }

    @Test
    void testJsonDeserialization() {
        // Test asMap() for JSON deserialization
        HttpResponse resp = Http.get("https://httpbin.org/get")
                .query("test", "value")
                .execute();
        assertTrue(resp.isSuccessful());

        Map<String, Object> map = resp.asMap();
        assertNotNull(map);
        // httpbin.org/get returns a JSON with "args", "headers", "url" etc.
        assertTrue(map.containsKey("args"));
        assertTrue(map.containsKey("url"));
    }

    @Test
    void testResponseHeaders() {
        // Test getHeaders() on response
        HttpResponse resp = Http.get("https://httpbin.org/get").execute();
        assertTrue(resp.isSuccessful());

        Map<String, ?> headers = resp.getHeaders();
        assertNotNull(headers);
        assertFalse(headers.isEmpty());
        // httpbin always returns content-type header
        assertTrue(headers.containsKey("content-type"));
    }

    @Test
    void testDownload() {
        // Test download() returning InputStream
        InputStream is = Http.get("https://httpbin.org/get").download();
        assertNotNull(is);
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = is.read(buffer);
            assertTrue(bytesRead > 0);
        } catch (Exception e) {
            fail("Failed to read from download stream: " + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Test
    void testGlobalConfig() {
        // Save original config
        HttpConfig originalConfig = Http.getConfig();

        // Test Http.setConfig() with Consumer
        Http.setConfig(config -> {
            config.setReadTimeout(60);
            config.setConnectTimeout(15);
            config.setUserAgent("TestAgent/1.0");
        });

        HttpConfig currentConfig = Http.getConfig();
        assertNotNull(currentConfig);
        assertEquals(60, currentConfig.getReadTimeout());
        assertEquals(15, currentConfig.getConnectTimeout());
        assertEquals("TestAgent/1.0", currentConfig.getUserAgent());

        // Test Http.setConfig() with direct config object
        HttpConfig newConfig = new HttpConfig();
        newConfig.setReadTimeout(45);
        newConfig.setConnectTimeout(20);
        Http.setConfig(newConfig);

        assertEquals(45, Http.getConfig().getReadTimeout());
        assertEquals(20, Http.getConfig().getConnectTimeout());

        // Restore original config
        Http.setConfig(originalConfig);
    }

    @Test
    void testPerRequestTimeout() {
        // Test timeout() on individual request - use a reasonable timeout
        HttpResponse resp = Http.get("https://httpbin.org/get")
                .timeout(30)
                .connectTimeout(10)
                .execute();
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        assertEquals(200, resp.getCode());
    }

    @Test
    void testPutRequest() {
        // Test PUT method
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "updated");

        HttpResponse resp = Http.put("https://httpbin.org/put")
                .body(payload)
                .execute();
        assertTrue(resp.isSuccessful());
        assertEquals(200, resp.getCode());
        String body = resp.asString();
        assertTrue(body.contains("updated"));
    }

    @Test
    void testDeleteRequest() {
        // Test DELETE method
        HttpResponse resp = Http.delete("https://httpbin.org/delete").execute();
        assertTrue(resp.isSuccessful());
        assertEquals(200, resp.getCode());
    }

    @Test
    void testUserAgent() {
        // Test userAgent() convenience method
        HttpResponse resp = Http.get("https://httpbin.org/user-agent")
                .userAgent("MyCustomAgent/2.0")
                .execute();
        assertTrue(resp.isSuccessful());
        String body = resp.asString();
        assertNotNull(body);
        assertTrue(body.contains("MyCustomAgent/2.0"));
    }

    @Test
    void testAsBytes() {
        // Test asBytes() on response
        HttpResponse resp = Http.get("https://httpbin.org/get").execute();
        assertTrue(resp.isSuccessful());
        byte[] bytes = resp.asBytes();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void testAsStream() {
        // Test asStream() on response
        HttpResponse resp = Http.get("https://httpbin.org/get").execute();
        assertTrue(resp.isSuccessful());
        InputStream stream = resp.asStream();
        assertNotNull(stream);
    }
}
