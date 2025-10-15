package com.afip.application.usecase;

import com.afip.domain.port.in.AutenticacionUseCase;
import com.afip.domain.port.out.AutenticacionRepository;

public class AutenticacionService implements AutenticacionUseCase {
    
    private final AutenticacionRepository autenticacionRepository;
    private static final String WSFE_SERVICE = "wsfe";
    private static final String WSMTXCA_SERVICE = "wsmtxca";
    
    public AutenticacionService(AutenticacionRepository autenticacionRepository) {
        this.autenticacionRepository = autenticacionRepository;
    }
    
    @Override
    public void autenticarWSFE() {
        autenticacionRepository.autenticar(WSFE_SERVICE);
    }
    
    @Override
    public void autenticarWSMTXCA() {
        autenticacionRepository.autenticar(WSMTXCA_SERVICE);
    }
    
    @Override
    public boolean verificarCredencialesWSFE() {
        return autenticacionRepository.tieneCredencialesValidas(WSFE_SERVICE);
    }
    
    @Override
    public boolean verificarCredencialesWSMTXCA() {
        return autenticacionRepository.tieneCredencialesValidas(WSMTXCA_SERVICE);
    }
    
    @Override
    public void limpiarCredenciales() {
        autenticacionRepository.limpiarCredenciales();
    }
}