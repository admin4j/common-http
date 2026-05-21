package io.github.admin4j.http;

import io.github.admin4j.http.util.core.HttpConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 迁移到新 {@link Http} 流式 API 的 ApiClient 风格示例。
 *
 * @author andanyang
 * @since 2023/8/17 15:19
 */
class ApiClientTest {

    @Test
    void get() {
        // 全局配置（替代旧 ApiClient 构造方式）
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.setLogName("SubApiClient.ApiClientTest");
        Http.setConfig(httpConfig);

        HttpResponse resp = Http.get("https://www.baidu.com").execute();
        assertNotNull(resp);
        assertTrue(resp.isSuccessful());
        assertNotNull(resp.asString());
    }
}
