package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.auth.AfipAuthenticationException;
import com.afip.auth.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test1_Authentication {
    
    private static final Logger log = LoggerFactory.getLogger(Test1_Authentication.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        log.info("=== TEST 1: AUTENTICACIÓN Y GUARDADO ===");
        
        try {
            AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            
            // Verificar si ya hay credenciales
            if (CredentialsManager.hasValidCredentials("wsfe")) {
                log.info("✅ Ya existen credenciales para WSFE");
            } else {
                log.info("🔐 No hay credenciales, autenticando...");
                adapter.authenticate();
                log.info("💾 Credenciales guardadas exitosamente");
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error de autenticación: {}", e.getMessage());
        }
    }
}