package io.github.admin4j.http;

import com.alibaba.fastjson.JSON;
import io.github.admin4j.http.core.Method;
import io.github.admin4j.http.core.Pair;
import lombok.Cleanup;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
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
    private static volatile HttpRequest SINGLETON_REQUEST = null;

    private HttpUtil() {

    }

    public static HttpRequest getHttpRequest() {
        if (null == SINGLETON_REQUEST) {

            synchronized (HttpUtil.class) {
                if (null == SINGLETON_REQUEST) {
                    SINGLETON_REQUEST = new HttpRequest();
                }
            }
        }
        return SINGLETON_REQUEST;
    }

    public static void setHttpRequest(HttpRequest httpRequest) {
        SINGLETON_REQUEST = httpRequest;
    }

    public static String get(String path, Pair<?>... queryParams) throws IOException {

        Call call = getHttpRequest().buildCall(path, Method.GET, queryParams, null, null, null, null);
        ResponseBody body = call.execute().body();

        assert body != null;
        return body.string();
    }

    public static <T> T get(String path, Class<T> tClass, Pair<?>... queryParams) throws IOException {

        Call call = getHttpRequest().buildCall(path, Method.GET, queryParams, null, null, null, null);
        ResponseBody body = call.execute().body();

        return JSON.parseObject(body.byteStream(), tClass);
    }

    public static String get(String path, Map<String, Object> queryParams) throws IOException {

        Call call = getHttpRequest().buildCall(path, Method.GET, null, queryParams, null, null, null);
        ResponseBody body = call.execute().body();

        assert body != null;
        return body.string();
    }


    public static HttpRequestBuilder postBuilder(String url) {
        return new HttpRequestBuilder(url, Method.POST);
    }

    public static HttpRequestBuilder getBuilder(String url) {
        return new HttpRequestBuilder(url, Method.GET);
    }

    public static <T> T post(String url, Object body, Type returnType) {
        return getHttpRequest().post(url, body, returnType);
    }

    public static String post(String url, Object body) {
        return (String) getHttpRequest().post(url, body, String.class);
    }

    public static <T> T postForm(String url, Map<String, Object> formParams, Type returnType) {
        return getHttpRequest().postForm(url, formParams, returnType);
    }

    public static String postForm(String url, Map<String, Object> formParams) {
        return (String) getHttpRequest().postForm(url, formParams, String.class);
    }

    public static String upload(String url, Map<String, Object> formParams) {
        return (String) getHttpRequest().postFormData(url, formParams, String.class);
    }

    public static InputStream down(String url) {
        HttpRequest httpRequest = getHttpRequest();
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
        //TODO
    }
}
