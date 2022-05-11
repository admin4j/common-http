package io.admin4j.http.ext.dingtalk;

import io.admin4j.http.HttpRequest;
import io.admin4j.http.MediaTypeEnum;
import io.admin4j.http.core.Pair;
import io.admin4j.http.exception.HttpException;
import io.admin4j.http.ext.dingtalk.core.RobotResponse;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * https://open.dingtalk.com/document/group/custom-robot-access
 *
 * @author andanyang
 * @since 2022/5/11 11:43
 */
@Data
public class DingRobot extends HttpRequest {

    private Map<String, Object> queryMap;
    private String secret;

    public DingRobot(String accessToken, String secret) {
        super();
        queryMap = new HashMap<>(4);
        this.queryMap.put("access_token", accessToken);
        this.secret = secret;
    }

    public DingRobot(String accessToken) {
        this(accessToken, null);
    }

    public DingRobot() {
        super();
    }

    public void setAccessToken(String accessToken) {
        this.queryMap.put("access_token", accessToken);
    }

    @Override
    public MediaTypeEnum getMediaTypeEnum() {
        return MediaTypeEnum.JSON;
    }

    @Override
    public void init() {
        super.init();
        basePath = "https://oapi.dingtalk.com/robot/send";
    }

    @SneakyThrows
    @Override
    public String buildUrl(String path, Pair<?>[] queryParams, Map<String, Object> queryMap) {

        queryMap = this.queryMap;
        if (StringUtils.isNotBlank(secret)) {
            long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(Base64.getEncoder().encodeToString(signData), "UTF-8");
            queryMap.put("timestamp", timestamp);
            queryMap.put("sign", sign);
        }

        return super.buildUrl(path, queryParams, this.queryMap);
    }

    /**
     * // 消息内容中不包含任何关键词
     * {
     * "errcode":310000,
     * "errmsg":"keywords not in content"
     * }
     * <p>
     * // timestamp 无效
     * {
     * "errcode":310000,
     * "errmsg":"invalid timestamp"
     * }
     * <p>
     * // 签名不匹配
     * {
     * "errcode":310000,
     * "errmsg":"sign not match"
     * }
     * <p>
     * // IP地址不在白名单
     * {
     * "errcode":310000,
     * "errmsg":"ip X.X.X.X not in whitelist"
     * }
     *
     * @param request
     */
    public void send(AbstractRobotRequest request) {

        //response {"errcode":0,"errmsg":"ok"}
        RobotResponse response = post("", request, RobotResponse.class);
        if (response == null || response.getErrcode() != 0) {
            throw new HttpException(response.getErrmsg(), response.getErrcode());
        }
    }
}
