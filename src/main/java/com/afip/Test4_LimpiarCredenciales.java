package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.auth.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test4_LimpiarCredenciales {
    
    private static final Logger log = LoggerFactory.getLogger(Test4_LimpiarCredenciales.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        log.info("=== TEST 4: LIMPIAR CREDENCIALES ===");
        
        AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
        
        // Verificar estado actual
        if (CredentialsManager.hasValidCredentials("wsfe")) {
            log.info("📄 Credenciales WSFE encontradas");
            adapter.clearCredentials();
            log.info("🗑️ Credenciales WSFE eliminadas");
        } else {
            log.info("📭 No hay credenciales WSFE para eliminar");
        }
        
        // Verificar que se eliminaron
        if (!CredentialsManager.hasValidCredentials("wsfe")) {
            log.info("✅ Confirmado: No hay credenciales WSFE");
        }
    }
}