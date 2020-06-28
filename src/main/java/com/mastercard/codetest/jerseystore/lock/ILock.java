package com.mastercard.codetest.jerseystore.lock;

/**
 * Lock interface
 */
public interface ILock {
    /**
     * lock
     *
     * @param lock key
     */
    void lock(String lock);

    /**
     * unlock
     *
     * @param lock key
     */
    void unlock(String lock);
}
