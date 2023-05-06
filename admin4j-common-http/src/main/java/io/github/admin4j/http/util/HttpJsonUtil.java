package io.github.admin4j.http.util;

import io.github.admin4j.http.ApiJsonClient;
import io.github.admin4j.http.core.HttpDefaultConfig;
import io.github.admin4j.http.core.Pair;

import java.util.Map;

/**
 * 返回body为json格式的请求工具类
 *
 * @author andanyang
 * @since 2022/5/10 14:31
 */
public class HttpJsonUtil {
    /**
     * 单列
     */
    private static volatile ApiJsonClient SINGLETON_REQUEST = null;

    private HttpJsonUtil() {

    }

    public static void setClient(ApiJsonClient apiJsonClient) {
        SINGLETON_REQUEST = apiJsonClient;
    }

    private static ApiJsonClient getHttpRequest() {
        if (null == SINGLETON_REQUEST) {

            synchronized (HttpJsonUtil.class) {
                if (null == SINGLETON_REQUEST) {
                    SINGLETON_REQUEST = new ApiJsonClient(HttpDefaultConfig.get());
                }
            }
        }
        return SINGLETON_REQUEST;
    }

    public static Map<String, Object> get(String url, Pair<?>... queryParams) {

        return getHttpRequest().get(url, queryParams);
    }

    public static <T> T get(String url, Class<T> tClass, Pair<?>... queryParams) {

        return getHttpRequest().get(url, tClass, queryParams);
    }

    public static Map<String, Object> get(String url, Map<String, Object> queryParams) {

        return getHttpRequest().get(url, queryParams);
    }

    public static <T> T get(String url, Class<T> tClass, Map<String, Object> queryParams) {

        return getHttpRequest().get(url, queryParams, tClass);
    }


    public static Map<String, Object> post(String url, Object body) {
        return getHttpRequest().post(url, body);
    }

    public static <T> T post(String url, Object body, Class<T> tClass) {
        return getHttpRequest().post(url, body, tClass);
    }

    public static Map<String, Object> postForm(String url, Map<String, Object> formParams) {
        return getHttpRequest().postForm(url, formParams);
    }

    public static <T> T postForm(String url, Map<String, Object> formParams, Class<T> tClass) {
        return getHttpRequest().postForm(url, formParams, tClass);
    }


    public static Map<String, Object> upload(String url, Map<String, Object> formParams) {
        return getHttpRequest().postFormData(url, formParams);
    }

}
