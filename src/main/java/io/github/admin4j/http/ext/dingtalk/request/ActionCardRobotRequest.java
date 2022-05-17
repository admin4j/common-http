package io.github.admin4j.http.ext.dingtalk.request;

import io.github.admin4j.http.ext.dingtalk.AbstractRobotRequest;
import io.github.admin4j.http.ext.dingtalk.core.MsgType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/4099076061/p131227.png
 *
 * @author andanyang
 * @since 2022/5/11 11:56
 */
@Data
public class ActionCardRobotRequest extends AbstractRobotRequest {
    private Map<String, Object> actionCard = new HashMap<>(8);

    @Override
    public MsgType getMsgType() {
        return MsgType.ACTION_CARD;
    }

    /**
     * 消息内容。如果太长只会部分展示。
     *
     * @param text
     * @return
     */
    public ActionCardRobotRequest setText(String text) {

        actionCard.put("text", text);
        return this;
    }

    /**
     * 消息标题。
     *
     * @param title
     * @return
     */
    public ActionCardRobotRequest setTitle(String title) {

        actionCard.put("title", title);
        return this;
    }

    /**
     * 单个按钮的标题。
     * <p>
     * 注意 设置此项和singleURL后，btns无效。
     *
     * @param singleTitle
     * @return
     */
    public ActionCardRobotRequest setSingleTitle(String singleTitle) {

        actionCard.put("singleTitle", singleTitle);
        return this;
    }

    /**
     * 点击消息跳转的URL，打开方式如下：
     * 移动端，在钉钉客户端内打开
     * <p>
     * PC端
     * <p>
     * 默认侧边栏打开
     * <p>
     * 希望在外部浏览器打开，请参考消息链接说明
     *
     * @param singleURL
     * @return
     */
    public ActionCardRobotRequest setSingleURL(String singleURL) {

        actionCard.put("singleURL", singleURL);
        return this;
    }

    /**
     * 0：按钮竖直排列
     * <p>
     * 1：按钮横向排列
     *
     * @param isHorizontal
     * @return
     */
    public ActionCardRobotRequest setBtnOrientation(boolean isHorizontal) {

        actionCard.put("btnOrientation", isHorizontal ? "1" : "0");
        return this;
    }


    /**
     * "btns": [
     * {
     * "title": "内容不错",
     * "actionURL": "https://www.dingtalk.com/"
     * },
     * {
     * "title": "不感兴趣",
     * "actionURL": "https://www.dingtalk.com/"
     * }
     * ]
     *
     * @param btns
     * @return
     */
    public ActionCardRobotRequest setBtns(List<Map<String, String>> btns) {

        actionCard.put("btns", btns);
        return this;
    }


    public ActionCardRobotRequest addBtn(String title, String actionURL) {

        if (!actionCard.containsKey("btns")) {
            actionCard.put("btns", new ArrayList<Map<String, String>>(4));
        }
        ArrayList<Map<String, String>> btns = (ArrayList<Map<String, String>>) actionCard.get("btns");

        btns.add(new HashMap<String, String>(4) {
            {
                put("title", title);
                put("actionURL", actionURL);
            }
        });
        return this;
    }


}
