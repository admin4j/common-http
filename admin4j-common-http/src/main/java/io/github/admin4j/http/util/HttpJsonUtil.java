package io.github.admin4j.http.util;

import com.admin4j.json.mapper.JSONMapper;
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

    public static JSONMapper get(String url, Pair<?>... queryParams) {

        return getHttpRequest().get(url, queryParams);
    }

    public static <T> T get(String url, Class<T> tClass, Pair<?>... queryParams) {

        return getHttpRequest().get(url, tClass, queryParams);
    }

    public static JSONMapper get(String url, Map<String, Object> queryParams) {

        return getHttpRequest().get(url, queryParams);
    }

    public static JSONMapper get(String url, Map<String, Object> queryParams, Map<String, Object> headerMap) {

        return getHttpRequest().get(url, queryParams,headerMap);
    }

    public static <T> T get(String url, Class<T> tClass, Map<String, Object> queryParams) {

        return getHttpRequest().get(url, queryParams, tClass);
    }

    /**
     * 发送GET 请求
     * @param url 地址
     * @param tClass 需要JSON解析的model
     * @param queryParams   查询参数
     * @param headerMap      header 参数
     * @return      返回结果 model
     * @param <T>
     */
    public static <T> T get(String url, Class<T> tClass, Map<String, Object> queryParams, Map<String, Object> headerMap) {

        return getHttpRequest().get(url, queryParams,headerMap, tClass);
    }


    public static JSONMapper post(String url, Object body) {
        return getHttpRequest().post(url, body);
    }

    public static JSONMapper post(String url, Object body, Map<String, Object> headerParams) {
        return getHttpRequest().post(url, body, headerParams);
    }

    public static <T> T post(String url, Object body, Class<T> tClass) {
        return getHttpRequest().post(url, body, tClass);
    }

    public static <T> T post(String url, Object body, Map<String, Object> headerParams, Class<T> tClass) {
        return getHttpRequest().post(url, body, headerParams, tClass);
    }

    public static JSONMapper postForm(String url, Map<String, Object> formParams) {
        return getHttpRequest().postForm(url, formParams);
    }

    public static JSONMapper postForm(String url, Map<String, Object> formParams, Map<String, Object> headerParams) {
        return getHttpRequest().postForm(url, formParams, headerParams);
    }

    public static <T> T postForm(String url, Map<String, Object> formParams, Class<T> tClass) {
        return getHttpRequest().postForm(url, formParams, tClass);
    }

    public static <T> T postForm(String url, Map<String, Object> formParams, Map<String, Object> headerParams, Class<T> tClass) {
        return getHttpRequest().postForm(url, formParams, headerParams, tClass);
    }

    //===================  Put =================
    public static JSONMapper put(String url, Object body) {
        return getHttpRequest().put(url, body);
    }

    public static JSONMapper put(String url, Object body, Map<String, Object> headerParams) {
        return getHttpRequest().put(url, body, headerParams);
    }

    public static <T> T put(String url, Object body, Class<T> tClass) {
        return getHttpRequest().put(url, body, tClass);
    }

    public static <T> T put(String url, Object body, Map<String, Object> headerParams, Class<T> tClass) {
        return getHttpRequest().put(url, body, headerParams, tClass);
    }

    public static JSONMapper putForm(String url, Map<String, Object> formParams) {
        return getHttpRequest().putForm(url, formParams);
    }

    public static JSONMapper putForm(String url, Map<String, Object> formParams, Map<String, Object> headerParams) {
        return getHttpRequest().putForm(url, formParams, headerParams);
    }

    public static <T> T putForm(String url, Map<String, Object> formParams, Class<T> tClass) {
        return getHttpRequest().putForm(url, formParams, tClass);
    }

    public static <T> T putForm(String url, Map<String, Object> formParams, Map<String, Object> headerParams, Class<T> tClass) {
        return getHttpRequest().putForm(url, formParams, headerParams, tClass);
    }


    public static JSONMapper upload(String url, Map<String, Object> formParams) {
        return getHttpRequest().postFormData(url, formParams);
    }

    public static JSONMapper upload(String url, Map<String, Object> formParams, Map<String, Object> headerParams) {
        return getHttpRequest().postFormData(url, formParams, headerParams);
    }

    /**
     * 使用 put 方法上传
     *
     * @param url
     * @param formParams
     * @return
     */
    public static JSONMapper uploadPut(String url, Map<String, Object> formParams) {
        return getHttpRequest().putFormData(url, formParams);
    }

    public static JSONMapper uploadPut(String url, Map<String, Object> formParams, Map<String, Object> headerParams) {
        return getHttpRequest().putFormData(url, formParams, headerParams);
    }

    // ============= delete =======

    /**
     * 删除
     *
     * @param url        url
     * @param body       如果不为空 以 JSON 格式发送
     * @param formParams 如果不为空 以 form 格式发送
     * @return JSONMapper
     */
    public static JSONMapper delete(String url,
                                    Object body,
                                    Map<String, Object> formParams) {
        return getHttpRequest().delete(url, body, formParams);
    }

    public static JSONMapper delete(String url,
                                    Object body,
                                    Map<String, Object> formParams, Map<String, Object> headerParams) {
        return getHttpRequest().delete(url, body, formParams, headerParams);
    }

    /**
     * 删除
     *
     * @param url        url
     * @param body       如果不为空 以 JSON 格式发送
     * @param formParams 如果不为空 以 form 格式发送
     * @return class
     */
    public static <T> T delete(String url,
                               Object body,
                               Map<String, Object> formParams, Class<T> tClass) {
        return getHttpRequest().delete(url, body, formParams, tClass);
    }

    public static <T> T delete(String url,
                               Object body,
                               Map<String, Object> formParams, Map<String, Object> headerParams, Class<T> tClass) {
        return getHttpRequest().delete(url, body, formParams, headerParams, tClass);
    }

}
