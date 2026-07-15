package io.github.admin4j.http.util.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 新日志架构单元测试
 * <p>
 * 覆盖：HttpLogConfig 默认值/拷贝、HttpLogger 防护条件、
 * 日志输出验证、HttpLogInterceptor 透传与短路。
 */
class HttpLoggingConfigTest {

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(HttpLogger.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.ALL);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    // ======================== HttpLogConfig 默认值 ========================

    @Test
    void testLogConfigDefaultValues() {
        HttpLogConfig config = new HttpLogConfig();
        assertFalse(config.isLogEnabled());
        assertFalse(config.isLogRequestHeaders());
        assertTrue(config.isLogRequestUrl());
        assertTrue(config.isLogRequestParams());
        assertTrue(config.isLogResponseBody());
        assertFalse(config.isLogResponseHeaders());
        assertTrue(config.isLogOnFailure());
    }

    // ======================== HttpLogConfig 拷贝 ========================

    @Test
    void testLogConfigCopy() {
        HttpLogConfig config = new HttpLogConfig();
        config.setLogEnabled(true);
        config.setLogRequestHeaders(true);
        config.setLogRequestUrl(false);
        config.setLogRequestParams(false);
        config.setLogResponseBody(false);
        config.setLogResponseHeaders(true);
        config.setLogOnFailure(false);

        HttpLogConfig copy = config.copy();

        assertNotSame(config, copy);
        assertTrue(copy.isLogEnabled());
        assertTrue(copy.isLogRequestHeaders());
        assertFalse(copy.isLogRequestUrl());
        assertFalse(copy.isLogRequestParams());
        assertFalse(copy.isLogResponseBody());
        assertTrue(copy.isLogResponseHeaders());
        assertFalse(copy.isLogOnFailure());

        // 修改副本不影响原对象
        copy.setLogEnabled(false);
        assertTrue(config.isLogEnabled());
    }

    @Test
    void testLogConfigCopyIndependence() {
        HttpLogConfig config = new HttpLogConfig();
        config.setLogEnabled(true);
        config.setLogRequestHeaders(true);

        HttpLogConfig copied = config.copy();

        assertNotSame(config, copied);
        assertTrue(copied.isLogEnabled());
        assertTrue(copied.isLogRequestHeaders());

        // 修改拷贝不影响原对象
        copied.setLogEnabled(false);
        copied.setLogRequestHeaders(false);
        assertTrue(config.isLogEnabled());
        assertTrue(config.isLogRequestHeaders());
    }

    // ======================== HttpLogger 防护条件 ========================

    @Test
    void testLogRequestGuardConditions() {
        Request request = new Request.Builder().url("http://example.com/test").build();

        // null config
        HttpLogger.logRequest(null, request);
        // disabled logging
        HttpConfig config = createConfig(false);
        HttpLogger.logRequest(config, request);
        // null request
        HttpLogger.logRequest(config, null);

        assertTrue(getAllLogMessages().isEmpty());
    }

    @Test
    void testLogResponseGuardConditions() {
        Request request = new Request.Builder().url("http://example.com/test").build();
        Response response = buildSimpleResponse(request, 200, "OK");

        // null config
        HttpLogger.logResponse(null, request, response, 100);
        // disabled logging
        HttpConfig config = createConfig(false);
        HttpLogger.logResponse(config, request, response, 100);
        // null request
        HttpLogger.logResponse(config, null, response, 100);
        // null response
        HttpLogger.logResponse(config, request, null, 100);

        assertTrue(getAllLogMessages().isEmpty());
    }

    @Test
    void testLogFailureGuardConditions() {
        Request request = new Request.Builder().url("http://example.com/test").build();
        HttpConfig config = createConfig(false);
        Exception ex = new IOException("connection refused");

        // disabled logging
        HttpLogger.logFailure(config, request, ex);
        // null request
        config = createConfig(true);
        HttpLogger.logFailure(config, null, ex);
        // logOnFailure = false
        config.getLogConfig().setLogOnFailure(false);
        HttpLogger.logFailure(config, request, ex);

        assertTrue(getAllLogMessages().isEmpty());
    }

    @Test
    void testLogFailureResponseGuardConditions() {
        Request request = new Request.Builder().url("http://example.com/test").build();
        Response response = buildSimpleResponse(request, 500, "Internal Server Error");

        // disabled logging
        HttpConfig config = createConfig(false);
        HttpLogger.logFailureResponse(config, request, response, 100);
        // logOnFailure = false
        config = createConfig(true);
        config.getLogConfig().setLogOnFailure(false);
        HttpLogger.logFailureResponse(config, request, response, 100);

        assertTrue(getAllLogMessages().isEmpty());
    }

    // ======================== HttpLogger 日志输出 ========================

    @Test
    void testLogRequestBasic() {
        HttpConfig config = createConfig(true);
        Request request = new Request.Builder().url("http://example.com/api/v1/users").build();

        HttpLogger.logRequest(config, request);

        List<String> messages = getAllLogMessages();
        assertEquals(1, messages.size());
        String msg = messages.get(0);
        assertTrue(msg.contains("HTTP Request"));
        assertTrue(msg.contains("GET"));
        assertTrue(msg.contains("http://example.com/api/v1/users"));
    }

    @Test
    void testLogRequestWithHeaders() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogRequestHeaders(true);
        Request request = new Request.Builder()
                .url("http://example.com/test")
                .addHeader("X-Custom", "value123")
                .build();

        HttpLogger.logRequest(config, request);

        String msg = getAllLogMessages().get(0);
        assertTrue(msg.contains("Headers"));
        assertTrue(msg.contains("X-Custom"));
        assertTrue(msg.contains("value123"));
    }

    @Test
    void testLogRequestWithoutHeaders() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogRequestHeaders(false);
        Request request = new Request.Builder()
                .url("http://example.com/test")
                .addHeader("X-Custom", "value123")
                .build();

        HttpLogger.logRequest(config, request);

        String msg = getAllLogMessages().get(0);
        assertFalse(msg.contains("Headers"));
    }

    @Test
    void testLogRequestWithBody() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogRequestParams(true);
        RequestBody body = RequestBody.create(
                "{\"name\":\"test\"}",
                MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://example.com/test")
                .post(body)
                .build();

        HttpLogger.logRequest(config, request);

        String msg = getAllLogMessages().get(0);
        assertTrue(msg.contains("Body"));
        assertTrue(msg.contains("{\"name\":\"test\"}"));
    }

    @Test
    void testLogRequestWithoutBody() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogRequestParams(false);
        RequestBody body = RequestBody.create(
                "{\"name\":\"test\"}",
                MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://example.com/test")
                .post(body)
                .build();

        HttpLogger.logRequest(config, request);

        String msg = getAllLogMessages().get(0);
        assertFalse(msg.contains("{\"name\":\"test\"}"));
    }

    @Test
    void testLogRequestUrlEnabled() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogRequestUrl(true);
        Request request = new Request.Builder().url("http://example.com/api/v1/users").build();

        HttpLogger.logRequest(config, request);

        String msg = getAllLogMessages().get(0);
        assertTrue(msg.contains("http://example.com/api/v1/users"));
    }

    @Test
    void testLogRequestUrlDisabled() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogRequestUrl(false);
        Request request = new Request.Builder().url("http://example.com/api/v1/users").build();

        HttpLogger.logRequest(config, request);

        String msg = getAllLogMessages().get(0);
        assertTrue(msg.contains("GET"));
        assertFalse(msg.contains("http://example.com/api/v1/users"));
    }

    @Test
    void testLogResponseBasic() {
        HttpConfig config = createConfig(true);
        Request request = new Request.Builder().url("http://example.com/test").build();
        Response response = buildSimpleResponse(request, 200, "OK");

        HttpLogger.logResponse(config, request, response, 150);

        String msg = getAllLogMessages().get(0);
        assertTrue(msg.contains("HTTP Response"));
        assertTrue(msg.contains("200"));
        assertTrue(msg.contains("150ms"));
    }

    @Test
    void testLogResponseWithHeaders() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogResponseHeaders(true);
        Request request = new Request.Builder().url("http://example.com/test").build();
        Response response = buildSimpleResponse(request, 200, "OK");

        HttpLogger.logResponse(config, request, response, 100);

        String msg = getAllLogMessages().get(0);
        assertTrue(msg.contains("Headers"));
    }

    @Test
    void testLogResponseWithoutHeaders() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogResponseHeaders(false);
        Request request = new Request.Builder().url("http://example.com/test").build();
        Response response = buildSimpleResponse(request, 200, "OK");

        HttpLogger.logResponse(config, request, response, 100);

        String msg = getAllLogMessages().get(0);
        assertFalse(msg.contains("Headers"));
    }

    @Test
    void testLogResponseWithBody() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogResponseBody(true);
        Request request = new Request.Builder().url("http://example.com/test").build();
        Response response = buildResponseBodyResponse(request, 200, "OK", "{\"result\":\"success\"}");

        HttpLogger.logResponse(config, request, response, 100);

        String msg = getAllLogMessages().get(0);
        assertTrue(msg.contains("Body"));
        assertTrue(msg.contains("success"));
    }

    @Test
    void testLogResponseWithoutBody() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogResponseBody(false);
        Request request = new Request.Builder().url("http://example.com/test").build();
        Response response = buildResponseBodyResponse(request, 200, "OK", "{\"result\":\"success\"}");

        HttpLogger.logResponse(config, request, response, 100);

        String msg = getAllLogMessages().get(0);
        assertFalse(msg.contains("Body"));
    }

    @Test
    void testLogFailureResponseEnabled() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogOnFailure(true);
        Request request = new Request.Builder().url("http://example.com/test").build();
        Response response = buildSimpleResponse(request, 500, "Internal Server Error");

        HttpLogger.logFailureResponse(config, request, response, 200);

        List<ILoggingEvent> events = getLogEvents();
        assertEquals(1, events.size());
        assertEquals(Level.ERROR, events.get(0).getLevel());
        String msg = events.get(0).getFormattedMessage();
        assertTrue(msg.contains("Failed"));
        assertTrue(msg.contains("500"));
    }

    @Test
    void testLogFailureResponseDisabled() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogOnFailure(false);
        Request request = new Request.Builder().url("http://example.com/test").build();
        Response response = buildSimpleResponse(request, 500, "Internal Server Error");

        HttpLogger.logFailureResponse(config, request, response, 200);

        assertTrue(getAllLogMessages().isEmpty());
    }

    @Test
    void testLogFailureExceptionEnabled() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogOnFailure(true);
        Request request = new Request.Builder().url("http://example.com/test").build();
        Exception ex = new IOException("connection timeout");

        HttpLogger.logFailure(config, request, ex);

        List<ILoggingEvent> events = getLogEvents();
        assertEquals(1, events.size());
        assertEquals(Level.ERROR, events.get(0).getLevel());
        String msg = events.get(0).getFormattedMessage();
        assertTrue(msg.contains("Request Failed"));
    }

    @Test
    void testLogFailureExceptionDisabled() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogOnFailure(false);
        Request request = new Request.Builder().url("http://example.com/test").build();
        Exception ex = new IOException("connection timeout");

        HttpLogger.logFailure(config, request, ex);

        assertTrue(getAllLogMessages().isEmpty());
    }

    // ======================== HttpLogInterceptor 透传与短路 ========================

    @Test
    void testInterceptorPassesThroughWhenLoggingDisabled() throws IOException {
        HttpConfig config = createConfig(false);
        HttpDefaultConfig.set(config);

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setBody("hello").setResponseCode(200));

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLogInterceptor())
                    .build();

            Request request = new Request.Builder().url(server.url("/test")).build();
            Response response = client.newCall(request).execute();
            response.close();

            assertEquals(200, response.code());
            assertTrue(getAllLogMessages().isEmpty());
        }
    }

    @Test
    void testInterceptorLogsRequestWhenEnabled() throws IOException {
        HttpConfig config = createConfig(true);
        HttpDefaultConfig.set(config);

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setBody("hello").setResponseCode(200));

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLogInterceptor())
                    .build();

            Request request = new Request.Builder().url(server.url("/api/test")).build();
            Response response = client.newCall(request).execute();
            response.close();

            List<String> messages = getAllLogMessages();
            assertTrue(messages.size() >= 2);
            assertTrue(messages.stream().anyMatch(m -> m.contains("HTTP Request")));
            assertTrue(messages.stream().anyMatch(m -> m.contains("HTTP Response")));
        }
    }

    @Test
    void testInterceptorLogsFailureOnNon2xx() throws IOException {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogOnFailure(true);
        HttpDefaultConfig.set(config);

        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(500).setBody("error"));

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLogInterceptor())
                    .build();

            Request request = new Request.Builder().url(server.url("/api/test")).build();
            Response response = client.newCall(request).execute();
            response.close();

            List<String> messages = getAllLogMessages();
            assertTrue(messages.stream().anyMatch(m -> m.contains("Failed")));
        }
    }

    @Test
    void testInterceptorLogsException() {
        HttpConfig config = createConfig(true);
        config.getLogConfig().setLogOnFailure(true);
        HttpDefaultConfig.set(config);

        // Use an invalid URL to trigger IOException
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLogInterceptor())
                .build();

        // Use a port that's not listening
        Request request = new Request.Builder().url("http://127.0.0.1:1/test").build();

        assertThrows(IOException.class, () -> client.newCall(request).execute());

        List<String> messages = getAllLogMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Request Failed")));
    }

    // ======================== Helper Methods ========================

    private HttpConfig createConfig(boolean logEnabled) {
        HttpConfig config = new HttpConfig();
        config.getLogConfig().setLogEnabled(logEnabled);
        return config;
    }

    private List<ILoggingEvent> getLogEvents() {
        return listAppender.list;
    }

    private List<String> getAllLogMessages() {
        return listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());
    }

    private Response buildSimpleResponse(Request request, int code, String message) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(message)
                .build();
    }

    private Response buildResponseBodyResponse(Request request, int code, String message, String body) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(message)
                .body(ResponseBody.create(body, MediaType.parse("application/json")))
                .build();
    }
}
