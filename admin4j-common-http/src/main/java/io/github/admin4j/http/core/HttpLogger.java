package io.github.admin4j.http.core;

import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author andanyang
 * @since 2022/5/10 14:24
 */
public class HttpLogger implements HttpLoggingInterceptor.Logger {
    private static final Marker MARKER = MarkerFactory.getMarker("OKHTTP");

    private final Logger logger;

    public HttpLogger() {
        this.logger = LoggerFactory.getLogger(HttpLogger.class);
    }

    public HttpLogger(Logger logger) {
        if (logger != null) {
            this.logger = logger;
        } else {
            this.logger = LoggerFactory.getLogger(HttpLogger.class);
        }
    }

    public HttpLogger(String logName) {

        if (StringUtils.isBlank(logName)) {
            this.logger = LoggerFactory.getLogger(HttpLogger.class);
        } else {
            this.logger = LoggerFactory.getLogger(logName);
        }
    }

    @Override
    public void log(String message) {

        logger.debug(MARKER, message);
    }
}
