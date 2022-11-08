package io.github.admin4j.http;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author andanyang
 * @since 2022/11/3 16:14
 */
@ConfigurationProperties(prefix = "admin4j.http")
public class HttpPropsConfiguration extends io.github.admin4j.http.core.HttpConfig {
    /**
     * 是否启用
     */
    private boolean enabled = true;
}
