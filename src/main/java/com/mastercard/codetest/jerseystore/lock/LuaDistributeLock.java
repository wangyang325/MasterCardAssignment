package com.mastercard.codetest.jerseystore.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * LuaDistributeLock
 */
public class LuaDistributeLock implements ILock {
    // Lock expiration time : 5 second
    private static final int LOCK_MAX_EXIST_TIME = 5;
    // Pre-name for lock
    private static final String LOCK_PREX = "lock_";

    @Autowired
    private StringRedisTemplate redisTemplate;
    // Pre-name for lock
    private String lockPrex;
    // Lock time
    private int lockMaxExistTime;
    // Lock lua script
    private DefaultRedisScript<Long> lockScript;
    // Unlock lua script
    private DefaultRedisScript<Long> unlockScript;

    // Thread Key
    private ThreadLocal<String> threadKeyId = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return UUID.randomUUID().toString();
        }
    };

    /**
     * Constructor
     *
     * @param redisTemplate
     */
    public LuaDistributeLock(StringRedisTemplate redisTemplate) {
        this(redisTemplate, LOCK_PREX, LOCK_MAX_EXIST_TIME);
    }

    /**
     * Constructor
     *
     * @param redisTemplate
     * @param lockPrex
     * @param lockMaxExistTime
     */
    public LuaDistributeLock(StringRedisTemplate redisTemplate, String lockPrex, int lockMaxExistTime) {
        this.redisTemplate = redisTemplate;
        this.lockPrex = lockPrex;
        this.lockMaxExistTime = lockMaxExistTime;
        // init
        init();
    }

    /**
     * init
     */
    public void init() {
        // Lock script
        lockScript = new DefaultRedisScript<Long>();
        lockScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("lua/lock.lua")));
        lockScript.setResultType(Long.class);
        // unlock script
        unlockScript = new DefaultRedisScript<Long>();
        unlockScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("lua/unlock.lua")));
        unlockScript.setResultType(Long.class);
    }

    /**
     * Lock
     *
     * @param lock2
     */
    @Override
    public void lock(String lock2) {
        String lockKey = getLockKey(lock2);
        while (true) {
            List<String> keyList = new ArrayList<String>();
            keyList.add(lockKey);
            keyList.add(threadKeyId.get());
            if (redisTemplate.execute(lockScript, keyList, String.valueOf(lockMaxExistTime * 1000)) > 0) {
                break;
            } else {
                try {
                    // avoid dead lock
                    Thread.sleep(10, (int) (Math.random() * 500));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * unlock
     *
     * @param lock
     */
    @Override
    public void unlock(final String lock) {
        final String lockKey = getLockKey(lock);
        List<String> keyList = new ArrayList<String>();
        keyList.add(lockKey);
        keyList.add(threadKeyId.get());
        redisTemplate.execute(unlockScript, keyList);
    }

    /**
     * Make lock key
     *
     * @param lock
     * @return
     */
    private String getLockKey(String lock) {
        StringBuilder sb = new StringBuilder();
        sb.append(lockPrex).append(lock);
        return sb.toString();
    }
}
