package com.mastercard.codetest.jerseystore.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * LuaLockRedisLockManager
 */
@Component
public class LuaLockRedisLockManager implements ILockManager {

    @Autowired
    protected StringRedisTemplate redisTemplate;

    // Distribute lock
    protected ILock distributeLock;

    @PostConstruct
    public void init() {
        // init lock
        distributeLock = new LuaDistributeLock(redisTemplate, "mylock_", 5);
    }

    /**
     * lockCallBack
     *
     * @param lockKeyName
     * @param callback
     */
    @Override
    public void lockCallBack(String lockKeyName, SimpleCallBack callback) {
        try {
            // get lock
            distributeLock.lock(lockKeyName);
            callback.execute();
        } finally {
            // release lock
            distributeLock.unlock(lockKeyName);
        }
    }

    /**
     * lockCallBackWithRtn
     *
     * @param lockKeyName
     * @param callback
     */
    @Override
    public <T> T lockCallBackWithRtn(String lockKeyName, ReturnCallBack<T> callback) {
        try {
            // get lock
            distributeLock.lock(lockKeyName);
            return callback.execute();
        } finally {
            // release lock
            distributeLock.unlock(lockKeyName);
        }
    }
}





