package com.afip.infrastructure.adapter.in.web.config;

import com.afip.application.usecase.*;
import com.afip.domain.port.in.*;
import com.afip.infrastructure.adapter.AfipRepositoryImpl;
import com.afip.infrastructure.adapter.AutenticacionRepositoryImpl;
import com.afip.infrastructure.adapter.MonotributoRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    
    @Bean
    public ConsultarPuntosVentaUseCase consultarPuntosVentaUseCase(AfipRepositoryImpl afipRepository) {
        return new ConsultarPuntosVentaService(afipRepository);
    }
    
    @Bean
    public ConsultarUltimoComprobanteUseCase consultarUltimoComprobanteUseCase(AfipRepositoryImpl afipRepository) {
        return new ConsultarUltimoComprobanteService(afipRepository);
    }
    
    @Bean
    public ConsultarCAEUseCase consultarCAEUseCase(AfipRepositoryImpl afipRepository) {
        return new ConsultarCAEService(afipRepository);
    }
    
    @Bean
    public SolicitarCAEUseCase solicitarCAEUseCase(AfipRepositoryImpl afipRepository, 
                                                  MonotributoRepositoryImpl monotributoRepository) {
        return new SolicitarCAEService(afipRepository, monotributoRepository);
    }
    
    @Bean
    public GenerarFacturaUseCase generarFacturaUseCase(ConsultarUltimoComprobanteUseCase consultarUltimo,
                                                      SolicitarCAEUseCase solicitarCAE) {
        return new GenerarFacturaService(consultarUltimo, solicitarCAE);
    }
    
    @Bean
    public AutenticarWSFEUseCase autenticarWSFEUseCase(AutenticacionRepositoryImpl autenticacionRepository) {
        return new AutenticarWSFEService(autenticacionRepository);
    }
    
    @Bean
    public AutenticarWSMTXCAUseCase autenticarWSMTXCAUseCase(AutenticacionRepositoryImpl autenticacionRepository) {
        return new AutenticarWSMTXCAService(autenticacionRepository);
    }
}