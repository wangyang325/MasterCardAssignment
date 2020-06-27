package com.mastercard.codetest.jerseystore.common;

import com.mastercard.codetest.jerseystore.model.Jersey;

/**
 * Utils method class
 */
public class Utils {
    // Permission: view
    public static final String PERMISSION_VIEW = "userInfo:view";
    // Permission: add
    public static final String PERMISSION_ADD = "userInfo:add";
    // Permission: buy
    public static final String PERMISSION_BUY = "userInfo:buy";

    /**
     * initial index
     *
     * @param pJersey : Jersey data;
     * @return key;
     */
    public static String getKey(Jersey pJersey) {
        String key = null;
        // key: brand + club + year + cut + size + type + material
        if (pJersey != null) {
            key = pJersey.getBrand() + "|" + pJersey.getClub() + "|"
                    + pJersey.getYear() + "|" + pJersey.getCut() + "|"
                    + pJersey.getSize() + "|" + pJersey.getType() + "|"
                    + pJersey.getMaterial();
        }
        return key;
    }
}
