package io.github.admin4j.http.util;

import io.github.admin4j.http.util.core.HttpDefaultConfig;
import io.github.admin4j.http.util.core.Pair;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link HttpUtil}.
 *
 * @author admin4j
 * @since 0.10.0
 */
public class HttpUtilTest {

    @Before
    public void setUp() {
        HttpDefaultConfig.get().setLogEnabled(true);
        HttpDefaultConfig.get().setLogRequestHeaders(true);
        HttpDefaultConfig.get().setLogResponseBody(true);
    }

    private static String bodyOf(Response response) {
        try (ResponseBody body = response.body()) {
            return body == null ? "" : body.string();
        } catch (IOException e) {
            fail("Failed to read response body: " + e.getMessage());
            return "";
        }
    }

    @org.junit.Test
    public void testGetWithPair() {
        Response resp = HttpUtil.get("https://httpbin.org/get",
                Pair.of("name", "test"),
                Pair.of("page", 1));
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        String body = bodyOf(resp);
        assertTrue(body.contains("name"));
        assertTrue(body.contains("test"));
        assertTrue(body.contains("page"));
    }

    @Test
    void testGetWithMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        params.put("num", 42);
        Response resp = HttpUtil.get("https://httpbin.org/get", params);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        String body = bodyOf(resp);
        assertTrue(body.contains("foo"));
        assertTrue(body.contains("bar"));
    }

    @Test
    void testPostJson() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "admin");
        payload.put("email", "admin@example.com");

        Response resp = HttpUtil.post("https://httpbin.org/post", payload);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        String body = bodyOf(resp);
        assertTrue(body.contains("username"));
        assertTrue(body.contains("admin"));
    }

    @Test
    void testPostJsonWithHeader() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("k", "v");
        Map<String, Object> header = new HashMap<>();
        header.put("X-Trace-Id", "trace-123");

        Response resp = HttpUtil.post("https://httpbin.org/post", payload, header);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        String body = bodyOf(resp);
        assertTrue(body.contains("X-Trace-Id"));
        assertTrue(body.contains("trace-123"));
    }

    @Test
    void testPostForm() {
        Map<String, Object> form = new HashMap<>();
        form.put("username", "admin");
        form.put("password", "secret");

        Response resp = HttpUtil.postForm("https://httpbin.org/post", form);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        String body = bodyOf(resp);
        assertTrue(body.contains("username"));
        assertTrue(body.contains("admin"));
        assertTrue(body.contains("password"));
    }

    @Test
    void testPostFormWithHeader() {
        Map<String, Object> form = new HashMap<>();
        form.put("a", "1");
        Map<String, Object> header = new HashMap<>();
        header.put("X-Form-Header", "yes");

        Response resp = HttpUtil.postForm("https://httpbin.org/post", form, header);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        String body = bodyOf(resp);
        assertTrue(body.contains("X-Form-Header"));
    }

    @Test
    void testPut() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "updated");

        Response resp = HttpUtil.put("https://httpbin.org/put", payload);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        String body = bodyOf(resp);
        assertTrue(body.contains("updated"));
    }

    @Test
    void testPutForm() {
        Map<String, Object> form = new HashMap<>();
        form.put("k", "v");
        Response resp = HttpUtil.putForm("https://httpbin.org/put", form);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
    }

    @Test
    void testDelete() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", 1);
        Response resp = HttpUtil.delete("https://httpbin.org/delete", payload);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
    }

    @Test
    void testDeleteForm() {
        Map<String, Object> form = new HashMap<>();
        form.put("id", "1");
        Response resp = HttpUtil.deleteForm("https://httpbin.org/delete", form);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
    }

    @Test
    void testPostFormData() {
        Map<String, Object> form = new HashMap<>();
        form.put("field1", "value1");
        Response resp = HttpUtil.postFormData("https://httpbin.org/post", form);
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        String body = bodyOf(resp);
        assertTrue(body.contains("field1"));
        assertTrue(body.contains("value1"));
    }

    @Test
    void testDownStream() {
        InputStream in = HttpUtil.down("https://httpbin.org/bytes/64");
        assertNotNull(in);
        try {
            byte[] buf = new byte[64];
            int read = in.read(buf);
            assertTrue(read > 0);
        } catch (IOException e) {
            fail("Failed to read download stream: " + e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }
}
