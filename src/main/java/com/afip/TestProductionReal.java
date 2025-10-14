package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.auth.AfipAuthenticationException;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.Comprobante;
import com.afip.billing.model.TipoComprobante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestProductionReal {
    
    private static final Logger log = LoggerFactory.getLogger(TestProductionReal.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        log.info("=== PRODUCCIÓN: FACTURA REAL ===");
        log.warn("⚠️ ESTO GENERARÁ UNA FACTURA REAL EN AFIP");
        
        try {
            AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            
            // Consultar último comprobante
            log.info("🔍 Consultando último comprobante Factura C...");
            long ultimoComprobante = adapter.obtenerUltimoComprobante(1, TipoComprobante.FACTURA_C.getCodigo());
            log.info("📊 Último comprobante: {}", ultimoComprobante);
            
            // Crear factura REAL
            Comprobante factura = new Comprobante();
            factura.setTipoComprobante(TipoComprobante.FACTURA_C);
            factura.setPuntoVenta(1); // Verificar que este PV esté habilitado
            factura.setNumeroComprobante(ultimoComprobante + 1);
            factura.setFechaComprobante(LocalDate.now());
            
            // Consumidor Final
            factura.setCuitCliente(0L);
            factura.setTipoDocumento(99);
            
            // Importe REAL
            BigDecimal importeTotal = new BigDecimal("100.00"); // Importe bajo para prueba
            factura.setImporteTotal(importeTotal);
            factura.setImporteNeto(importeTotal);
            factura.setImporteIva(BigDecimal.ZERO);
            
            log.info("📄 GENERANDO FACTURA REAL N° {}", factura.getNumeroComprobante());
            log.info("   💰 Importe: ${}", importeTotal);
            log.warn("⚠️ Esta será una factura REAL en AFIP");
            
            CAEResponse cae = adapter.solicitarCAE(factura);
            
            if (cae.isSuccess()) {
                log.info("✅ ¡FACTURA REAL GENERADA!");
                log.info("   🔐 CAE: {}", cae.getCae());
                log.info("   📅 Vence: {}", cae.getFechaVencimiento());
            } else {
                log.error("❌ Error: {}", cae.getErrorMessage());
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
}