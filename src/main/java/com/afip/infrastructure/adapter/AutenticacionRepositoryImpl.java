package com.afip.infrastructure.adapter;

import com.afip.adapter.AfipAdapter;
import com.afip.adapter.MonotributoAdapter;
import com.afip.auth.CredentialsManager;
import com.afip.domain.port.out.AutenticacionRepository;

public class AutenticacionRepositoryImpl implements AutenticacionRepository {
    
    private final AfipAdapter afipAdapter;
    private final MonotributoAdapter monotributoAdapter;
    
    public AutenticacionRepositoryImpl(AfipAdapter afipAdapter, MonotributoAdapter monotributoAdapter) {
        this.afipAdapter = afipAdapter;
        this.monotributoAdapter = monotributoAdapter;
    }
    
    @Override
    public void autenticar(String servicio) {
        try {
            switch (servicio) {
                case "wsfe":
                    afipAdapter.authenticate();
                    break;
                case "wsmtxca":
                    monotributoAdapter.authenticate();
                    break;
                default:
                    throw new IllegalArgumentException("Servicio no soportado: " + servicio);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error en autenticación " + servicio, e);
        }
    }
    
    @Override
    public boolean tieneCredencialesValidas(String servicio) {
        return CredentialsManager.hasValidCredentials(servicio);
    }
    
    @Override
    public void limpiarCredenciales() {
        CredentialsManager.clearAllCredentials();
    }
    
    @Override
    public void limpiarCredencialesServicio(String servicio) {
        CredentialsManager.clearCredentials(servicio);
    }
}