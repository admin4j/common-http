package io.github.admin4j.http;

import io.github.admin4j.http.core.Method;
import io.github.admin4j.http.core.Pair;
import io.github.admin4j.http.entity.R;
import lombok.Cleanup;
import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/5/10 14:08
 */
class HttpRequestTest {
    HttpRequest httpRequest;

    @BeforeEach
    void init() {
        HttpConfig httpConfig = new HttpConfig();
        httpRequest = new HttpRequest(httpConfig);
    }

    @Test
    void get() throws IOException {

        String s1 = HttpUtil.get("https://www.hutool.cn/docs/#/http/Http%E8%AF%B7%E6%B1%82-HttpRequest",
                Pair.of("id", "介绍"),
                Pair.of("id", 12));
        System.out.println("s1 = " + s1);
        String s = httpRequest.get("https://www.hutool.cn/docs/#/http/Http%E8%AF%B7%E6%B1%82-HttpRequest", Pair.of("id", "介绍"));
        System.out.println("s = " + s);
    }

    @Test
    void post() {
        String post = HttpUtil.post("https://oapi.dingtalk.com/robot/send?access_token=37f5954ab60ea8b2e431ae9101b1289c138e85aa6eb6e3940c35ee13ff8b6335",
                "{\"msgtype\": \"text\",\"text\": {\"content\":\"【反馈提醒】我就是我, 是不一样的烟火\"}}");
        System.out.println("post = " + post);
    }

    @Test
    void postForm() {

        Map<String, Object> formParams = new HashMap<>(16);
        formParams.put("username", "admin");
        formParams.put("password", "admin123");
        String post = HttpUtil.postForm("http://192.168.1.13:9100/auth/login",
                formParams
        );
        System.out.println("post = " + post);
    }

    @Test
    void postForm2() {

        Map<String, Object> formParams = new HashMap<>(16);
        formParams.put("username", "admin");
        formParams.put("password", "admin123");
        Object post = HttpUtil.postForm("http://192.168.1.13:9100/auth/login",
                formParams,
                R.class
        );
        System.out.println("post = " + post);
    }

    @SneakyThrows
    @Test
    void getRouters() {
        Map<String, Object> headerParams = new HashMap<>(16);
        headerParams.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NTIzNTA2NDAsInVzZXJJZCI6MX0.idSAdHULHlVKZapcREsV6pg7kDcda70D8vSyzl_ubiY");
        String url = "http://192.168.1.13:9100/system/menu/getRouters";
        Call call = httpRequest.buildCall(url, Method.GET, null, null, headerParams);
        Object execute = httpRequest.execute(call, R.class);
        System.out.println(execute);
    }

    @SneakyThrows
    @Test
    void getRouters2() {
        String url = "http://192.168.1.13:9100/system/menu/getRouters";

        Object authorization = HttpUtil.getBuilder(url).addHeader("Authorization",
                        "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NTIzNTA2NDAsInVzZXJJZCI6MX0.idSAdHULHlVKZapcREsV6pg7kDcda70D8vSyzl_ubiY")
                .addHeader("1", 2)
                .execute(R.class);
        System.out.println("authorization = " + authorization);
    }


    @SneakyThrows
    @Test
    void upload() {
        String url = "http://192.168.1.12:9100/system/upload/test";

        Object authorization = HttpUtil.postBuilder(url)
                .mediaType(MediaTypeEnum.FORM_DATA)
                .addHeader("Authorization",
                        "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NTIzNTA2NDAsInVzZXJJZCI6MX0.idSAdHULHlVKZapcREsV6pg7kDcda70D8vSyzl_ubiY")
                .addHeader("1", 2)
                .addForm("file", new File("C:\\Users\\andanyang\\Pictures\\src=http___wx2.sinaimg.cn_mw690_005YeltJly1gyiopv8b43j30ss0ssdoc.jpg&refer=http___wx2.sinaimg.jpg"))
                .execute(R.class);
        System.out.println("authorization = " + authorization);
    }

    //upload/1/test/1523977637482352642.jpg

    @SneakyThrows
    @Test
    void download() {
        String url = "http://192.168.1.12:9100/system/upload/test";

        ResponseBody execute = HttpUtil.getBuilder("https://img-blog.csdnimg.cn/20200709161622641.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Fnb25pZTIwMTIxOA==,size_16,color_FFFFFF,t_70")
                .execute();

        @Cleanup InputStream inputStream = execute.byteStream();
        @Cleanup BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\andanyang\\Pictures\\12\\123.jpg");
        byte[] b = new byte[1024];
        while ((bufferedInputStream.read(b)) != -1) {
            fileOutputStream.write(b);// 写入数据
        }
        fileOutputStream.flush();
    }

    @Test
    void down() throws IOException {

        String imgUrl = "https://img-blog.csdnimg.cn/20200709161622641.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Fnb25pZTIwMTIxOA==,size_16,color_FFFFFF,t_70";
        HttpUtil.down(imgUrl, "C:\\Users\\andanyang\\Pictures\\123\\12");
    }


}