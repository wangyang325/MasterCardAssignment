package com.mastercard.codetest.jerseystore.lock;

/**
 * Lock manager interface
 */
public interface ILockManager {
    /**
     * Lock and Callback: no return
     *
     * @param lockKeyName
     * @param callback
     */
    void lockCallBack(String lockKeyName, SimpleCallBack callback);

    /**
     * Lock and Callback: has return
     *
     * @param lockKeyName
     * @param callback
     * @return
     */
    <T> T lockCallBackWithRtn(String lockKeyName, ReturnCallBack<T> callback);
}
