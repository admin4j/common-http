package http.util;

import io.github.admin4j.http.core.Pair;
import io.github.admin4j.http.util.HttpUtil;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/11/3 13:29
 */
public class HttpUtilTest {


    @Test
    void get() {
        Response response = HttpUtil.get("https://github.com/search", Pair.of("q", "okhttp"));
        System.out.println("response = " + response);
    }

    @Test
    void testGet() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("q", "okhttp");
        Response response = HttpUtil.get("https://search.gitee.com/", queryParams);
        System.out.println("response = " + response);
    }

    @Test
    void post() {

        Response post = HttpUtil.post("https://oapi.dingtalk.com/robot/send?access_token=37f5954ab60ea8b2e431ae9101b1289c138e85aa6eb6e3940c35ee13ff8b6335", "{\"msgtype\": \"text\",\"text\": {\"content\":\"【反馈提醒】我就是我, 是不一样的烟火\"}}");
        System.out.println("post = " + post);
    }

    @Test
    void postForm() {

        Map<String, Object> formParams = new HashMap<>(16);
        formParams.put("username", "admin");
        formParams.put("password", "admin123");
        Response response = HttpUtil.postForm("http://192.168.1.13:9100/auth/login",
                formParams
        );
        System.out.println("response = " + response);
    }

    @Test
    void upload() {
    }

    @Test
    void down() {
    }

    @Test
    void testDown() {
    }
}
