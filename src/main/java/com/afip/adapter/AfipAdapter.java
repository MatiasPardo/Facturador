package com.afip.adapter;

import com.afip.auth.AfipAuthenticationException;
import com.afip.auth.AfipCredentials;
import com.afip.auth.CredentialsManager;
import com.afip.auth.WsaaAuthenticator;
import com.afip.billing.WsfeService;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.Comprobante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfipAdapter {
    
    private static final Logger log = LoggerFactory.getLogger(AfipAdapter.class);
    
    private final WsaaAuthenticator authenticator;
    private final WsfeService wsfeService;
    private AfipCredentials currentCredentials;
    private static final String WSFE_SERVICE = "wsfe";
    
    public AfipAdapter(String certPath, String certPassword, String certAlias, 
                      String wsaaUrl, String wsfeUrl) {
        this.authenticator = new WsaaAuthenticator(certPath, certPassword, certAlias, wsaaUrl);
        this.wsfeService = new WsfeService(wsfeUrl);
    }
    
    /**
     * Autentica con AFIP y obtiene credenciales válidas
     */
    public void authenticate() throws AfipAuthenticationException {
        log.info("🚀 Iniciando proceso de autenticación con AFIP");
        this.currentCredentials = authenticator.authenticate(WSFE_SERVICE);
        log.info("✅ Autenticación exitosa");
    }
    
    /**
     * Solicita CAE para un comprobante
     */
    public CAEResponse solicitarCAE(Comprobante comprobante) throws AfipAuthenticationException {
        ensureAuthenticated();
        
        log.info("📄 Solicitando CAE para {} N° {}", 
                comprobante.getTipoComprobante().getDescripcion(),
                comprobante.getNumeroComprobante());
        
        return wsfeService.solicitarCAE(comprobante, currentCredentials);
    }
    
    /**
     * Obtiene el último número de comprobante autorizado
     */
    public long obtenerUltimoComprobante(int puntoVenta, int tipoComprobante) throws AfipAuthenticationException {
        ensureAuthenticated();
        
        log.info("🔍 Consultando último comprobante PV: {} Tipo: {}", puntoVenta, tipoComprobante);
        
        return wsfeService.obtenerUltimoComprobante(puntoVenta, tipoComprobante, currentCredentials);
    }
    
    /**
     * Consulta los puntos de venta habilitados
     */
    public java.util.List<Integer> consultarPuntosVenta() throws AfipAuthenticationException {
        ensureAuthenticated();
        
        log.info("🏪 Consultando puntos de venta habilitados");
        
        return wsfeService.consultarPuntosVenta(currentCredentials);
    }
    
    /**
     * Consulta el último comprobante para cada punto de venta y tipo
     */
    public java.util.Map<String, Long> consultarUltimosComprobantesPorPV(int tipoComprobante) throws AfipAuthenticationException {
        ensureAuthenticated();
        
        log.info("📈 Consultando últimos comprobantes tipo {} para todos los PV", tipoComprobante);
        
        java.util.List<Integer> puntosVenta = consultarPuntosVenta();
        java.util.Map<String, Long> resultados = new java.util.HashMap<>();
        
        for (Integer pv : puntosVenta) {
            try {
                long ultimo = obtenerUltimoComprobante(pv, tipoComprobante);
                resultados.put("PV_" + pv, ultimo);
                log.info("📊 PV {}: Último comprobante = {}", pv, ultimo);
            } catch (Exception e) {
                log.warn("⚠️ Error consultando PV {}: {}", pv, e.getMessage());
                resultados.put("PV_" + pv, 0L);
            }
        }
        
        return resultados;
    }
    
    /**
     * Consulta un comprobante por su CAE
     */
    public String consultarComprobantePorCAE(String cae) throws AfipAuthenticationException {
        ensureAuthenticated();
        
        log.info("🔍 Consultando comprobante por CAE: {}", cae);
        
        return wsfeService.consultarComprobantePorCAE(cae, currentCredentials);
    }
    
    /**
     * Verifica si hay credenciales válidas, si no las hay o están expiradas, autentica automáticamente
     */
    private void ensureAuthenticated() throws AfipAuthenticationException {
        if (currentCredentials == null || currentCredentials.isExpired()) {
            if (currentCredentials != null && currentCredentials.isExpired()) {
                log.warn("⏰ Token expirado para {}, renovando automáticamente...", WSFE_SERVICE);
            }
            
            // Intentar cargar credenciales guardadas para WSFE
            currentCredentials = CredentialsManager.loadCredentials(WSFE_SERVICE);
            
            if (currentCredentials == null || currentCredentials.isExpired()) {
                log.info("🔄 Autenticando {} (token expirado o inexistente)...", WSFE_SERVICE);
                authenticate();
            } else {
                log.info("✅ Usando credenciales guardadas válidas para {}", WSFE_SERVICE);
            }
        }
    }
    
    /**
     * Limpia las credenciales actuales (útil para forzar re-autenticación)
     */
    public void clearCredentials() {
        this.currentCredentials = null;
        CredentialsManager.clearCredentials(WSFE_SERVICE);
        log.info("🗑️ Credenciales limpiadas para {}", WSFE_SERVICE);
    }
    
    /**
     * Verifica si hay credenciales válidas
     */
    public boolean isAuthenticated() {
        return currentCredentials != null;
    }
}