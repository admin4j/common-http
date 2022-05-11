package io.admin4j.http.ext.dingtalk.request;

import io.admin4j.http.ext.dingtalk.AbstractRobotRequest;
import io.admin4j.http.ext.dingtalk.core.MsgType;
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
public class LinkRobotRequest extends AbstractRobotRequest {
    private Map<String, String> link = new HashMap<>(8);

    @Override
    public MsgType getMsgType() {
        return MsgType.LINK;
    }

    /**
     * 消息内容。如果太长只会部分展示。
     *
     * @param text
     * @return
     */
    public LinkRobotRequest setText(String text) {

        link.put("text", text);
        return this;
    }

    /**
     * 消息标题。
     *
     * @param title
     * @return
     */
    public LinkRobotRequest setTitle(String title) {

        link.put("title", title);
        return this;
    }

    /**
     * 图片URL
     *
     * @param picUrl
     * @return
     */
    public LinkRobotRequest setPicUrl(String picUrl) {

        link.put("picUrl", picUrl);
        return this;
    }

    /**
     * 点击消息跳转的URL，打开方式如下：
     * <p>
     * 移动端，在钉钉客户端内打开
     * <p>
     * PC端
     * <p>
     * 默认侧边栏打开
     * <p>
     * 希望在外部浏览器打开，请参考消息链接说明
     *
     * @param messageUrl
     * @return
     */
    public LinkRobotRequest setMessageUrl(String messageUrl) {

        link.put("messageUrl", messageUrl);
        return this;
    }


}
