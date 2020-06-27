package com.mastercard.codetest.jerseystore.service;

import com.mastercard.codetest.jerseystore.dao.UserInfoDao;
import com.mastercard.codetest.jerseystore.entity.UserInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User info service
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    UserInfoDao userInfoDao;

    /**
     * Get user info by name
     *
     * @param username : String;
     * @return UserInfo;
     */
    @Override
    public UserInfo findByUsername(String username) {
        return userInfoDao.findByUsername(username);
    }
}

