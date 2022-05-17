package io.github.admin4j.http.ext.dingtalk.core;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author andanyang
 * @since 2022/5/11 11:54
 */
@Data
public class At {
    private List<String> atMobiles;
    private List<String> atUserIds;
    private boolean isAtAll = false;

    public static At atMobiles(List<String> atMobiles) {
        At at = new At();
        at.atMobiles = atMobiles;
        return at;
    }

    public static At atMobile(String atMobile) {
        At at = new At();
        at.atMobiles = Collections.singletonList(atMobile);
        return at;
    }

    public static At atUserIds(List<String> atUserIds) {
        At at = new At();
        at.atUserIds = atUserIds;
        return at;
    }

    public static At atUserId(String atUserId) {
        At at = new At();
        at.atUserIds = Collections.singletonList(atUserId);
        return at;
    }

    public static At atAll() {
        At at = new At();
        at.setAtAll(true);
        return at;
    }
}
