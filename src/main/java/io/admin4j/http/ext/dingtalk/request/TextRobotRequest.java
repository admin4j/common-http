package io.admin4j.http.ext.dingtalk.request;

import io.admin4j.http.ext.dingtalk.AbstractRobotRequest;
import io.admin4j.http.ext.dingtalk.core.MsgType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author andanyang
 * @since 2022/5/11 11:56
 */
@Data
public class TextRobotRequest extends AbstractRobotRequest {
    private Map<String, String> text;

    @Override
    public MsgType getMsgType() {
        return MsgType.TEXT;
    }

    public void setContent(String content) {
        if (text == null) {
            text = new HashMap<>(2);
        }
        text.put("content", content);
    }
}
