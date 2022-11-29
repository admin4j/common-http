package io.github.admin4j.http.core;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.http.HttpMethod;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/4/21 13:28
 */
@Slf4j
public abstract class AbstractHttpBuildCall {

    /**
     * 请求头
     */
    protected Map<String, String> headerMap = new HashMap<>();
    /**
     * 基础URL
     */
    protected String baseUrl = null;

    /**
     * 字符编码格式
     */
    protected java.nio.charset.Charset charset = StandardCharsets.UTF_8;

    /**
     * 从子类中获取 初始化好的 OkHttpClient
     *
     * @return
     */
    public abstract OkHttpClient getHttpClient();

    /**
     * 默认的 MediaTypeEnum
     *
     * @return
     */
    protected MediaType getMediaType() {
        if (headerMap.containsKey("Content-Type")) {
            return MediaType.parse(headerMap.get("Content-Type"));
        }
        return null;
    }

    /**
     * Escape the given string to be used as URL query value.
     *
     * @param str String to be escaped
     * @return Escaped string
     */
    public String escapeString(String str) {
        try {
            return URLEncoder.encode(str, "utf8").replace("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    /**
     * Build full URL by concatenating base path, the given sub path and query parameters.
     *
     * @param path        The sub path
     * @param queryParams The query parameters
     * @param queryMap    The query map
     * @return The full URL
     */
    public String buildUrl(String path, Pair<?>[] queryParams, Map<String, Object> queryMap) {
        if (StringUtils.isBlank(baseUrl) && queryParams == null && queryMap == null) {
            return path;
        }
        final StringBuilder url = new StringBuilder();
        if (StringUtils.startsWithIgnoreCase(path, "http")) {
            url.append(path);
        } else {
            url.append(baseUrl).append(path);
        }

        if (queryParams != null && queryParams.length > 0) {
            // support (constant) query string in `path`, e.g. "/posts?draft=1"
            String prefix = path.contains("?") ? "&" : "?";
            for (Pair param : queryParams) {
                if (param.getValue() != null) {
                    if (prefix != null) {
                        url.append(prefix);
                        prefix = null;
                    } else {
                        url.append("&");
                    }
                    String value = parameterToString(param.getValue());
                    url.append(escapeString(param.getName())).append("=").append(escapeString(value));
                }
            }
        }

        if (queryMap != null && !queryMap.isEmpty()) {
            // support (constant) query string in `path`, e.g. "/posts?draft=1"
            String prefix = url.toString().contains("?") ? "&" : "?";
            for (Map.Entry<String, Object> param : queryMap.entrySet()) {
                if (param.getValue() != null) {
                    if (prefix != null) {
                        url.append(prefix);
                        prefix = null;
                    } else {
                        url.append("&");
                    }
                    String value = parameterToString(param.getValue());
                    url.append(escapeString(param.getKey())).append("=").append(escapeString(value));
                }
            }
        }

        return url.toString();
    }

    public String buildUrl(String path, Pair<?>[] queryParams) {
        return buildUrl(path, queryParams, null);
    }

    public String buildUrl(String path, Map<String, Object> queryMap) {
        return buildUrl(path, null, queryMap);
    }

    /**
     * Set header parameters to the request builder, including default headers.
     *
     * @param headerParams Header parameters in the ofrm of Map
     * @param reqBuilder   Reqeust.Builder
     */
    protected void processHeaderParams(Map<String, Object> headerParams, Request.Builder reqBuilder) {
        for (Map.Entry<String, String> header : headerMap.entrySet()) {
            if (headerParams == null || !headerParams.containsKey(header.getKey())) {
                reqBuilder.header(header.getKey(), parameterToString(header.getValue()));
            }
        }

        if (ObjectUtils.isEmpty(headerParams)) {
            return;
        }
        for (Map.Entry<String, Object> param : headerParams.entrySet()) {
            reqBuilder.header(param.getKey(), parameterToString(param.getValue()));
        }
    }

    /**
     * Build an HTTP request with the given options.
     *
     * @param url          The sub-path of the HTTP URL
     * @param method       The request method, one of "GET", "HEAD", "OPTIONS", "POST", "PUT", "PATCH" and "DELETE"
     * @param body         The request body object
     * @param headerParams The header parameters
     * @param formParams   The form parameters
     * @return The HTTP request
     */
    protected Request buildRequest(String url,
                                   Method method,
                                   MediaTypeEnum mediaType,
                                   Object body,
                                   Map<String, Object> formParams,
                                   Map<String, Object> headerParams) {

        //handleBeforeBuildRequest()
        if (!StringUtils.startsWithIgnoreCase(url, "http")) {
            url = buildUrl(url, null, null);
        }

        final Request.Builder reqBuilder = new Request.Builder().url(url);
        processHeaderParams(headerParams, reqBuilder);

        RequestBody reqBody;
        //GET or HEAD
        if (!HttpMethod.permitsRequestBody(method.name())) {
            return reqBuilder
                    .method(method.name(), null)
                    .build();
        }

        if (mediaType == null) {
            Object contentType = ObjectUtils.isEmpty(headerParams) ? null : headerParams.get("Content-Type");
            // ensuring a default content type
            mediaType = contentType == null ? MediaTypeEnum.JSON : MediaTypeEnum.of(contentType);
        }


        if (MediaTypeEnum.FORM.equals(mediaType)) {
            reqBody = buildRequestBodyFormEncoding(formParams);
        } else if (MediaTypeEnum.FORM_DATA.equals(mediaType)) {
            reqBody = buildRequestBodyMultipart(formParams);
        } else {
            MediaType media = getMediaType();
            media = media == null ? mediaType.getMediaType() : media;
            if (body == null) {
                if (Method.DELETE.equals(method)) {
                    // allow calling DELETE without sending a request body
                    reqBody = null;
                } else {
                    // use an empty request body (for POST, PUT and PATCH)

                    reqBody = RequestBody.create(media, "");
                }
            } else {
                reqBody = serialize(body, media);
            }
        }
        return reqBuilder.method(method.name(), reqBody).build();
    }

    /**
     * Serialize the given Java object into request body according to the object's
     * class and the request Content-Type.
     *
     * @param obj The Java object
     * @return The serialized request body
     */
    protected RequestBody serialize(Object obj, MediaType mediaType) {
        if (obj instanceof byte[]) {
            // Binary (byte array) body parameter support.
            return RequestBody.create(mediaType, (byte[]) obj);
        } else if (obj instanceof File) {
            // File body parameter support.
            return RequestBody.create(mediaType, (File) obj);
        } else if (obj instanceof String) {
            return RequestBody.create(mediaType, (String) obj);
        } else {
            String content;
            if (obj != null) {
                content = serializeJSON(obj);
            } else {
                content = "";
            }


            byte[] bytes = content.getBytes(charset);
            return RequestBody.create(mediaType, bytes);
            //return RequestBody.create(content, mediaType);
        }
    }

    public abstract String serializeJSON(Object obj);

    protected String parameterToString(Object param) {
        if (param == null) {
            return "";
        } else if (param instanceof Date || param instanceof OffsetDateTime || param instanceof LocalDate) {
            //Serialize to json string and remove the " enclosing characters
            String jsonStr = serializeJSON(param);
            return jsonStr.substring(1, jsonStr.length() - 1);
        } else if (param instanceof Collection) {
            StringBuilder b = new StringBuilder();
            for (Object o : (Collection) param) {
                if (b.length() > 0) {
                    b.append(",");
                }
                b.append(o);
            }
            return b.toString();
        } else if (param instanceof String) {
            return (String) param;
        } else {
            return String.valueOf(param);
        }
    }

    /**
     * Build a form-encoding request body with the given form parameters.
     *
     * @param formParams Form parameters in the form of Map
     * @return RequestBody
     */
    protected RequestBody buildRequestBodyFormEncoding(Map<String, Object> formParams) {

        if (formParams == null || formParams.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            return builder.build();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> param : formParams.entrySet()) {
            builder.add(param.getKey(), parameterToString(param.getValue()));
        }
        return builder.build();
    }

    /**
     * Build a multipart (file uploading) request body with the given form parameters,
     * which could contain text fields and file fields.
     *
     * @param formParams Form parameters in the form of Map
     * @return RequestBody
     */
    protected RequestBody buildRequestBodyMultipart(Map<String, Object> formParams) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MediaTypeEnum.FORM_DATA.getMediaType());

        for (Map.Entry<String, Object> param : formParams.entrySet()) {
            if (param.getValue() instanceof File) {
                File file = (File) param.getValue();
                Headers partHeaders = Headers.of("Content-Disposition", "form-data; name=\"" + param.getKey() + "\"; filename=\"" + file.getName() + "\"");
                MediaType mediaType = guessContentTypeFromFile(file);
                builder.addPart(partHeaders, RequestBody.create(mediaType, file));
            } else {
                Headers partHeaders = Headers.of("Content-Disposition", "form-data; name=\"" + param.getKey() + "\"");
                builder.addPart(partHeaders, RequestBody.create(null, parameterToString(param.getValue())));
            }
        }
        return builder.build();
    }

    protected MediaType guessContentTypeFromFile(File file) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        if (contentType == null) {
            return MediaTypeEnum.OCTET_STREAM.getMediaType();
        } else {
            return MediaType.parse(contentType);
        }
    }

    /**
     * Build HTTP call with the given options.
     *
     * @param path         The sub-path of the HTTP URL
     * @param method       The request method, one of "GET", "HEAD", "OPTIONS", "POST", "PUT", "PATCH" and "DELETE"
     * @param queryParams  The query parameters
     * @param body         The request body object
     * @param headerParams The header parameters
     * @param formParams   The form parameters
     * @return The HTTP call
     */
    public Call buildCall(String path,
                          Method method,
                          MediaTypeEnum mediaType,
                          Pair<?>[] queryParams,
                          Map<String, Object> queryMap,
                          Object body,
                          Map<String, Object> formParams,
                          Map<String, Object> headerParams
    ) {

        final String url = buildUrl(path, queryParams, queryMap);
        Request request = buildRequest(url, method, mediaType, body, formParams, headerParams);
        return getHttpClient().newCall(request);
    }

    /**
     * Build HTTP call with the given options.
     *
     * @param url          The sub-path of the HTTP URL
     * @param method       The request method, one of "GET", "HEAD", "OPTIONS", "POST", "PUT", "PATCH" and "DELETE"
     * @param body         The request body object
     * @param formParams   The query parameters
     * @param headerParams The header parameters
     * @param formParams   The form parameters
     * @return The HTTP call
     */
    public Call buildCall(String url,
                          Method method,
                          Object body,
                          Map<String, Object> formParams,
                          Map<String, Object> headerParams) {
        return buildCall(url, method, null, null, null, body, formParams, headerParams);
    }

    /**
     * 重定向请求
     *
     * @param response
     * @return
     */
    protected Request buildRedirectRequest(Response response) {

        String location = response.header("Location");
        if (StringUtils.isBlank(location)) {
            return null;
        }

        Request request = response.request();
        HttpUrl url = request.url().resolve(location);

        Request.Builder builder = request.newBuilder();
        return builder.url(url).build();
    }

    public Call buildGet(String path, Map<String, Object> queryMap, Pair<?>... queryParams) {

        return buildCall(path, Method.GET, null, queryParams, queryMap, null, null, null);
    }

    // ======================= build GET POST ===============

    protected Call buildPost(String url,
                             MediaTypeEnum mediaTypeEnum,
                             Object body,
                             Map<String, Object> formParams,
                             Map<String, Object> headerParams) {

        Request request = buildRequest(url, Method.POST, mediaTypeEnum, body, formParams, headerParams);

        return getHttpClient().newCall(request);
    }
}
