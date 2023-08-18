package io.github.admin4j.http;

import io.github.admin4j.http.core.HttpConfig;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

/**
 * @author andanyang
 * @since 2023/8/17 15:19
 */
class ApiClientTest {

    @Test
    void get() {
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.setLogName("SubApiClient.ApiClientTest");
        SubApiClient client = new SubApiClient(httpConfig);
        Response response = client.get("https://www.baidu.com");
    }
}