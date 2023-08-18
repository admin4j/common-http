package io.github.admin4j.http.factory;

import io.github.admin4j.http.core.HttpConfig;
import io.github.admin4j.http.core.HttpDefaultConfig;
import io.github.admin4j.http.core.HttpLogger;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.net.CookiePolicy.ACCEPT_ORIGINAL_SERVER;

/**
 * @author andanyang
 * @since 2022/10/9 17:39
 */
public class HttpClientFactory {

    private HttpClientFactory() {
    }

    /**
     * 创建 OkHttpClient
     *
     * @param httpConfig
     * @return
     */
    public static OkHttpClient okHttpClient(HttpConfig httpConfig) {
        return okHttpClient(httpConfig, httpConfig.isCommonConnectionPool());
    }


    /**
     * 创建 OkHttpClient
     *
     * @param httpConfig
     * @param commonConnectionPool 是否公用连接池
     * @return
     */
    public static OkHttpClient okHttpClient(HttpConfig httpConfig, boolean commonConnectionPool) {

        ConnectionPool connectionPool;
        if (commonConnectionPool) {
            connectionPool = HttpDefaultConfig.getConnectionPool();
        } else {
            connectionPool = new ConnectionPool(httpConfig.getMaxIdleConnections(), httpConfig.getKeepAliveDuration(), TimeUnit.SECONDS);
        }

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger(httpConfig.getLogName()));
        logInterceptor.setLevel(httpConfig.getLoggLevel());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                //跳转由自己控制
                .followRedirects(httpConfig.isFollowRedirects())
                .addNetworkInterceptor(logInterceptor)
                .readTimeout(Duration.ofSeconds(httpConfig.getReadTimeout()))
                .connectTimeout(Duration.ofSeconds(httpConfig.getReadTimeout()));

        if (httpConfig.isCookie()) {
            CookieManager cookieManager = new CookieManager(null, ACCEPT_ORIGINAL_SERVER);
            builder.cookieJar(new JavaNetCookieJar(cookieManager));
        }
        //proxy
        if (httpConfig.getProxy() != null) {
            builder.proxy(new Proxy(httpConfig.getProxy().getType(),
                    new InetSocketAddress(httpConfig.getProxy().getHost(), httpConfig.getProxy().getPort())));
            if (StringUtils.isNotBlank(httpConfig.getProxy().getPassword())) {
                builder.proxyAuthenticator((route, response) -> {

                    //设置代理服务器账号密码
                    String credential = Credentials.basic(httpConfig.getProxy().getUserName(), httpConfig.getProxy().getPassword());
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                });
            }
        }
        return builder.build();
    }
}
