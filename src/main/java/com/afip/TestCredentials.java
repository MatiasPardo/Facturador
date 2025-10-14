package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.auth.AfipAuthenticationException;
import com.afip.auth.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCredentials {
    
    private static final Logger log = LoggerFactory.getLogger(TestCredentials.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
        
        try {
            log.info("=== PRUEBA 1: AUTENTICACIÓN Y GUARDADO ===");
            adapter.authenticate();
            
            log.info("\n=== PRUEBA 2: VERIFICAR ARCHIVO GUARDADO ===");
            if (CredentialsManager.hasValidCredentials("wsfe")) {
                log.info("✅ Credenciales encontradas en archivo para wsfe");
            } else {
                log.warn("⚠️ No se encontraron credenciales para wsfe");
            }
            
            log.info("\n=== PRUEBA 3: LIMPIAR Y RECARGAR ===");
            adapter.clearCredentials();
            
            // Simular nueva instancia del adapter
            AfipAdapter newAdapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            
            log.info("\n=== PRUEBA 4: USAR CREDENCIALES GUARDADAS ===");
            // Esta llamada debería usar credenciales del archivo si existen
            long ultimo = newAdapter.obtenerUltimoComprobante(1, 1);
            log.info("📊 Último comprobante: {}", ultimo);
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
}