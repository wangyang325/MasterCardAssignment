package com.mastercard.codetest.jerseystore.lock;

/**
 * ReturnCallBack
 *
 * @param <T>
 */
public interface ReturnCallBack<T> {
    T execute();
}
