package com.mastercard.codetest.jerseystore.service;

import com.mastercard.codetest.jerseystore.lock.LuaLockRedisLockManager;
import com.mastercard.codetest.jerseystore.lock.ReturnCallBack;
import com.mastercard.codetest.jerseystore.lock.SimpleCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SalesService {

    public Logger logger = LoggerFactory.getLogger(SalesService.class);

    private final String KEY_TOTAL_SALES = "TOTAL:SALES";
    private final String KEY_STOCK = "STOCK:";


    // Redis template
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private LuaLockRedisLockManager luaLockRedisLockManager;

    /**
     * Adds a integer amount to the total sales, and returns the total sales
     *
     * @param id         : product id
     * @param saleAmount The integer amount to add to the total sales
     * @return Sales info
     */
    public String addSale(String id, int saleAmount) {
        int rs = updateAmount(KEY_STOCK + id, saleAmount);
        String logStr = "";
        if (rs == 0) {
            logStr = "Success-> id:" + id + "  sale:" + saleAmount + "   TotalSales:" + getTotalSales();
        } else if (rs == 1) {
            logStr = "Error-> Jersey does not exist. Id:" + id;
        } else if (rs == 2) {
            logStr = "Error-> sale > stock" + "  sale:" + saleAmount;
        } else if (rs == 3) {
            logStr = "Error-> Update failed (STOCK)";
        } else if (rs == 4) {
            logStr = "Error-> Update failed (TOTAL SALES)";
        } else {
            logStr = "Error-> Update failed";
        }
        System.out.println("-----" + logStr);
        return logStr;
    }

    /**
     * Update stock
     *
     * @param id         : product id
     * @param saleAmount The integer amount to add to the total sales
     * @return result
     */
    @Transactional(timeout = 5)
    public int updateAmount(String id, int saleAmount) {
        int rs = 0;
        try {
            rs = luaLockRedisLockManager.lockCallBackWithRtn(id, new ReturnCallBack<Integer>() {
                @Override
                public Integer execute() {
                    String value = stringRedisTemplate.opsForValue().get(id);
                    int nowAmount = 0;
                    if (value != null) {
                        try {
                            nowAmount = Integer.valueOf(value);
                        } catch (Exception e) {
                            return 1;
                        }
                    } else {
                        return 1;
                    }

                    if (saleAmount > nowAmount) {
                        return 2;
                    } else {
                        try {
                            stringRedisTemplate.opsForValue().set(id, nowAmount - saleAmount + "");
                        } catch (Exception e) {
                            return 3;
                        }
                        if (updateTotalSales(KEY_TOTAL_SALES, saleAmount) == false) {
                            return 4;
                        }
                    }
                    return 0;
                }
            });
        } catch (Exception e) {
            rs = 5;
        }
        return rs;
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
    @Transactional(timeout = 5)
    public boolean updateTotalSales(String key, int amount) {
        try {
            luaLockRedisLockManager.lockCallBack(key, new SimpleCallBack() {
                @Override
                public void execute() {
                    String value = stringRedisTemplate.opsForValue().get(key);
                    if (value != null) {
                        try {
                            int nowV = Integer.valueOf(value);
                            stringRedisTemplate.opsForValue().set(key, amount + nowV + "");
                        } catch (Exception e) {
                            throw e;
                        }
                    } else {
                        stringRedisTemplate.opsForValue().set(key, amount + "");
                    }
                }
            });
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
