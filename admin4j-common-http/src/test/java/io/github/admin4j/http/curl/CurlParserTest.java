package io.github.admin4j.http.curl;

import io.github.admin4j.http.HttpRequest;
import io.github.admin4j.http.util.core.MediaTypeEnum;
import io.github.admin4j.http.util.core.Method;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Offline tests for {@link CurlParser}: validate tokenization and option parsing
 * without performing real network calls.
 */
class CurlParserTest {

    // ==================== Tokenizer ====================

    @Test
    void tokenize_basic() {
        List<String> tokens = CurlParser.tokenize("curl -X POST https://example.com");
        assertEquals(4, tokens.size());
        assertEquals("curl", tokens.get(0));
        assertEquals("-X", tokens.get(1));
        assertEquals("POST", tokens.get(2));
        assertEquals("https://example.com", tokens.get(3));
    }

    @Test
    void tokenize_singleQuotes() {
        List<String> tokens = CurlParser.tokenize("curl -H 'Content-Type: application/json'");
        assertEquals(3, tokens.size());
        assertEquals("Content-Type: application/json", tokens.get(2));
    }

    @Test
    void tokenize_doubleQuotesWithEscape() {
        List<String> tokens = CurlParser.tokenize("curl -d \"{\\\"a\\\":1}\"");
        assertEquals(3, tokens.size());
        assertEquals("{\"a\":1}", tokens.get(2));
    }

    @Test
    void tokenize_lineContinuation() {
        String cmd = "curl -X POST \\\n  -H 'X: Y' \\\n  https://example.com";
        List<String> tokens = CurlParser.tokenize(cmd);
        assertEquals(6, tokens.size());
        assertEquals("POST", tokens.get(2));
        assertEquals("-H", tokens.get(3));
        assertEquals("X: Y", tokens.get(4));
        assertEquals("https://example.com", tokens.get(5));
    }

    // ==================== Parsing ====================

    @Test
    void parse_simpleGet() throws Exception {
        HttpRequest req = CurlParser.parse("curl https://example.com/api");
        assertEquals(Method.GET, getField(req, "method"));
        assertEquals("https://example.com/api", getField(req, "url"));
    }

    @Test
    void parse_methodOverride() throws Exception {
        HttpRequest req = CurlParser.parse("curl -X DELETE https://example.com");
        assertEquals(Method.DELETE, getField(req, "method"));
    }

    @Test
    void parse_methodInferredFromData() throws Exception {
        HttpRequest req = CurlParser.parse("curl https://example.com -d 'a=1'");
        assertEquals(Method.POST, getField(req, "method"));
    }

    @Test
    void parse_headersAndAuth() throws Exception {
        HttpRequest req = CurlParser.parse(
                "curl -u admin:secret -H 'X-Token: abc' -A 'MyAgent/1.0' -e 'https://ref' " +
                        "-b 'sid=1' https://example.com");
        Map<String, Object> headers = getField(req, "headerParams");
        assertEquals("abc", headers.get("X-Token"));
        assertEquals("MyAgent/1.0", headers.get("User-Agent"));
        assertEquals("https://ref", headers.get("Referer"));
        assertEquals("sid=1", headers.get("Cookie"));
        // basic auth header: Basic base64(admin:secret)
        String auth = (String) headers.get("Authorization");
        assertNotNull(auth);
        assertTrue(auth.startsWith("Basic "));
        assertEquals("Basic YWRtaW46c2VjcmV0", auth);
    }

    @Test
    void parse_jsonBody() throws Exception {
        HttpRequest req = CurlParser.parse(
                "curl -X POST https://example.com " +
                        "-H 'Content-Type: application/json' " +
                        "-d '{\"name\":\"admin\"}'");
        assertEquals(Method.POST, getField(req, "method"));
        assertEquals("{\"name\":\"admin\"}", getField(req, "body"));
        assertEquals(MediaTypeEnum.JSON, getField(req, "mediaType"));
    }

    @Test
    void parse_jsonBodyHeuristic() throws Exception {
        // 没有显式 Content-Type，但 body 形如 JSON
        HttpRequest req = CurlParser.parse("curl -X POST https://example.com -d '{\"a\":1}'");
        assertEquals(MediaTypeEnum.JSON, getField(req, "mediaType"));
    }

    @Test
    void parse_formMultipleData() throws Exception {
        HttpRequest req = CurlParser.parse(
                "curl -X POST https://example.com -d 'a=1' -d 'b=2'");
        assertEquals("a=1&b=2", getField(req, "body"));
        assertEquals(MediaTypeEnum.FORM, getField(req, "mediaType"));
    }

    @Test
    void parse_getModeMovesDataToQuery() throws Exception {
        HttpRequest req = CurlParser.parse("curl -G https://example.com -d 'q=hello' -d 'page=1'");
        assertEquals(Method.GET, getField(req, "method"));
        Map<String, Object> q = getField(req, "queryParams");
        assertEquals("hello", q.get("q"));
        assertEquals("1", q.get("page"));
    }

    @Test
    void parse_multipartForm() throws Exception {
        HttpRequest req = CurlParser.parse(
                "curl -X POST https://example.com -F 'name=admin' -F 'avatar=@/tmp/a.png'");
        Map<String, Object> mp = getField(req, "multipartParams");
        assertEquals("admin", mp.get("name"));
        // file 引用使用 java.io.File
        assertTrue(mp.get("avatar") instanceof java.io.File);
        assertEquals("a.png", ((java.io.File) mp.get("avatar")).getName());
    }

    @Test
    void parse_dataUrlencode() throws Exception {
        HttpRequest req = CurlParser.parse(
                "curl -X POST https://example.com --data-urlencode 'q=hello world&x'");
        // 'hello world&x' 被 URL 编码后为 hello%20world%26x（+ 替换为 %20 的形式视实现而定，URLEncoder 默认是 +）
        String body = (String) getField(req, "body");
        assertTrue(body.startsWith("q="));
        assertTrue(body.contains("hello"));
        assertTrue(body.contains("%26") || body.contains("&x") == false);
    }

    @Test
    void parse_urlOption() throws Exception {
        HttpRequest req = CurlParser.parse("curl --url https://example.com/x");
        assertEquals("https://example.com/x", getField(req, "url"));
    }

    @Test
    void parse_emptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> CurlParser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> CurlParser.parse(null));
    }

    @Test
    void parse_missingUrlThrows() {
        assertThrows(IllegalArgumentException.class, () -> CurlParser.parse("curl -X GET"));
    }

    @Test
    void parse_unknownOptionsIgnored() throws Exception {
        // -L/--compressed/-s 等无副作用的标记应被忽略
        HttpRequest req = CurlParser.parse("curl -L --compressed -s -k https://example.com");
        assertEquals("https://example.com", getField(req, "url"));
    }

    // ==================== Reflection helper ====================

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object target, String name) throws Exception {
        Field f = HttpRequest.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }
}
