package com.mastercard.codetest.jerseystore.service;

import com.mastercard.codetest.jerseystore.entity.UserInfo;

/**
 * User info service
 */
public interface UserInfoService {
    /**
     * Get user info by name
     *
     * @param username : String;
     * @return UserInfo;
     */
    UserInfo findByUsername(String username);
}
