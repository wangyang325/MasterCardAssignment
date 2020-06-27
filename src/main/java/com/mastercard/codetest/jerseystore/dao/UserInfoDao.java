package com.mastercard.codetest.jerseystore.dao;

import com.mastercard.codetest.jerseystore.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserInfoDao interface
 */
public interface UserInfoDao extends JpaRepository<UserInfo, Long> {
    /**
     * Get user info
     *
     * @param username : String;
     * @return UserInfo;
     */
    UserInfo findByUsername(String username);
}
