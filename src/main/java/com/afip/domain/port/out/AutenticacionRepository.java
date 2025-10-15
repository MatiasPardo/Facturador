package com.afip.domain.port.out;

public interface AutenticacionRepository {
    
    void autenticar(String servicio);
    
    boolean tieneCredencialesValidas(String servicio);
    
    void limpiarCredenciales();
    
    void limpiarCredencialesServicio(String servicio);
}