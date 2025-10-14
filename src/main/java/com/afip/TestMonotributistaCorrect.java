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

public class TestMonotributistaCorrect {
    
    private static final Logger log = LoggerFactory.getLogger(TestMonotributistaCorrect.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        log.info("=== MONOTRIBUTISTA: FACTURA C CORRECTA ===");
        
        try {
            AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            
            // Consultar último comprobante Factura C
            log.info("🔍 Consultando último comprobante Factura C...");
            long ultimoComprobante = adapter.obtenerUltimoComprobante(1, TipoComprobante.FACTURA_C.getCodigo());
            log.info("📊 Último comprobante Factura C: {}", ultimoComprobante);
            
            // Crear Factura C (Monotributo)
            Comprobante factura = new Comprobante();
            factura.setTipoComprobante(TipoComprobante.FACTURA_C); // Código 11
            factura.setPuntoVenta(1);
            factura.setNumeroComprobante(ultimoComprobante + 1);
            factura.setFechaComprobante(LocalDate.now());
            
            // Cliente real (cambiar por datos reales)
            factura.setCuitCliente(0L); // 0 = Consumidor Final (OK)
            factura.setTipoDocumento(99); // 99 = Sin identificar (OK)
            
            // Importes Monotributo (sin discriminar IVA)
            BigDecimal importeTotal = new BigDecimal("1500.00");
            factura.setImporteTotal(importeTotal);
            factura.setImporteNeto(importeTotal); // En Monotributo: neto = total
            factura.setImporteIva(BigDecimal.ZERO); // Sin IVA discriminado
            
            factura.setConcepto("Productos");
            
            log.info("📄 Solicitando CAE Factura C N° {}", factura.getNumeroComprobante());
            log.info("   👤 Cliente: Consumidor Final");
            log.info("   💰 Importe: ${}", importeTotal);
            
            CAEResponse cae = adapter.solicitarCAE(factura);
            
            if (cae.isSuccess()) {
                log.info("✅ ¡CAE OBTENIDO EXITOSAMENTE!");
                log.info("   🔐 CAE: {}", cae.getCae());
                log.info("   📅 Vence: {}", cae.getFechaVencimiento());
            } else {
                log.error("❌ Error: {}", cae.getErrorMessage());
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error de autenticación: {}", e.getMessage());
        }
    }
}