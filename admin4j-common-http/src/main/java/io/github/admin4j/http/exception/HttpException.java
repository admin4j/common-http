package io.github.admin4j.http.exception;

import java.util.List;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/5/10 16:01
 */
public class HttpException extends RuntimeException {

    private int code = 0;
    private Map<String, List<String>> responseHeaders = null;
    private String responseBody = null;

    public HttpException(Throwable throwable) {
        super(throwable);
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, int code) {
        super(message);
        this.code = code;
    }

    public HttpException(String message, Throwable throwable, int code, Map<String, List<String>> responseHeaders, String responseBody) {
        super(message, throwable);
        this.code = code;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public HttpException(String message, int code, Map<String, List<String>> responseHeaders, String responseBody) {
        super(message);
        this.code = code;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public HttpException(String message, Throwable throwable, int code, Map<String, List<String>> responseHeaders) {
        super(message, throwable);
        this.code = code;
        this.responseHeaders = responseHeaders;
    }
}
