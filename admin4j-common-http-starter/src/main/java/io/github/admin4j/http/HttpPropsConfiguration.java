package io.github.admin4j.http;

import io.github.admin4j.http.util.core.HttpConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author andanyang
 * @since 2022/11/3 16:14
 */
@ConfigurationProperties(prefix = "admin4j.http")
public class HttpPropsConfiguration extends HttpConfig {
    /**
     * 是否启用
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public HttpPropsConfiguration copy() {
        HttpPropsConfiguration copy = (HttpPropsConfiguration) super.copy();
        copy.setEnabled(this.enabled);
        return copy;
    }
}
