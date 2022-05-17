package io.github.admin4j.http.ext.dingtalk;

import io.github.admin4j.http.ext.dingtalk.core.At;
import io.github.admin4j.http.ext.dingtalk.request.*;
import org.junit.jupiter.api.Test;

/**
 * @author andanyang
 * @since 2022/5/11 12:42
 */
class DingRobotTest {

    @Test
    void send() {
        DingRobot dingRobot = new DingRobot("37f5954ab60ea8b2e431ae9101b1289c138e85aa6eb6e3940c35ee13ff8b6335");
        TextRobotRequest textRobotRequest = new TextRobotRequest();
        textRobotRequest.setContent("【反馈提醒】我就是我,");
        dingRobot.send(textRobotRequest);
    }

    @Test
    void sendLink() {
        DingRobot dingRobot = new DingRobot("37f5954ab60ea8b2e431ae9101b1289c138e85aa6eb6e3940c35ee13ff8b6335");
        LinkRobotRequest request = new LinkRobotRequest();
        request.setText("【反馈提醒】我就是我,");
        request.setTitle("【反馈提醒】时代的火车向前开,");
        request.setPicUrl("https://pics0.baidu.com/feed/32fa828ba61ea8d3b04aed0c512dc349251f5879.jpeg?token=63be25d2f3b5411643d6d1f3c6661016&s=622A9645C4125BCC2886BEF80300101C");
        request.setMessageUrl("https://open.dingtalk.com/document/group/custom-robot-access");

        dingRobot.send(request);
    }

    @Test
    void sendMD() {
        DingRobot dingRobot = new DingRobot("37f5954ab60ea8b2e431ae9101b1289c138e85aa6eb6e3940c35ee13ff8b6335");
        MarkdownRobotRequest request = new MarkdownRobotRequest();
        request.setTitle("【反馈提醒】杭州天气");
        request.setText("#### 杭州天气 @15757856602 \n > 9度，西北风1级，空气良89，相对温度73% \n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png) \n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n");
        request.setAt(At.atMobile("15757856602"));
        dingRobot.send(request);
    }

    @Test
    void sendActionCard() {
        DingRobot dingRobot = new DingRobot("37f5954ab60ea8b2e431ae9101b1289c138e85aa6eb6e3940c35ee13ff8b6335");
        ActionCardRobotRequest request = new ActionCardRobotRequest();
        request.setTitle("【反馈提醒】时代的火车向前开 sendActionCard");
        request.setText("#### 杭州天气 @15757856602 \n > 9度，西北风1级，空气良89，相对温度73% \n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png) \n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n");
        request.setAt(At.atMobile("15757856602"));
        request.addBtn("内容不错", "https://www.dingtalk.com/");
        request.addBtn("不感兴趣", "https://open.dingtalk.com/document/group/custom-robot-access");
        dingRobot.send(request);
    }

    @Test
    void sendFeedCard() {
        DingRobot dingRobot = new DingRobot("37f5954ab60ea8b2e431ae9101b1289c138e85aa6eb6e3940c35ee13ff8b6335", "SEC51ec7811e8cafb09cf59926c070ae9cf79f158415b4e839cc7d08d9f95d82c36");
        FeedCardRobotRequest request = new FeedCardRobotRequest();
        request.addLink("【反馈提醒】时代的火车向前开1", "https://www.dingtalk.com/", "https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png");
        request.addLink("【反馈提醒】时代的火车向前开1  @15757856602 ", "https://www.dingtalk.com/", "https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png");
        //request.setText("#### 杭州天气 @15757856602 \n > 9度，西北风1级，空气良89，相对温度73% \n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png) \n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n");
        request.setAt(At.atMobile("15757856602"));

        dingRobot.send(request);
    }
}