package com.mastercard.codetest.jerseystore.model;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

public enum JerseyMaterial {
    COTTON(1), NYLON(2);

    private final static Map<Integer, JerseyMaterial> map =
            stream(JerseyMaterial.values()).collect(toMap(e -> e.id, e -> e));

    private int id;

    JerseyMaterial(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static JerseyMaterial valueOf(int id) {
        // defaults to `COTTON`
        if (map.containsKey(id) == false) {
            return map.get(1);
        }
        else {
            return map.get(id);
        }
    }
}
