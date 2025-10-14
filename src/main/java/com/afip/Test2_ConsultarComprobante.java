package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.auth.AfipAuthenticationException;
import com.afip.billing.model.TipoComprobante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test2_ConsultarComprobante {
    
    private static final Logger log = LoggerFactory.getLogger(Test2_ConsultarComprobante.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        log.info("=== TEST 2: CONSULTAR ÚLTIMO COMPROBANTE ===");
        
        try {
            AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            
            // Consultar último comprobante Factura A
            log.info("🔍 Consultando último comprobante Factura A...");
            long ultimoFacturaA = adapter.obtenerUltimoComprobante(1, TipoComprobante.FACTURA_A.getCodigo());
            log.info("📊 Último comprobante Factura A PV 1: {}", ultimoFacturaA);
            
            // Consultar último comprobante Factura B
            log.info("🔍 Consultando último comprobante Factura B...");
            long ultimoFacturaB = adapter.obtenerUltimoComprobante(1, TipoComprobante.FACTURA_B.getCodigo());
            log.info("📊 Último comprobante Factura B PV 1: {}", ultimoFacturaB);
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error: {}", e.getMessage());
            log.info("💡 Ejecutar primero Test1_Authentication para obtener credenciales");
        }
    }
}