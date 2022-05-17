package io.github.admin4j.http.ext.dingtalk.request;

import io.github.admin4j.http.ext.dingtalk.AbstractRobotRequest;
import io.github.admin4j.http.ext.dingtalk.core.MsgType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/4099076061/p131227.png
 *
 * @author andanyang
 * @since 2022/5/11 11:56
 */
@Data
public class MarkdownRobotRequest extends AbstractRobotRequest {
    private Map<String, String> markdown = new HashMap<>(8);

    @Override
    public MsgType getMsgType() {
        return MsgType.MARKDOWN;
    }

    /**
     * 消息内容。如果太长只会部分展示。
     *
     * @param text
     * @return
     */
    public MarkdownRobotRequest setText(String text) {

        markdown.put("text", text);
        return this;
    }

    /**
     * 消息标题。
     *
     * @param title
     * @return
     */
    public MarkdownRobotRequest setTitle(String title) {

        markdown.put("title", title);
        return this;
    }
}
