package io.github.admin4j.http.util;

import com.admin4j.json.mapper.JSONMapper;
import io.github.admin4j.http.util.core.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link HttpJsonUtil}.
 *
 * @author admin4j
 * @since 0.10.0
 */
class HttpJsonUtilTest {

    /**
     * httpbin /get / /post 等响应中常见字段：args / headers / url / json / form。
     */
    @Test
    void testGetWithPair() {
        JSONMapper json = HttpJsonUtil.get("https://httpbin.org/get",
                Pair.of("name", "test"),
                Pair.of("page", 1));
        assertNotNull(json);
        assertTrue(json.containsKey("args"));
        assertTrue(json.containsKey("url"));
        JSONMapper args = json.getJSONMapper("args");
        assertNotNull(args);
        assertEquals("test", args.getString("name"));
        assertEquals("1", args.getString("page"));
    }

    @Test
    void testGetWithMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        params.put("num", 42);

        JSONMapper json = HttpJsonUtil.get("https://httpbin.org/get", params);
        assertNotNull(json);
        JSONMapper args = json.getJSONMapper("args");
        assertNotNull(args);
        assertEquals("bar", args.getString("foo"));
        assertEquals("42", args.getString("num"));
    }

    @Test
    void testGetWithMapAndHeader() {
        Map<String, Object> params = new HashMap<>();
        params.put("k", "v");
        Map<String, Object> header = new HashMap<>();
        header.put("X-Trace-Id", "trace-1");

        JSONMapper json = HttpJsonUtil.get("https://httpbin.org/get", params, header);
        assertNotNull(json);
        JSONMapper headers = json.getJSONMapper("headers");
        assertNotNull(headers);
        assertEquals("trace-1", headers.getString("X-Trace-Id"));
    }

    @Test
    void testGetToBean() {
        Map<String, Object> params = new HashMap<>();
        params.put("a", "1");

        HttpBinGetResp resp = HttpJsonUtil.get("https://httpbin.org/get", HttpBinGetResp.class, params);
        assertNotNull(resp);
        assertNotNull(resp.getUrl());
        assertTrue(resp.getUrl().contains("httpbin.org/get"));
    }

    @Test
    void testPostJson() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "admin");
        payload.put("email", "admin@example.com");

        JSONMapper json = HttpJsonUtil.post("https://httpbin.org/post", payload);
        assertNotNull(json);
        // httpbin 返回的 json 字段会回显 JSON body
        JSONMapper echoed = json.getJSONMapper("json");
        assertNotNull(echoed);
        assertEquals("admin", echoed.getString("username"));
        assertEquals("admin@example.com", echoed.getString("email"));
    }

    @Test
    void testPostJsonWithHeader() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("k", "v");
        Map<String, Object> header = new HashMap<>();
        header.put("X-Custom", "custom-value");

        JSONMapper json = HttpJsonUtil.post("https://httpbin.org/post", payload, header);
        assertNotNull(json);
        JSONMapper headers = json.getJSONMapper("headers");
        assertNotNull(headers);
        assertEquals("custom-value", headers.getString("X-Custom"));
    }

    @Test
    void testPostJsonToBean() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "post-bean");

        HttpBinPostResp resp = HttpJsonUtil.post("https://httpbin.org/post", payload, HttpBinPostResp.class);
        assertNotNull(resp);
        assertNotNull(resp.getUrl());
        assertTrue(resp.getUrl().contains("httpbin.org/post"));
    }

    @Test
    void testPostForm() {
        Map<String, Object> form = new HashMap<>();
        form.put("username", "admin");
        form.put("password", "secret");

        JSONMapper json = HttpJsonUtil.postForm("https://httpbin.org/post", form);
        assertNotNull(json);
        JSONMapper formEcho = json.getJSONMapper("form");
        assertNotNull(formEcho);
        assertEquals("admin", formEcho.getString("username"));
        assertEquals("secret", formEcho.getString("password"));
    }

    @Test
    void testPut() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "updated");

        JSONMapper json = HttpJsonUtil.put("https://httpbin.org/put", payload);
        assertNotNull(json);
        JSONMapper echoed = json.getJSONMapper("json");
        assertNotNull(echoed);
        assertEquals("updated", echoed.getString("name"));
    }

    @Test
    void testPutForm() {
        Map<String, Object> form = new HashMap<>();
        form.put("k", "v");
        JSONMapper json = HttpJsonUtil.putForm("https://httpbin.org/put", form);
        assertNotNull(json);
        JSONMapper formEcho = json.getJSONMapper("form");
        assertNotNull(formEcho);
        assertEquals("v", formEcho.getString("k"));
    }

    @Test
    void testDelete() {
        Map<String, Object> form = new HashMap<>();
        form.put("id", "1");
        JSONMapper json = HttpJsonUtil.delete("https://httpbin.org/delete", null, form);
        assertNotNull(json);
        assertTrue(json.containsKey("url"));
    }

    /**
     * Bean 用于 GET 反序列化。
     */
    public static class HttpBinGetResp {
        private String url;
        private Map<String, Object> args;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, Object> getArgs() {
            return args;
        }

        public void setArgs(Map<String, Object> args) {
            this.args = args;
        }
    }

    /**
     * Bean 用于 POST 反序列化。
     */
    public static class HttpBinPostResp {
        private String url;
        private Map<String, Object> json;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, Object> getJson() {
            return json;
        }

        public void setJson(Map<String, Object> json) {
            this.json = json;
        }
    }
}
