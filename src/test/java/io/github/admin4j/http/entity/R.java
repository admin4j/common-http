package io.github.admin4j.http.entity;

import lombok.Data;

/**
 * @author andanyang
 * @since 2022/5/10 18:32
 */
@Data
public class R {
    private int code;
    private Object data;
    private String msg;
}