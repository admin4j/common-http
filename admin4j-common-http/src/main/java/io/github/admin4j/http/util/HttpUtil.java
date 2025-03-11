package io.github.admin4j.http.util;

import io.github.admin4j.http.ApiClient;
import io.github.admin4j.http.HttpRequest;
import io.github.admin4j.http.core.HttpDefaultConfig;
import io.github.admin4j.http.core.MediaTypeEnum;
import io.github.admin4j.http.core.Pair;
import lombok.Cleanup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Map;

/**
 * HTTP  返回body为json格式的请求工具类
 *
 * @author andanyang
 * @since 2022/5/10 14:31
 */
public class HttpUtil {
    /**
     * 单列
     */
    private static volatile ApiClient SINGLETON_REQUEST = null;

    private HttpUtil() {

    }

    public static ApiClient getClient() {
        if (null == SINGLETON_REQUEST) {

            synchronized (HttpUtil.class) {
                if (null == SINGLETON_REQUEST) {
                    SINGLETON_REQUEST = new ApiClient(HttpDefaultConfig.get());
                }
            }
        }
        return SINGLETON_REQUEST;
    }

    public static void setClient(ApiClient apiJsonClient) {
        SINGLETON_REQUEST = apiJsonClient;
    }

    /**
     * get 请求
     *
     * @param url
     * @param queryParams 查询参数
     * @return
     */
    public static Response get(String url, Pair<?>... queryParams) {

        return getClient().get(url, (Map<String, Object>) null, queryParams);
    }


    /**
     * get 请求
     *
     * @param url
     * @param queryParams 查询参数
     * @return
     */
    public static Response get(String url, Map<String, Object> queryParams) {

        return getClient().get(url, queryParams, (Pair<?>[]) null);
    }


    /**
     * post 请求
     *
     * @param url
     * @param body post body 体
     * @return
     */
    public static Response post(String url, Object body) {
        return getClient().post(url, MediaTypeEnum.JSON, body, (Map<String, Object>) null, (Map<String, Object>) null);
    }

    /**
     * post（application/json;） 请求
     *
     * @param url
     * @param body   post body 体
     * @param header header 头
     * @return
     */
    public static Response post(String url, Object body, Map<String, Object> header) {
        return getClient().post(url, MediaTypeEnum.JSON, body, (Map<String, Object>) null, header);
    }

    /**
     * form（x-www-form-urlencoded） 格式的 post 请求
     *
     * @param url
     * @param formParams
     * @return
     */
    public static Response postForm(String url, Map<String, Object> formParams) {
        return getClient().post(url, MediaTypeEnum.FORM, null, formParams, (Map<String, Object>) null);
    }

    /**
     * form（x-www-form-urlencoded） 格式的 post 请求
     *
     * @param url
     * @param formParams
     * @param header
     * @return
     */
    public static Response postForm(String url, Map<String, Object> formParams, Map<String, Object> header) {
        return getClient().post(url, MediaTypeEnum.FORM, null, formParams, header);
    }

    /**
     * put 请求
     *
     * @param url
     * @param body
     * @return
     */
    public static Response put(String url, Object body) {
        return getClient().put(url, MediaTypeEnum.JSON, body, (Map<String, Object>) null, (Map<String, Object>) null);
    }

    /**
     * put 请求
     *
     * @param url
     * @param body
     * @param header
     * @return
     */
    public static Response put(String url, Object body, Map<String, Object> header) {
        return getClient().put(url, MediaTypeEnum.JSON, body, (Map<String, Object>) null, header);
    }

    /**
     * form（x-www-form-urlencoded） 格式的 put 请求
     *
     * @param url
     * @param formParams
     * @return
     */
    public static Response putForm(String url, Map<String, Object> formParams) {
        return getClient().put(url, MediaTypeEnum.FORM, null, formParams, (Map<String, Object>) null);
    }

    /**
     * form（x-www-form-urlencoded） 格式的 put 请求
     *
     * @param url
     * @param formParams
     * @return
     */
    public static Response putForm(String url, Map<String, Object> formParams, Map<String, Object> header) {
        return getClient().put(url, MediaTypeEnum.FORM, null, formParams, header);
    }

    /**
     * delete 请求
     *
     * @param url
     * @param body
     * @return
     */
    public static Response delete(String url, Object body) {
        return getClient().delete(url, MediaTypeEnum.JSON, body, (Map<String, Object>) null, (Map<String, Object>) null);
    }

    /**
     * delete 请求
     *
     * @param url
     * @param body
     * @return
     */
    public static Response delete(String url, Object body, Map<String, Object> header) {
        return getClient().delete(url, MediaTypeEnum.JSON, body, (Map<String, Object>) null, header);
    }

    /**
     * form（x-www-form-urlencoded） 格式的 delete 请求
     *
     * @param url
     * @param formParams
     * @return
     */
    public static Response deleteForm(String url, Map<String, Object> formParams) {
        return getClient().delete(url, MediaTypeEnum.FORM, null, formParams, (Map<String, Object>) null);
    }

    /**
     * form（x-www-form-urlencoded） 格式的 delete 请求
     *
     * @param url
     * @param formParams
     * @return
     */
    public static Response deleteForm(String url, Map<String, Object> formParams, Map<String, Object> header) {
        return getClient().delete(url, MediaTypeEnum.FORM, null, formParams, header);
    }

    /**
     * form-data（multipart/form-data） 格式的 post 请求
     *
     * @param url
     * @param formParams
     * @return
     */
    public static Response postFormData(String url, Map<String, Object> formParams) {
        return getClient().post(url, MediaTypeEnum.FORM_DATA, null, formParams, (Map<String, Object>) null);
    }

    public static Response postFormData(String url, Map<String, Object> formParams, Map<String, Object> header) {
        return getClient().post(url, MediaTypeEnum.FORM_DATA, null, formParams, header);
    }


    /**
     * form-data（multipart/form-data） 格式的 post 请求
     *
     * @param url
     * @param formParams
     * @return
     */
    public static Response upload(String url, Map<String, Object> formParams) {
        return getClient().post(url, MediaTypeEnum.FORM_DATA, null, formParams, (Map<String, Object>) null);
    }

    public static Response upload(String url, Map<String, Object> formParams, Map<String, Object> header) {
        return getClient().post(url, MediaTypeEnum.FORM_DATA, null, formParams, header);
    }

    public static InputStream down(String url) {

        return getClient().down(url);
    }


    public static void down(String url, String savePath) throws IOException {
        InputStream in = down(url);

        File file = new File(savePath);

        boolean isFile = savePath.contains(".");
        if (isFile) {
            file.getParentFile().mkdirs();
        } else {
            file.mkdirs();
        }

        if (!isFile) {
            String fName = StringUtils.substringBefore(url, "?");
            fName = StringUtils.substringAfterLast(fName, "/");
            savePath = savePath + (StringUtils.endsWith(savePath, "/") ? "" : "/") + fName;
        }
        @Cleanup BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(savePath);
        byte[] b = new byte[1024];
        while ((bufferedInputStream.read(b)) != -1) {
            // 写入数据
            fileOutputStream.write(b);
        }
    }

    //============= 发送http 请求 =================
    public static Response send(HttpRequest httpRequest) {
        ApiClient apiClient = HttpUtil.getClient();
        Call call = apiClient.buildCall(httpRequest.getUrl(), httpRequest.getMethod(), httpRequest.getMediaType(),
                httpRequest.getQueryParams(), httpRequest.getQueryMap(), httpRequest.getBody(), httpRequest.getForm(),
                httpRequest.getHeaders());
        return apiClient.execute(call);
    }


    /**
     * 异步执行
     *
     * @param httpRequest
     * @param callback
     */
    public static void asyncExecute(HttpRequest httpRequest, Callback callback) {

        ApiClient apiClient = HttpUtil.getClient();
        Call call = apiClient.buildCall(httpRequest.getUrl(), httpRequest.getMethod(), httpRequest.getMediaType(),
                httpRequest.getQueryParams(), httpRequest.getQueryMap(), httpRequest.getBody(), httpRequest.getForm(),
                httpRequest.getHeaders());
        apiClient.executeAsync(call, callback);
    }
}
