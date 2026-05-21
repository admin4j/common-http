package io.github.admin4j.http.curl;

import io.github.admin4j.http.HttpRequest;
import io.github.admin4j.http.util.core.MediaTypeEnum;
import io.github.admin4j.http.util.core.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * curl 命令字符串解析器，将一段 curl 命令转换为 {@link HttpRequest} 构建器实例。
 *
 * <h3>支持的 curl 选项</h3>
 * <ul>
 *   <li>{@code -X / --request}</li>
 *   <li>{@code -H / --header}</li>
 *   <li>{@code -d / --data / --data-raw / --data-binary / --data-urlencode}</li>
 *   <li>{@code -F / --form}（包含 {@code @file} 文件上传语法）</li>
 *   <li>{@code -u / --user}（生成 Basic Auth 头）</li>
 *   <li>{@code -G / --get}（将 -d 数据并入 query string）</li>
 *   <li>{@code --url}</li>
 *   <li>{@code -A / --user-agent}、{@code -e / --referer}</li>
 *   <li>{@code -b / --cookie}（添加 Cookie 头）</li>
 *   <li>无副作用的开关：{@code --compressed}、{@code -k}、{@code -s}、{@code -v}、{@code -L}、{@code -i}、{@code -I}（忽略）</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * String cmd = "curl -X POST 'https://api.example.com/users' \\\n" +
 *              "  -H 'Content-Type: application/json' \\\n" +
 *              "  -d '{\"name\":\"admin\"}'";
 *
 * HttpResponse resp = CurlParser.parse(cmd).execute();
 * // 或：HttpResponse resp = Http.curl(cmd).execute();
 * }</pre>
 *
 * <h3>解析行为</h3>
 * <ul>
 *   <li>支持单引号、双引号以及反斜杠续行 {@code "\\\n"}</li>
 *   <li>多个 {@code -d} 会按 curl 行为以 {@code &} 拼接</li>
 *   <li>{@code -G} 模式下 -d 数据会自动转入 query 参数</li>
 *   <li>未识别的选项会被跳过，并在日志中给出 WARN 提示</li>
 * </ul>
 *
 * @author admin4j
 * @since 0.10.0
 */
public final class CurlParser {

    private static final Logger log = LoggerFactory.getLogger(CurlParser.class);

    private CurlParser() {
        // utility class, no instantiation
    }

    /**
     * 解析 curl 命令字符串为 {@link HttpRequest} 构建器。
     *
     * @param curl curl 命令字符串（可包含换行续行符）
     * @return 已根据 curl 选项填充的 HttpRequest 实例
     * @throws IllegalArgumentException 当输入为空或未找到 URL 时抛出
     */
    public static HttpRequest parse(String curl) {
        if (curl == null || curl.trim().isEmpty()) {
            throw new IllegalArgumentException("curl command must not be null or empty");
        }
        List<String> tokens = tokenize(curl);
        return parseTokens(tokens);
    }

    // ======================== Tokenizer ========================

    /**
     * 类 shell 风格分词：处理单引号、双引号、反斜杠转义与行续接。
     */
    static List<String> tokenize(String curl) {
        List<String> tokens = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;
        boolean hasToken = false;
        int len = curl.length();

        for (int i = 0; i < len; i++) {
            char c = curl.charAt(i);

            if (inSingle) {
                if (c == '\'') {
                    inSingle = false;
                } else {
                    cur.append(c);
                }
                continue;
            }

            if (inDouble) {
                if (c == '\\' && i + 1 < len) {
                    char next = curl.charAt(i + 1);
                    // 双引号中仅这几种转义有特殊意义；其余原样保留反斜杠
                    if (next == '"' || next == '\\' || next == '$' || next == '`' || next == '\n') {
                        if (next != '\n') {
                            cur.append(next);
                        }
                        i++;
                    } else {
                        cur.append(c);
                    }
                } else if (c == '"') {
                    inDouble = false;
                } else {
                    cur.append(c);
                }
                continue;
            }

            if (c == '\'') {
                inSingle = true;
                hasToken = true;
                continue;
            }
            if (c == '"') {
                inDouble = true;
                hasToken = true;
                continue;
            }
            if (c == '\\' && i + 1 < len) {
                char next = curl.charAt(i + 1);
                if (next == '\n') {
                    // 行续接：跳过 "\n"
                    i++;
                    continue;
                }
                if (next == '\r') {
                    i++;
                    if (i + 1 < len && curl.charAt(i + 1) == '\n') {
                        i++;
                    }
                    continue;
                }
                // 其它转义直接保留下一个字符
                cur.append(next);
                i++;
                hasToken = true;
                continue;
            }
            if (Character.isWhitespace(c)) {
                if (hasToken) {
                    tokens.add(cur.toString());
                    cur.setLength(0);
                    hasToken = false;
                }
                continue;
            }
            cur.append(c);
            hasToken = true;
        }
        if (hasToken) {
            tokens.add(cur.toString());
        }
        return tokens;
    }

    // ======================== Token Parser ========================

    private static HttpRequest parseTokens(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Empty curl command");
        }

        // 跳过命令前缀 "curl"
        int start = 0;
        if ("curl".equalsIgnoreCase(tokens.get(0))) {
            start = 1;
        }

        String url = null;
        Method method = null;
        boolean useGet = false;
        List<String[]> headers = new ArrayList<>();
        List<String> dataRaw = new ArrayList<>();
        List<String> dataUrlencoded = new ArrayList<>();
        List<String[]> formList = new ArrayList<>();
        String basicAuth = null;
        String userAgent = null;
        String referer = null;
        String cookie = null;
        boolean hasFormFlag = false;

        for (int i = start; i < tokens.size(); i++) {
            String t = tokens.get(i);
            switch (t) {
                case "-X":
                case "--request":
                    method = Method.valueOf(requireValue(tokens, ++i, t).toUpperCase());
                    break;
                case "-H":
                case "--header": {
                    String h = requireValue(tokens, ++i, t);
                    int idx = h.indexOf(':');
                    if (idx > 0) {
                        headers.add(new String[]{h.substring(0, idx).trim(), h.substring(idx + 1).trim()});
                    }
                    break;
                }
                case "-d":
                case "--data":
                case "--data-raw":
                case "--data-binary":
                    dataRaw.add(requireValue(tokens, ++i, t));
                    break;
                case "--data-urlencode":
                    dataUrlencoded.add(requireValue(tokens, ++i, t));
                    break;
                case "-F":
                case "--form": {
                    String f = requireValue(tokens, ++i, t);
                    int idx = f.indexOf('=');
                    if (idx > 0) {
                        formList.add(new String[]{f.substring(0, idx), f.substring(idx + 1)});
                        hasFormFlag = true;
                    }
                    break;
                }
                case "-u":
                case "--user":
                    basicAuth = requireValue(tokens, ++i, t);
                    break;
                case "-G":
                case "--get":
                    useGet = true;
                    break;
                case "--url":
                    url = requireValue(tokens, ++i, t);
                    break;
                case "-A":
                case "--user-agent":
                    userAgent = requireValue(tokens, ++i, t);
                    break;
                case "-e":
                case "--referer":
                    referer = requireValue(tokens, ++i, t);
                    break;
                case "-b":
                case "--cookie":
                    cookie = requireValue(tokens, ++i, t);
                    break;
                case "--compressed":
                case "-k":
                case "--insecure":
                case "-L":
                case "--location":
                case "-s":
                case "--silent":
                case "-S":
                case "--show-error":
                case "-v":
                case "--verbose":
                case "-i":
                case "--include":
                case "-I":
                case "--head":
                case "-f":
                case "--fail":
                    // 忽略：对请求构建无影响
                    break;
                default:
                    if (t.startsWith("--")) {
                        log.warn("CurlParser: ignoring unsupported long option '{}'", t);
                        if (i + 1 < tokens.size() && !tokens.get(i + 1).startsWith("-")) {
                            i++;
                        }
                        break;
                    }
                    if (t.startsWith("-") && t.length() > 1) {
                        log.warn("CurlParser: ignoring unsupported short option '{}'", t);
                        if (i + 1 < tokens.size() && !tokens.get(i + 1).startsWith("-")) {
                            i++;
                        }
                        break;
                    }
                    // 位置参数：URL（取第一个）
                    if (url == null) {
                        url = t;
                    }
            }
        }

        if (url == null) {
            throw new IllegalArgumentException("URL not found in curl command");
        }

        // 推断 method
        if (method == null) {
            if (useGet) {
                method = Method.GET;
            } else if (!dataRaw.isEmpty() || !dataUrlencoded.isEmpty() || !formList.isEmpty()) {
                method = Method.POST;
            } else {
                method = Method.GET;
            }
        }

        HttpRequest req = HttpRequest.of(url, method);

        // headers
        String contentType = null;
        for (String[] h : headers) {
            req.header(h[0], h[1]);
            if ("content-type".equalsIgnoreCase(h[0])) {
                contentType = h[1];
            }
        }
        if (userAgent != null) {
            req.userAgent(userAgent);
        }
        if (referer != null) {
            req.referer(referer);
        }
        if (cookie != null) {
            req.header("Cookie", cookie);
        }
        if (basicAuth != null) {
            String encoded = Base64.getEncoder().encodeToString(basicAuth.getBytes(StandardCharsets.UTF_8));
            req.header("Authorization", "Basic " + encoded);
        }

        // multipart 优先
        if (hasFormFlag) {
            for (String[] f : formList) {
                String name = f[0];
                String value = f[1];
                if (value.startsWith("@")) {
                    req.multipart(name, new File(value.substring(1)));
                } else {
                    req.multipart(name, value);
                }
            }
            return req;
        }

        // data 处理
        List<String> allData = new ArrayList<>(dataRaw);
        for (String item : dataUrlencoded) {
            allData.add(urlencodeItem(item));
        }
        if (allData.isEmpty()) {
            return req;
        }

        String joined = String.join("&", allData);

        if (useGet || method == Method.GET) {
            // -G 模式下 -d 数据转为 query
            for (String pair : joined.split("&")) {
                if (pair.isEmpty()) {
                    continue;
                }
                int idx = pair.indexOf('=');
                if (idx > 0) {
                    req.query(pair.substring(0, idx), decode(pair.substring(idx + 1)));
                } else {
                    req.query(pair, "");
                }
            }
            return req;
        }

        MediaTypeEnum mt = inferMediaType(contentType, joined);
        req.mediaType(mt).body(joined);
        return req;
    }

    // ======================== Helpers ========================

    private static String requireValue(List<String> tokens, int idx, String flag) {
        if (idx >= tokens.size()) {
            throw new IllegalArgumentException("Missing value for option '" + flag + "'");
        }
        return tokens.get(idx);
    }

    /**
     * 处理 {@code --data-urlencode} 的 5 种格式：
     * <pre>
     *   content
     *   =content
     *   name=content
     *   @file
     *   name@file （文件读取暂不支持，当作 name=@file 字面量）
     * </pre>
     */
    private static String urlencodeItem(String item) {
        if (item == null) {
            return "";
        }
        int eq = item.indexOf('=');
        if (eq < 0) {
            return urlencode(item);
        }
        String name = item.substring(0, eq);
        String value = item.substring(eq + 1);
        if (name.isEmpty()) {
            return urlencode(value);
        }
        return name + "=" + urlencode(value);
    }

    private static String urlencode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    private static String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    private static MediaTypeEnum inferMediaType(String contentType, String data) {
        if (contentType != null) {
            String ct = contentType.toLowerCase();
            if (ct.contains("application/json")) {
                return MediaTypeEnum.JSON;
            }
            if (ct.contains("application/x-www-form-urlencoded")) {
                return MediaTypeEnum.FORM;
            }
            if (ct.contains("multipart/form-data")) {
                return MediaTypeEnum.FORM_DATA;
            }
            if (ct.contains("application/xml") || ct.contains("text/xml")) {
                return MediaTypeEnum.XML;
            }
            if (ct.contains("text/plain")) {
                return MediaTypeEnum.PLAIN;
            }
            if (ct.contains("text/html")) {
                return MediaTypeEnum.HTML;
            }
            if (ct.contains("application/octet-stream")) {
                return MediaTypeEnum.OCTET_STREAM;
            }
        }
        // 启发式：以 { 或 [ 开头视为 JSON
        String trimmed = data == null ? "" : data.trim();
        if ((trimmed.startsWith("{") && trimmed.endsWith("}"))
                || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return MediaTypeEnum.JSON;
        }
        return MediaTypeEnum.FORM;
    }
}
