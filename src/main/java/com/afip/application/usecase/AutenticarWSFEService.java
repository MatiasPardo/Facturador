package com.afip.application.usecase;

import com.afip.domain.port.in.AutenticarWSFEUseCase;
import com.afip.domain.port.out.AutenticacionRepository;

public class AutenticarWSFEService implements AutenticarWSFEUseCase {
    
    private final AutenticacionRepository autenticacionRepository;
    
    public AutenticarWSFEService(AutenticacionRepository autenticacionRepository) {
        this.autenticacionRepository = autenticacionRepository;
    }
    
    @Override
    public void ejecutar() {
        autenticacionRepository.autenticar("wsfe");
    }
}