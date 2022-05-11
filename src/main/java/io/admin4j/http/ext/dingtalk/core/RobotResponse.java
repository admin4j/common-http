package io.admin4j.http.ext.dingtalk.core;

import lombok.Data;

/**
 * @author andanyang
 * @since 2022/5/11 13:11
 */
@Data
public class RobotResponse {
    private int errcode = -1;
    private String errmsg;
}
