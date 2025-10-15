package com.afip.application.usecase;

import com.afip.domain.port.in.AutenticarWSMTXCAUseCase;
import com.afip.domain.port.out.AutenticacionRepository;

public class AutenticarWSMTXCAService implements AutenticarWSMTXCAUseCase {
    
    private final AutenticacionRepository autenticacionRepository;
    
    public AutenticarWSMTXCAService(AutenticacionRepository autenticacionRepository) {
        this.autenticacionRepository = autenticacionRepository;
    }
    
    @Override
    public void ejecutar() {
        autenticacionRepository.autenticar("wsmtxca");
    }
}