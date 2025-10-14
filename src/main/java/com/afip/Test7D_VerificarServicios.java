package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.adapter.MonotributoAdapter;
import com.afip.auth.AfipAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test7D_VerificarServicios {
    
    private static final Logger log = LoggerFactory.getLogger(Test7D_VerificarServicios.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    private static final String WSMTXCA_URL = "https://servicios1.afip.gov.ar/wsmtxca/services/MTXCAService";
    
    public static void main(String[] args) {
        log.info("=== TEST 7D: VERIFICAR SERVICIOS DISPONIBLES ===");
        
        // Probar WSFE (debería funcionar)
        log.info("🔍 Probando servicio WSFE...");
        try {
            AfipAdapter wsfeAdapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            wsfeAdapter.authenticate();
            log.info("✅ WSFE: Autenticación exitosa");
        } catch (AfipAuthenticationException e) {
            log.error("❌ WSFE: Error - {}", e.getMessage());
        }
        
        log.info("");
        
        // Probar WSMTXCA (puede fallar si no está habilitado)
        log.info("🔍 Probando servicio WSMTXCA...");
        try {
            MonotributoAdapter mtxAdapter = new MonotributoAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSMTXCA_URL);
            mtxAdapter.authenticate();
            log.info("✅ WSMTXCA: Autenticación exitosa");
        } catch (AfipAuthenticationException e) {
            log.error("❌ WSMTXCA: Error - {}", e.getMessage());
            log.info("💡 Posibles causas:");
            log.info("   - El CUIT no está habilitado para Monotributo");
            log.info("   - El servicio WSMTXCA no está disponible para este certificado");
            log.info("   - Verificar en AFIP si tienes acceso a servicios de Monotributo");
        }
    }
}