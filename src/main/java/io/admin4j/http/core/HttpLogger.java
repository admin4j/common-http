package io.admin4j.http.core;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author andanyang
 * @since 2022/5/10 14:24
 */
@Slf4j
public class HttpLogger implements HttpLoggingInterceptor.Logger {
    private static final Marker MARKER = MarkerFactory.getMarker("OKHTTP");

    @Override
    public void log(String message) {
        log.info(MARKER, message);
    }
}
