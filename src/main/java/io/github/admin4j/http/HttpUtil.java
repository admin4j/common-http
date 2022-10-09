package io.github.admin4j.http;

import com.alibaba.fastjson.JSONObject;
import io.github.admin4j.http.core.Method;
import io.github.admin4j.http.core.Pair;
import lombok.Cleanup;
import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Map;
import java.util.Objects;

/**
 * @author andanyang
 * @since 2022/5/10 14:31
 */
public class HttpUtil {
    /**
     * 单列
     */
    private static volatile JSONHttpRequest SINGLETON_REQUEST = null;

    private HttpUtil() {

    }

    private static JSONHttpRequest getHttpRequest() {
        if (null == SINGLETON_REQUEST) {

            synchronized (HttpUtil.class) {
                if (null == SINGLETON_REQUEST) {
                    SINGLETON_REQUEST = new JSONHttpRequest(new HttpConfig());
                }
            }
        }
        return SINGLETON_REQUEST;
    }

    public static void okHttpClient(OkHttpClient okHttpClient) {
        getHttpRequest().setOkHttpClient(okHttpClient);
    }

    @SneakyThrows
    public static String getStr(String url, Pair<?>... queryParams) {

        return getHttpRequest().get(url, null, queryParams).body().string();
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

    public static InputStream down(String url) {
        JSONHttpRequest httpRequest = getHttpRequest();
        Call call = httpRequest.buildCall(url, Method.GET, null, null, null);
        Response response = httpRequest.execute(call);
        return Objects.requireNonNull(response.body()).byteStream();
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
            fileOutputStream.write(b);// 写入数据
        }
    }
}
