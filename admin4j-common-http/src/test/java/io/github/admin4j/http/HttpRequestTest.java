package io.github.admin4j.http;

import io.github.admin4j.http.exception.HttpException;
import io.github.admin4j.http.util.core.HttpHeaderKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
