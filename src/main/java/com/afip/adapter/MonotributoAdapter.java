package com.afip.adapter;

import com.afip.auth.AfipAuthenticationException;
import com.afip.auth.AfipCredentials;
import com.afip.auth.CredentialsManager;
import com.afip.auth.WsaaAuthenticator;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.Comprobante;
import com.afip.monotributo.WsmtxcaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonotributoAdapter {
    
    private static final Logger log = LoggerFactory.getLogger(MonotributoAdapter.class);
    
    private final WsaaAuthenticator authenticator;
    private final WsmtxcaService wsmtxcaService;
    private AfipCredentials currentCredentials;
    private static final String WSMTXCA_SERVICE = "wsmtxca";
    
    public MonotributoAdapter(String certPath, String certPassword, String certAlias, 
                             String wsaaUrl, String wsmtxcaUrl) {
        this.authenticator = new WsaaAuthenticator(certPath, certPassword, certAlias, wsaaUrl);
        this.wsmtxcaService = new WsmtxcaService(wsmtxcaUrl);
    }
    
    /**
     * Autentica con AFIP para WSMTXCA
     */
    public void authenticate() throws AfipAuthenticationException {
        log.info("🚀 Iniciando autenticación WSMTXCA (Monotributo)");
        this.currentCredentials = authenticator.authenticate(WSMTXCA_SERVICE);
        log.info("✅ Autenticación Monotributo exitosa");
    }
    
    /**
     * Solicita CAE para comprobante de Monotributo
     */
    public CAEResponse solicitarCAEMonotributo(Comprobante comprobante) throws AfipAuthenticationException {
        ensureAuthenticated();
        
        log.info("📄 Solicitando CAE Monotributo N° {}", comprobante.getNumeroComprobante());
        
        return wsmtxcaService.solicitarCAEMonotributo(comprobante, currentCredentials);
    }
    
    /**
     * Obtiene último comprobante de Monotributo
     */
    public long obtenerUltimoComprobanteMonotributo(int puntoVenta) throws AfipAuthenticationException {
        ensureAuthenticated();
        
        log.info("🔍 Consultando último comprobante Monotributo PV: {}", puntoVenta);
        
        return wsmtxcaService.obtenerUltimoComprobanteMonotributo(puntoVenta, currentCredentials);
    }
    
    private void ensureAuthenticated() throws AfipAuthenticationException {
        if (currentCredentials == null) {
            currentCredentials = CredentialsManager.loadCredentials(WSMTXCA_SERVICE);
            
            if (currentCredentials == null) {
                log.info("⚠️ No hay credenciales válidas para {}, autenticando...", WSMTXCA_SERVICE);
                authenticate();
            } else {
                log.info("✅ Usando credenciales guardadas para {}", WSMTXCA_SERVICE);
            }
        }
    }
    
    public void clearCredentials() {
        this.currentCredentials = null;
        CredentialsManager.clearCredentials(WSMTXCA_SERVICE);
        log.info("🗑️ Credenciales Monotributo limpiadas");
    }
    
    public boolean isAuthenticated() {
        return currentCredentials != null;
    }
}