package com.mastercard.codetest.jerseystore.model;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

public enum JerseySize {
    S(1), M(2), L(3);

    private final static Map<Integer, JerseySize> map =
            stream(JerseySize.values()).collect(toMap(e -> e.id, e -> e));

    private int id;

    JerseySize(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static JerseySize valueOf(int id) {
        return map.get(id);
    }
}
