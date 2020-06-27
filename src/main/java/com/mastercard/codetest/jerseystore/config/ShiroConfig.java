package com.mastercard.codetest.jerseystore.config;

import com.mastercard.codetest.jerseystore.shiro.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;

/**
 * Shiro Configuration
 */
@Configuration
public class ShiroConfig {

    @Value("${shiro.user.loginUrl}")
    public String loginUrl;
    @Value("${shiro.user.unauthorizedUrl}")
    private String unanthorizedUrl;

    /**
     * Shiro filter
     *
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // Shiro securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // user authority failed to this page
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        // permission authority failed to this page
        shiroFilterFactoryBean.setUnauthorizedUrl(unanthorizedUrl);
        // Shiro filter configuration ; anon: can be accessed by anyone
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // -------------------------------------------------------------------
        // static resource
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/templates/**", "anon");
        // url
        filterChainDefinitionMap.put("/jersey/login", "anon");
        filterChainDefinitionMap.put("/jersey/403", "anon");
        filterChainDefinitionMap.put("/jersey/doLogin", "anon");
        filterChainDefinitionMap.put("/jersey/404", "anon");
        filterChainDefinitionMap.put("/jersey/sql", "anon");

        // All request need authority
        filterChainDefinitionMap.put("/**", "user");
        // -------------------------------------------------------------------
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * SecurityManager
     *
     * @param userRealm
     * @return SecurityManager
     */
    @Bean
    public SecurityManager securityManager(UserRealm userRealm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(userRealm);
        return defaultWebSecurityManager;
    }

    /**
     * customized Realm
     * EhCacheManager cacheManager
     *
     * @return UserRealm
     */
    @Bean
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        return userRealm;
    }

    /**
     * open Shiro annotation
     *
     * @return LifecycleBeanPostProcessor
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * DefaultAdvisorAutoProxyCreator
     *
     * @return DefaultAdvisorAutoProxyCreator
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * AuthorizationAttributeSourceAdvisor
     *
     * @param securityManager
     * @return AuthorizationAttributeSourceAdvisor
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
