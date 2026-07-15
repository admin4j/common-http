package io.github.admin4j.http.util.core;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * HTTP 请求日志拦截器
 *
 * @author admin4j
 * @since 2024/1/1
 */
public class HttpLogInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        HttpConfig config = HttpDefaultConfig.get();
        Request request = chain.request();

        if (config == null || !config.isLogEnabled()) {
            return chain.proceed(request);
        }

        // 请求日志
        HttpLogger.logRequest(config, request);

        long startTime = System.currentTimeMillis();
        try {
            Response response = chain.proceed(request);
            long duration = System.currentTimeMillis() - startTime;


            // 失败响应日志（非 2xx）
            if (!response.isSuccessful()) {
                HttpLogger.logFailureResponse(config, request, response, duration);
            } else {
                // 响应日志
                HttpLogger.logResponse(config, request, response, duration);
            }

            return response;
        } catch (IOException e) {
            // 异常日志
            HttpLogger.logFailure(config, request, e);
            throw e;
        }
    }
}
