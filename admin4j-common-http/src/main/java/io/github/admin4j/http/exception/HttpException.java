package io.github.admin4j.http.exception;

import lombok.Getter;
import okhttp3.Response;

import java.util.List;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/5/10 16:01
 */
@Getter
public class HttpException extends RuntimeException {

    private static final long serialVersionUID = 6962672754149738953L;
    /**
     * HTTP status code
     */
    private int httpStatusCode = 200;
    private Map<String, List<String>> responseHeaders = null;
    private String responseBody = null;

    public HttpException(Throwable throwable) {
        super(throwable);
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public HttpException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpException(String message, Throwable throwable, int httpStatusCode, Map<String, List<String>> responseHeaders, String responseBody) {
        super(message, throwable);
        this.httpStatusCode = httpStatusCode;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public HttpException(String message, int httpStatusCode, Map<String, List<String>> responseHeaders, String responseBody) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public HttpException(String message, Throwable throwable, int httpStatusCode, Map<String, List<String>> responseHeaders) {
        super(message, throwable);
        this.httpStatusCode = httpStatusCode;
        this.responseHeaders = responseHeaders;
    }

    public HttpException(Response response) {
        super(response.message());
        this.httpStatusCode = response.code();
        this.responseHeaders = response.headers().toMultimap();
        //this.responseBody = response.body().string();
    }
}
