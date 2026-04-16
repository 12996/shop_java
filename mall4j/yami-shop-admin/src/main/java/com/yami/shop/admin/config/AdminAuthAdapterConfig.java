package com.yami.shop.admin.config;

import com.yami.shop.security.admin.adapter.ResourceServerAdapter;
import com.yami.shop.security.common.adapter.AuthConfigAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Explicitly registers the admin auth adapter in the admin application.
 * This avoids falling back to DefaultAuthConfigAdapter during local boot.
 */
@Configuration
public class AdminAuthAdapterConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAuthAdapterConfig.class);

    @Bean("authConfigAdapter")
    @Primary
    public AuthConfigAdapter authConfigAdapter() {
        LOGGER.info("using admin authConfigAdapter for local admin endpoints");
        return new ResourceServerAdapter();
    }
}
