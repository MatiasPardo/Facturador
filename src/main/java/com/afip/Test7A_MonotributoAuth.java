package com.afip;

import com.afip.adapter.MonotributoAdapter;
import com.afip.auth.AfipAuthenticationException;
import com.afip.auth.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test7A_MonotributoAuth {
    
    private static final Logger log = LoggerFactory.getLogger(Test7A_MonotributoAuth.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSMTXCA_URL = "https://servicios1.afip.gov.ar/wsmtxca/services/MTXCAService";
    
    public static void main(String[] args) {
        log.info("=== TEST 7A: AUTENTICACIÓN MONOTRIBUTO ===");
        
        try {
            MonotributoAdapter adapter = new MonotributoAdapter(
                CERT_PATH, CERT_PASSWORD, CERT_ALIAS, 
                WSAA_URL, WSMTXCA_URL
            );
            
            // Verificar si ya hay credenciales
            if (CredentialsManager.hasValidCredentials("wsmtxca")) {
                log.info("✅ Ya existen credenciales para WSMTXCA");
            } else {
                log.info("🔐 No hay credenciales, autenticando para WSMTXCA...");
                adapter.authenticate();
                log.info("💾 Credenciales Monotributo guardadas exitosamente");
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error de autenticación Monotributo: {}", e.getMessage());
        }
    }
}