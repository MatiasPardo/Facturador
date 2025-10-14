package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.auth.AfipAuthenticationException;
import com.afip.billing.model.TipoComprobante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Test6_ConsultarTodosPV {
    
    private static final Logger log = LoggerFactory.getLogger(Test6_ConsultarTodosPV.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        log.info("=== TEST 6: CONSULTAR ÚLTIMOS COMPROBANTES TODOS LOS PV ===");
        
        try {
            AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            
            // Consultar Facturas A en todos los PV
            log.info("📊 Consultando Facturas A en todos los puntos de venta...");
            Map<String, Long> facturasA = adapter.consultarUltimosComprobantesPorPV(TipoComprobante.FACTURA_A.getCodigo());
            
            log.info("📋 Resultados Facturas A:");
            facturasA.forEach((pv, ultimo) -> 
                log.info("   🏪 {}: Último comprobante = {}", pv, ultimo)
            );
            
            log.info("");
            
            // Consultar Facturas B en todos los PV
            log.info("📊 Consultando Facturas B en todos los puntos de venta...");
            Map<String, Long> facturasB = adapter.consultarUltimosComprobantesPorPV(TipoComprobante.FACTURA_B.getCodigo());
            
            log.info("📋 Resultados Facturas B:");
            facturasB.forEach((pv, ultimo) -> 
                log.info("   🏪 {}: Último comprobante = {}", pv, ultimo)
            );
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error: {}", e.getMessage());
            log.info("💡 Ejecutar primero Test1_Authentication para obtener credenciales");
        }
    }
}