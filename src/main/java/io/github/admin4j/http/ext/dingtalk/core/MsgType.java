package io.github.admin4j.http.ext.dingtalk.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author andanyang
 * @since 2022/5/11 11:48
 */
@AllArgsConstructor
@Getter
public enum MsgType {
    /**
     * 文本
     */
    TEXT("text"),
    /**
     * 链接
     */
    LINK("link"),
    MARKDOWN("markdown"),
    ACTION_CARD("actionCard"),
    FEED_CARD("feedCard"),

    ;

    private String value;

    @Override
    public String toString() {
        return value;
    }
}
