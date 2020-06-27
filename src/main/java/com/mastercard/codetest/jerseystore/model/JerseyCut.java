package com.mastercard.codetest.jerseystore.model;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Jersey cut:
 *
 *        MALE     |      FEMALE
 *
 *      __   __          __    __
 *     /  `-’  \        /  \  /  \
 *    /_|     |_\      /_|  v   |_\
 *      |     |          |      |
 *      |     |          |      |
 *      |_____|          |______|
 *
 */
public enum JerseyCut {
    MALE(1), FEMALE(2);

    private final static Map<Integer, JerseyCut> map =
            stream(JerseyCut.values()).collect(toMap(e -> e.id, e -> e));

    private int id;

    JerseyCut(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static JerseyCut valueOf(int id) {
        return map.get(id);
    }
}
