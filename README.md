# OKHttpUtil

在Java的世界中，Http客户端之前一直是Apache家的HttpClient占据主导，但是由于此包较为庞大，API又比较难用，因此并不使用很多场景。而新兴的OkHttp、Jodd-http固然好用，但是面对一些场景时，学习成本还是有一些的。很多时候，我们想追求轻量级的Http客户端，并且追求简单易用。而OKHttp
是一套处理 HTTP 网络请求的依赖库，由 Square 公司设计研发并开源，目前可以在 Java 和 Kotlin 中使用。对于 Android App
来说，OkHttp 现在几乎已经占据了所有的网络请求操作，对于服务器端请求外部接口也是必备的选择 。针对OKHttp
OkHttpUtil做了一层封装，使Http请求变得无比简单。

# OKHttpUtil使用

maven引入

```xml

<dependency>
    <groupId>io.github.admin4j</groupId>
    <artifactId>http</artifactId>
    <version>0.4.0</version>
</dependency>
```

最新版查询 [https://search.maven.org/artifact/io.github.admin4j/http](https://search.maven.org/artifact/io.github.admin4j/http)

## GET

最简单的使用莫过于用HttpUtil工具类快速请求某个接口：

``` 
Response response = HttpUtil.get("https://github.com/search", Pair.of("q", "okhttp"));
System.out.println("response = " + response);
```

## POST

一行代码即可搞定，当然Post请求也很简单：

```
        # JSON 格式的body
        Response post = HttpUtil.post("https://oapi.dingtalk.com/robot/send?access_token=27f5954ab60ea8b2e431ae9101b1289c138e85aa6eb6e3940c35ee13ff8b6335", "{\"msgtype\": \"text\",\"text\": {\"content\":\"【反馈提醒】我就是我, 是不一样的烟火\"}}");
        System.out.println("post = " + post);
        
        # form 请求
        Map<String, Object> formParams = new HashMap<>(16);
        formParams.put("username", "admin");
        formParams.put("password", "admin123");
        Response response = HttpUtil.postForm("http://192.168.1.13:9100/auth/login",
                        formParams
        );
        System.out.println("response = " + response);
```

返回格式为JSON的 可以使用 HttpJsonUtil 自动返回JsonObject

```java
        JSONObject object=HttpJsonUtil.get("https://github.com/search",
        Pair.of("q","http"),
        Pair.of("username","agonie201218"));
        System.out.println("object = "+object);
```

## 文件上传

```java
        File file=new File("C:\\Users\\andanyang\\Downloads\\Sql.txt");
        Map<String, Object> formParams=new HashMap<>();
        formParams.put("key","test");
        formParams.put("file",file);
        formParams.put("token","WXyUseb-D4sCum-EvTIDYL-mEehwDtrSBg-Zca7t:qgOcR2gUoKmxt-VnsNb657Oatzo=:eyJzY29wZSI6InpoYW56aGkiLCJkZWFkbGluZSI6MTY2NTMwNzUxNH0=");
        Response response=HttpUtil.upload("https://upload.qiniup.com/",formParams);
        System.out.println(response);
```

## 下载文件

```java
   HttpUtil.down("https://gitee.com/admin4j/common-http","path/");
```

## HttpRequest 链式请求

```java

        # get
        Response response=HttpRequest.get("https://search.gitee.com/?skin=rec&type=repository")
        .queryMap("q","admin4j")
        .header(HttpHeaderKey.USER_AGENT,"admin4j")
        .execute();
        System.out.println("response = "+response);

        # post form
        Response response=HttpRequest.get("http://192.168.1.13:9100/auth/login")
        .queryMap("q","admin4j")
        .header(HttpHeaderKey.USER_AGENT,"admin4j")
        .form("username","admin")
        .form("password","admin123")
        .execute();
        System.out.println("response = "+response);
```

post form 日志

```
16:49:14.092[main]DEBUG io.github.admin4j.http.core.HttpLogger- -->GET http://192.168.1.13:9100/auth/login?q=admin4j http/1.1
16:49:14.094[main]DEBUG io.github.admin4j.http.core.HttpLogger-User-Agent:admin4j
16:49:14.094[main]DEBUG io.github.admin4j.http.core.HttpLogger-Host:192.168.1.13:9100
16:49:14.094[main]DEBUG io.github.admin4j.http.core.HttpLogger-Connection:Keep-Alive
16:49:14.094[main]DEBUG io.github.admin4j.http.core.HttpLogger-Accept-Encoding:gzip
16:49:14.094[main]DEBUG io.github.admin4j.http.core.HttpLogger- -->END GET
16:49:14.670[main]DEBUG io.github.admin4j.http.core.HttpLogger-<--200OK http://192.168.1.13:9100/auth/login?q=admin4j (575ms)
16:49:14.670[main]DEBUG io.github.admin4j.http.core.HttpLogger-transfer-encoding:chunked
16:49:14.670[main]DEBUG io.github.admin4j.http.core.HttpLogger-Vary:Origin
16:49:14.670[main]DEBUG io.github.admin4j.http.core.HttpLogger-Vary:Access-Control-Request-Method
16:49:14.670[main]DEBUG io.github.admin4j.http.core.HttpLogger-Vary:Access-Control-Request-Headers
16:49:14.670[main]DEBUG io.github.admin4j.http.core.HttpLogger-Vary:Origin
16:49:14.670[main]DEBUG io.github.admin4j.http.core.HttpLogger-Vary:Access-Control-Request-Method
16:49:14.670[main]DEBUG io.github.admin4j.http.core.HttpLogger-Vary:Access-Control-Request-Headers
16:49:14.671[main]DEBUG io.github.admin4j.http.core.HttpLogger-Content-Type:application/json;charset=utf-8
16:49:14.671[main]DEBUG io.github.admin4j.http.core.HttpLogger-Date:Tue,08Nov 2022 08:49:14GMT
16:49:14.671[main]DEBUG io.github.admin4j.http.core.HttpLogger-
16:49:14.671[main]DEBUG io.github.admin4j.http.core.HttpLogger-{"code":406,"msg":"Full authentication is required to access this resource"}
16:49:14.671[main]DEBUG io.github.admin4j.http.core.HttpLogger-<--END HTTP(76-byte body)
response=Response{protocol=http/1.1,code=200,message=OK,url=http://192.168.1.13:9100/auth/login?q=admin4j}
```

# 再 Springboot 中使用

maven引入

```xml

<dependency>
    <groupId>io.github.admin4j</groupId>
    <artifactId>common-http-starter</artifactId>
    <version>0.4.0</version>
</dependency>
```

最新版查询 [io.github.admin4j:common-http-starter](io.github.admin4j:common-http-starter)

spring 版可以对 OkHttp进行个性化配置
配置详见

```java
public class HttpConfig {


    /**
     * 日志等级
     */
    private HttpLoggingInterceptor.Level loggLevel = HttpLoggingInterceptor.Level.BODY;

    /**
     * 读取超时时间，秒
     */
    private long readTimeout = 30;
    /**
     * 链接超时时间
     */
    private long connectTimeout = 30;

    private boolean followRedirects = false;

    /**
     * 最大的连接数
     */
    private int maxIdleConnections = 5;

    /**
     * 最大的kepAlive 时间 秒
     */
    private long keepAliveDuration = 5;

    private String userAgent = "OKHTTP";
    /**
     * 是否支持cookie
     */
    private boolean cookie = false;
    private ProxyConfig proxy;


    @Data
    public static class ProxyConfig {

        private Proxy.Type type = Proxy.Type.HTTP;
        private String host;
        private Integer port = 80;
        private String userName;
        private String password;
    }
}

```