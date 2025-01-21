package io.github.admin4j.http;

import io.github.admin4j.http.core.HttpHeaderKey;
import io.github.admin4j.http.exception.HttpException;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

/**
 * @author andanyang
 * @since 2022/11/8 16:23
 */
class HttpRequestTest {

    @Test
    void get() {
        Response response = HttpRequest.get("https://search.gitee.com/?skin=rec&type=repository")
                .queryMap("q", "admin4j")
                .header(HttpHeaderKey.USER_AGENT, "admin4j")
                .execute();
        System.out.println("response = " + response);
    }

    @Test
    void post() {
        Response response = HttpRequest.get("https://uatservice-etax.one.th/etaxbatchweb/etaxsignbatch")
                .queryMap("q", "admin4j")
                .header(HttpHeaderKey.USER_AGENT, "admin4j")
                .form("username", "admin")
                .form("password", "admin123")
                .execute();
        System.out.println("response = " + response);
    }

    @Test
    void postError() {

        //模拟http 404 异常
        try{
            Response response = HttpRequest.get("https://uatservice-etax.one.th/etaxbatchweb/etaxsignbatch")
                    .queryMap("q", "admin4j")
                    .header(HttpHeaderKey.USER_AGENT, "admin4j")
                    .form("username", "admin")
                    .form("password", "admin123")
                    .execute();
            System.out.println("response = " + response);
        }catch(HttpException e){
            System.out.println("e = " + e);
        }

    }
}