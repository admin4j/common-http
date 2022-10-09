package io.github.admin4j.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.admin4j.http.core.AbstractHttpRequest;
import io.github.admin4j.http.core.MediaTypeEnum;
import io.github.admin4j.http.core.Pair;
import io.github.admin4j.http.exception.HttpException;
import io.github.admin4j.http.factory.HttpClientFactory;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/4/21 11:27
 */
@Accessors
class JSONHttpRequest extends AbstractHttpRequest {

    @Setter
    private OkHttpClient okHttpClient;

    public JSONHttpRequest(HttpConfig httpConfig) {
        super();
        okHttpClient = HttpClientFactory.okHttpClient(httpConfig);
    }

    @Override
    public OkHttpClient getHttpClient() {
        return okHttpClient;
    }


    @Override
    public String serializeJSON(Object obj) {
        return JSON.toJSONString(obj);
    }

    private JSONObject serialize(Response response) {
        ResponseBody body = response.body();
        if (body == null) {
            throw new HttpException("response body is null");
        }
        try {
            return JSONObject.parseObject(body.string());
        } catch (IOException e) {

            throw new HttpException(e);
        }
    }

    //=============== request ===============
    public JSONObject get(String path, Map<String, Object> queryMap) {
        Response response = get(path, queryMap, null);

        return serialize(response);
    }

    public JSONObject get(String path, Pair<?>... queryParams) {
        Response response = get(path, null, queryParams);
        return serialize(response);
    }


    public JSONObject postForm(String url, Map<String, Object> formParams) {

        Response response = post(url, MediaTypeEnum.FORM, null, formParams, null);
        return serialize(response);
    }

    public JSONObject postFormData(String url, Map<String, Object> formParams) {

        Response response = post(url, MediaTypeEnum.FORM_DATA, null, formParams, null);
        return serialize(response);
    }

    public JSONObject post(String url, Object body) {

        Response response = post(url, MediaTypeEnum.JSON, body, null, null);
        return serialize(response);
    }
}
