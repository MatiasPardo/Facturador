package com.afip.domain.port.in;

public interface AutenticacionUseCase {
    
    void autenticarWSFE();
    
    void autenticarWSMTXCA();
    
    boolean verificarCredencialesWSFE();
    
    boolean verificarCredencialesWSMTXCA();
    
    void limpiarCredenciales();
}