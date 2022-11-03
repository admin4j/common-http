package io.github.admin4j.http.core;

/**
 * @author andanyang
 * @since 2022/11/3 16:32
 */
public class HttpDefaultConfig {

    private static HttpConfig DEFAULT_CONFIG = new HttpConfig();

    public static HttpConfig get() {
        return DEFAULT_CONFIG;
    }

    public static void set(HttpConfig httpConfig) {
        DEFAULT_CONFIG = httpConfig;
    }
}
