package com.mastercard.codetest.jerseystore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Redis Configuration
 */
@Configuration
public class RedisConfig {

    /**
     * StringRedisTemplate
     *
     * @param redisConnectionFactory : RedisConnectionFactory;
     * @return StringRedisTemplate;
     */
    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        // open transaction
        template.setEnableTransactionSupport(true);
        return template;
    }

    /**
     * PlatformTransactionManager
     *
     * @param dataSource : DataSource;
     * @return StringRedisTemplate;
     */
    // transactionManager
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) throws SQLException {
        return new DataSourceTransactionManager(dataSource);
    }
}
