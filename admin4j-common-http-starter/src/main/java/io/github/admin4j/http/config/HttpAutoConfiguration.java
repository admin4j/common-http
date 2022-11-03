package io.github.admin4j.http.config;

import io.github.admin4j.http.ApiClient;
import io.github.admin4j.http.ApiJsonClient;
import io.github.admin4j.http.HttpPropsConfiguration;
import io.github.admin4j.http.core.HttpDefaultConfig;
import io.github.admin4j.http.util.HttpJsonUtil;
import io.github.admin4j.http.util.HttpUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @author andanyang
 * @since 2022/11/3 15:27
 */
@EnableConfigurationProperties(HttpPropsConfiguration.class)
public class HttpAutoConfiguration implements InitializingBean {
    @Autowired
    HttpPropsConfiguration httpConfig;

    @PostConstruct
    public void initHttpClient() {

        ApiClient apiClient = new ApiClient(httpConfig);
        HttpUtil.setClient(apiClient);
        ApiJsonClient apiJsonClient = new ApiJsonClient(httpConfig);
        HttpJsonUtil.setClient(apiJsonClient);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        HttpDefaultConfig.set(httpConfig);
    }
}
