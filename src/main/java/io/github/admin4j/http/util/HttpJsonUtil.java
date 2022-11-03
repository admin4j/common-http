package io.github.admin4j.http.util;

import com.alibaba.fastjson.JSONObject;
import io.github.admin4j.http.ApiJsonClient;
import io.github.admin4j.http.core.HttpConfig;
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

    private static ApiJsonClient getHttpRequest() {
        if (null == SINGLETON_REQUEST) {

            synchronized (HttpJsonUtil.class) {
                if (null == SINGLETON_REQUEST) {
                    SINGLETON_REQUEST = new ApiJsonClient(new HttpConfig());
                }
            }
        }
        return SINGLETON_REQUEST;
    }

    public static JSONObject get(String url, Pair<?>... queryParams) {

        return getHttpRequest().get(url, queryParams);
    }

    public static <T> T get(String url, Class<T> tClass, Pair<?>... queryParams) {

        return get(url, queryParams).toJavaObject(tClass);
    }

    public static JSONObject get(String url, Map<String, Object> queryParams) {

        return getHttpRequest().get(url, queryParams);
    }

    public static <T> T get(String url, Class<T> tClass, Map<String, Object> queryParams) {

        return get(url, queryParams).toJavaObject(tClass);
    }


    public static JSONObject post(String url, Object body) {
        return getHttpRequest().post(url, body);
    }

    public static <T> T post(String url, Object body, Class<T> tClass) {
        return post(url, body).toJavaObject(tClass);
    }

    public static JSONObject postForm(String url, Map<String, Object> formParams) {
        return getHttpRequest().postForm(url, formParams);
    }

    public static <T> T postForm(String url, Map<String, Object> formParams, Class<T> tClass) {
        return postForm(url, formParams).toJavaObject(tClass);
    }


    public static JSONObject upload(String url, Map<String, Object> formParams) {
        return getHttpRequest().postFormData(url, formParams);
    }

}
