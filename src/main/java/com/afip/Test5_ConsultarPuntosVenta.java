package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.auth.AfipAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Test5_ConsultarPuntosVenta {
    
    private static final Logger log = LoggerFactory.getLogger(Test5_ConsultarPuntosVenta.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        log.info("=== TEST 5: CONSULTAR PUNTOS DE VENTA ===");
        
        try {
            AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            
            List<Integer> puntosVenta = adapter.consultarPuntosVenta();
            
            if (puntosVenta.isEmpty()) {
                log.warn("⚠️ No se encontraron puntos de venta habilitados");
            } else {
                log.info("🏪 Puntos de venta habilitados:");
                for (Integer pv : puntosVenta) {
                    log.info("   📍 Punto de Venta: {}", pv);
                }
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error: {}", e.getMessage());
            log.info("💡 Ejecutar primero Test1_Authentication para obtener credenciales");
        }
    }
}