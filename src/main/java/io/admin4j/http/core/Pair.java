package io.admin4j.http.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author andanyang
 * @since 2022/5/10 11:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pair<T> implements Serializable {
    private static final long serialVersionUID = -8127748275809834416L;
    private String name;
    private T value;

    public static Pair of(String name, Object value) {
        return new Pair(name, value);
    }

    private boolean isValidString(String arg) {
        if (arg == null) {
            return false;
        }
        if (arg.trim().isEmpty()) {
            return false;
        }

        return true;
    }
}
