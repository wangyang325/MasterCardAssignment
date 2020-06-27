package com.mastercard.codetest.jerseystore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesService {

    public Logger logger = LoggerFactory.getLogger(SalesService.class);

    // Redis template
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Adds a integer amount to the total sales, and returns the total sales
     *
     * @param id : product id
     * @param saleAmount The integer amount to add to the total sales
     * @return Sales info
     */
    public String addSale(String id, int saleAmount) {
        int nowAmount = getAmount(id.trim());
        if (nowAmount == -1) {
            return "Error-> Jersey does not exist. Id:" + id;
        }
        if (saleAmount > nowAmount) {
            return "Error-> stock:" + nowAmount + "  sale:" + saleAmount;
        } else {
            updateAmount(id.trim(), nowAmount - saleAmount);
            updateTotalSales(saleAmount);
        }
        return "Success-> id:" + id + "  sale:" + saleAmount + "  remain:" + (nowAmount - saleAmount) + "   TotalSales:" + getTotalSales();
    }

    /**
     * Update stock
     *
     * @param id : product id
     * @param amount The integer amount to add to the total sales
     * @return result
     */
    @Transactional
    public boolean updateAmount(String id, int amount) {
        try {
            stringRedisTemplate.opsForValue().set("STOCK:" + id, amount + "");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Get stock
     *
     * @param id : product id
     * @return result
     */
    public int getAmount(String id) {
        String value = stringRedisTemplate.opsForValue().get("STOCK:" + id);
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Get total sales
     *
     * @return sales amount
     */
    public int getTotalSales() {
        String value = stringRedisTemplate.opsForValue().get("TOTAL:SALES");
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * Update total sales
     *
     * @param amount : int
     * @return result
     */
    @Transactional
    public boolean updateTotalSales(int amount) {
        String value = stringRedisTemplate.opsForValue().get("TOTAL:SALES");
        if (value != null) {
            try {
                int nowV = Integer.valueOf(value);
                stringRedisTemplate.opsForValue().set("TOTAL:SALES", amount + nowV + "");
            } catch (Exception e) {
                return false;
            }
        } else {
            stringRedisTemplate.opsForValue().set("TOTAL:SALES", amount + "");
        }
        return true;
    }
}
