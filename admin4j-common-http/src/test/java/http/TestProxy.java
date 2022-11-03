package http;

import com.alibaba.fastjson.JSONObject;
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
        proxyConfig.setHost("119.8.115.201");
        proxyConfig.setPort(9898);
        proxyConfig.setUserName("ces");
        proxyConfig.setPassword("iIl1o0O");
        httpConfig.setProxy(proxyConfig);

        OkHttpClient okHttpClient = HttpClientFactory.okHttpClient(httpConfig);

        ApiJsonClient apiClient = new ApiJsonClient(httpConfig);
        apiClient.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
        //JSONObject jsonObject = apiClient.get("https://2022.ip138.com/");

        Map<String, Object> formParams = new HashMap<>(16);
        formParams.put("username", "admin");
        formParams.put("password", "admin123");
        JSONObject object = apiClient.postForm("http://127.0.0.1:30443/auth/login",
                formParams
        );
        System.out.println("post = " + object);
    }

}
