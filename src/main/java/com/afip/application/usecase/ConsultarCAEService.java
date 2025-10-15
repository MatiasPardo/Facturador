package com.afip.application.usecase;

import com.afip.domain.port.in.ConsultarCAEUseCase;
import com.afip.domain.port.out.AfipRepository;

public class ConsultarCAEService implements ConsultarCAEUseCase {
    
    private final AfipRepository afipRepository;
    
    public ConsultarCAEService(AfipRepository afipRepository) {
        this.afipRepository = afipRepository;
    }
    
    @Override
    public String ejecutar(String cae) {
        return afipRepository.consultarComprobantePorCAE(cae);
    }
}