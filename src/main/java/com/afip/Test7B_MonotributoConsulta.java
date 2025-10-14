package com.afip;

import com.afip.adapter.MonotributoAdapter;
import com.afip.auth.AfipAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test7B_MonotributoConsulta {
    
    private static final Logger log = LoggerFactory.getLogger(Test7B_MonotributoConsulta.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSMTXCA_URL = "https://servicios1.afip.gov.ar/wsmtxca/services/MTXCAService";
    
    public static void main(String[] args) {
        log.info("=== TEST 7B: CONSULTAR ÚLTIMO COMPROBANTE MONOTRIBUTO ===");
        
        try {
            MonotributoAdapter adapter = new MonotributoAdapter(
                CERT_PATH, CERT_PASSWORD, CERT_ALIAS, 
                WSAA_URL, WSMTXCA_URL
            );
            
            // Consultar último comprobante PV 1
            log.info("🔍 Consultando último comprobante Monotributo PV 1...");
            long ultimoComprobante = adapter.obtenerUltimoComprobanteMonotributo(1);
            log.info("📊 Último comprobante Monotributo PV 1: {}", ultimoComprobante);
            
            // Consultar último comprobante PV 2 (si existe)
            log.info("🔍 Consultando último comprobante Monotributo PV 2...");
            long ultimoComprobantePV2 = adapter.obtenerUltimoComprobanteMonotributo(2);
            log.info("📊 Último comprobante Monotributo PV 2: {}", ultimoComprobantePV2);
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error: {}", e.getMessage());
            log.info("💡 Ejecutar primero Test7A_MonotributoAuth para obtener credenciales");
        }
    }
}