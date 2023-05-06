package http.entity;

import lombok.Data;

/**
 * @author andanyang
 * @since 2023/5/5 16:45
 */
@Data
public class CsdnR {
    private int code;
    private Object data;
    private String message;
    private String traceId;
}
