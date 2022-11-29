package io.github.admin4j.http.core;

import io.github.admin4j.http.exception.HttpException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @author andanyang
 * @since 2022/4/21 13:28
 */
@Slf4j
public abstract class AbstractHttpExecute extends AbstractHttpBuildCall {

    /**
     * 是否自动304 重定向跳转
     */
    protected boolean followRedirects = true;

    // ------------- execute -------------
    public Response execute(Call call) throws HttpException {
        return execute(call, this::handleResponse);
    }

    public Response execute(Call call, UnaryOperator<Response> function) throws HttpException {
        try {
            Response response = call.execute();
            response = function.apply(response);
            return response;
        }
        //catch (SocketTimeoutException e)
        catch (IOException e) {
            throw new HttpException(e);
        }
    }

    /**
     * 异步执行
     *
     * @param call
     * @param callback
     */
    public void executeAsync(Call call, final Callback callback) {

        call.enqueue(callback);
    }

    /**
     * 返回二进制内容，用于下载文件
     *
     * @param path
     * @param method
     * @param mediaType
     * @param queryParams
     * @param queryMap
     * @param body
     * @param formParams
     * @param headerParams
     * @return
     */
    protected InputStream executeByteStream(
            String path,
            Method method,
            MediaTypeEnum mediaType,
            Pair<?>[] queryParams,
            Map<String, Object> queryMap,
            Object body,
            Map<String, Object> formParams,
            Map<String, Object> headerParams
    ) {
        Response response = null;

        try {
            Call call = buildCall(path, method, mediaType, queryParams, queryMap, body, formParams, headerParams);
            response = execute(call);
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful()) {
                log.error("executeByteStream ERROR" + responseBody.string());
                throw new HttpException(response);
            } else {
                return responseBody != null ? responseBody.byteStream() : null;
            }
        } catch (IOException var8) {
            log.error("executeByteStream Exception", var8);
            throw new HttpException(var8.getMessage());
        }
    }

    protected Response handleResponse(Response response) {

        if (response.isSuccessful()) {
            return response;
        } else if (followRedirects && response.code() <= 399 && response.code() >= 300) {
            //重定向
            Request request = buildRedirectRequest(response);
            if (request == null) {
                throw new HttpException("not find Redirect url");
            }
            Call call = getHttpClient().newCall(request);
            return execute(call);
        } else {
            return response;
        }
    }
}
