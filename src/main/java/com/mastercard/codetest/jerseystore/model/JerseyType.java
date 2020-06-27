package com.mastercard.codetest.jerseystore.model;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

public enum JerseyType {
    HOME(1), Away(2);

    private final static Map<Integer, JerseyType> map =
            stream(JerseyType.values()).collect(toMap(e -> e.id, e -> e));

    private int id;

    JerseyType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static JerseyType valueOf(int id) {
        return map.get(id);
    }
}
