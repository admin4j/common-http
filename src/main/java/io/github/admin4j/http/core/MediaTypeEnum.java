package io.github.admin4j.http.core;

import lombok.Getter;
import okhttp3.MediaType;
import org.apache.commons.lang3.StringUtils;

/**
 * @author andanyang
 * @Date 2021/6/2 17:21
 */
public enum MediaTypeEnum {
    /**
     * Content Type 为JSON
     */
    JSON(MediaType.parse("application/json;charset=utf-8")),
    FORM(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8")),
    FORM_DATA(MediaType.parse("multipart/form-data")),
    XML(MediaType.parse("text/xml;charset=UTF-8")),
    OTHER(null);


    /**
     * Content Type 类型
     */
    @Getter
    private final MediaType mediaType;

    MediaTypeEnum(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public static MediaTypeEnum of(Object contentType) {
        if (contentType instanceof MediaTypeEnum) {
            return (MediaTypeEnum) contentType;
        } else if (contentType instanceof String) {
            String contentStr = (String) contentType;
            //application/json;charset=utf-8 application/json;
            contentType = StringUtils.substringBefore(contentStr, ";");
            String type = StringUtils.substringBefore(contentStr, "/");
            String subType = StringUtils.substringAfter(contentStr, "/");
            for (MediaTypeEnum mediaType : MediaTypeEnum.values()) {
                if (mediaType.getMediaType().type().equals(type) && mediaType.getMediaType().subtype().equals(subType)) {
                    return mediaType;
                }
            }
        }

        return MediaTypeEnum.OTHER;
    }

    @Override
    public String toString() {
        return mediaType.toString();
    }
}
