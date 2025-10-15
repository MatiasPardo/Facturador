package com.afip.infrastructure.adapter.in.web.config;

import com.afip.adapter.AfipAdapter;
import com.afip.adapter.MonotributoAdapter;
import com.afip.config.AfipConfig;
import com.afip.infrastructure.adapter.AfipRepositoryImpl;
import com.afip.infrastructure.adapter.AutenticacionRepositoryImpl;
import com.afip.infrastructure.adapter.MonotributoRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    
    @Bean
    public AfipAdapter afipAdapter() {
        return new AfipAdapter(
            AfipConfig.CERT_PATH, 
            AfipConfig.CERT_PASSWORD, 
            AfipConfig.CERT_ALIAS,
            AfipConfig.WSAA_URL, 
            AfipConfig.WSFE_URL
        );
    }
    
    @Bean
    public MonotributoAdapter monotributoAdapter() {
        return new MonotributoAdapter(
            AfipConfig.CERT_PATH, 
            AfipConfig.CERT_PASSWORD, 
            AfipConfig.CERT_ALIAS,
            AfipConfig.WSAA_URL, 
            AfipConfig.WSMTXCA_URL
        );
    }
    
    @Bean
    public AfipRepositoryImpl afipRepository(AfipAdapter afipAdapter) {
        return new AfipRepositoryImpl(afipAdapter);
    }
    
    @Bean
    public MonotributoRepositoryImpl monotributoRepository(MonotributoAdapter monotributoAdapter) {
        return new MonotributoRepositoryImpl(monotributoAdapter);
    }
    
    @Bean
    public AutenticacionRepositoryImpl autenticacionRepository(AfipAdapter afipAdapter, 
                                                              MonotributoAdapter monotributoAdapter) {
        return new AutenticacionRepositoryImpl(afipAdapter, monotributoAdapter);
    }
}