package http;

import http.entity.R;
import io.github.admin4j.http.ApiJsonClient;
import io.github.admin4j.http.core.HttpConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/5/10 14:08
 */
class ApiJsonClientTest {
    ApiJsonClient httpRequest;

    @BeforeEach
    void init() {
        HttpConfig httpConfig = new HttpConfig();
        httpRequest = new ApiJsonClient(httpConfig);
    }


    @Test
    void get() throws IOException {
        Map<String, Object> stringObjectMap = httpRequest.get("https://gitee.com/o0w0o_1/mock/raw/master/Map");
        System.out.println("stringObjectMap = " + stringObjectMap);
        System.out.println("stringObjectMap = " + stringObjectMap.get("code"));
    }

    @Test
    void getObject() throws IOException {
        R csdnR = httpRequest.get("https://gitee.com/o0w0o_1/mock/raw/master/Map", R.class);
        System.out.println("csdnR = " + csdnR);
        System.out.println("csdnR = " + csdnR.getMsg());
        System.out.println("csdnR = " + csdnR.getCode());
        System.out.println("csdnR = " + csdnR.getData());


    }

    @Test
    void getList() throws IOException {

        List<Object> list = httpRequest.getList("https://gitee.com/o0w0o_1/mock/raw/master/list_object", Object.class);
        System.out.println("list = " + list);
    }

    //TODO - Gsog error
    @Test
    void getListObject() throws IOException {

        List<R> list = httpRequest.getList("https://gitee.com/o0w0o_1/mock/raw/master/list_object", R.class);
        System.out.println("list = " + list);
        for (R r : list) {
            Object data = r.getData();
            System.out.println("data = " + data);
        }
    }


}