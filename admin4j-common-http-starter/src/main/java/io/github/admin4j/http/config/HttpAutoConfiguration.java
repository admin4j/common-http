package io.github.admin4j.http.config;

import io.github.admin4j.http.Http;
import io.github.admin4j.http.HttpPropsConfiguration;
import io.github.admin4j.http.util.ApiClient;
import io.github.admin4j.http.util.ApiJsonClient;
import io.github.admin4j.http.util.HttpJsonUtil;
import io.github.admin4j.http.util.HttpUtil;
import io.github.admin4j.http.util.core.HttpDefaultConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @author andanyang
 * @since 2022/11/3 15:27
 */
@EnableConfigurationProperties(HttpPropsConfiguration.class)
@ConditionalOnProperty(prefix = "admin4j.http", name = "enabled", matchIfMissing = true)
public class HttpAutoConfiguration implements InitializingBean {

    HttpPropsConfiguration httpConfig;

    public HttpAutoConfiguration(HttpPropsConfiguration httpConfig) {
        this.httpConfig = httpConfig;
    }

    @PostConstruct
    public void initHttpClient() {

        HttpDefaultConfig.set(httpConfig);
        ApiClient apiClient = new ApiClient(httpConfig);
        HttpUtil.setClient(apiClient);
        ApiJsonClient apiJsonClient = new ApiJsonClient(httpConfig);
        HttpJsonUtil.setClient(apiJsonClient);

        // Initialize new unified API
        Http.setConfig(httpConfig);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        HttpDefaultConfig.set(httpConfig);
    }
}
