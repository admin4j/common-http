package http;

import com.admin4j.json.mapper.JSONMapper;
import io.github.admin4j.http.ApiJsonClient;
import io.github.admin4j.http.core.HttpConfig;
import io.github.admin4j.http.factory.HttpClientFactory;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/11/2 17:11
 */

public class TestProxy {

    @Test
    void getStr() throws IOException {
        HttpConfig httpConfig = new HttpConfig();

        HttpConfig.ProxyConfig proxyConfig = new HttpConfig.ProxyConfig();
        proxyConfig.setHost("192.168.0.1");
        proxyConfig.setPort(9898);
        proxyConfig.setUserName("ces_order");
        proxyConfig.setPassword("23451Order");
        httpConfig.setProxy(proxyConfig);

        OkHttpClient okHttpClient = HttpClientFactory.okHttpClient(httpConfig);

        ApiJsonClient apiClient = new ApiJsonClient(httpConfig);
        //    apiClient.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
        // JSONObject jsonObject = apiClient.get("https://2022.ip138.com/");

        Map<String, Object> formParams = new HashMap<>(16);
        formParams.put("username", "admin");
        formParams.put("password", "admin123");
        JSONMapper object = apiClient.get("https://api.onbuy.com/v2/auth/request-token",
                formParams
        );
        System.out.println("post = " + object);
    }

}
