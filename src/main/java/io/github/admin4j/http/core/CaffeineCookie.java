package io.github.admin4j.http.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author andanyang
 * @since 2022/10/14 16:54
 */
public class CaffeineCookie implements CookieJar {

    private Cache<String, List<Cookie>> cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .maximumSize(1000)
            .build();

    @NotNull
    @Override

    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {

        String host = httpUrl.host();
        List<Cookie> cookies = cache.getIfPresent(host);
        return cookies != null ? cookies : Collections.emptyList();
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {

        String host = httpUrl.host();
        cache.put(host, list);
    }
}
