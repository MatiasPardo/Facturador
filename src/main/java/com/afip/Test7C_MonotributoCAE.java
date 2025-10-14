package com.afip;

import com.afip.adapter.MonotributoAdapter;
import com.afip.auth.AfipAuthenticationException;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.Comprobante;
import com.afip.billing.model.TipoComprobante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Test7C_MonotributoCAE {
    
    private static final Logger log = LoggerFactory.getLogger(Test7C_MonotributoCAE.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSMTXCA_URL = "https://servicios1.afip.gov.ar/wsmtxca/services/MTXCAService";
    
    public static void main(String[] args) {
        log.info("=== TEST 7C: SOLICITAR CAE MONOTRIBUTO ===");
        
        try {
            MonotributoAdapter adapter = new MonotributoAdapter(
                CERT_PATH, CERT_PASSWORD, CERT_ALIAS, 
                WSAA_URL, WSMTXCA_URL
            );
            
            // Obtener próximo número de comprobante
            long ultimoComprobante = adapter.obtenerUltimoComprobanteMonotributo(1);
            long proximoNumero = ultimoComprobante + 1;
            
            // Crear comprobante Monotributo
            Comprobante comprobante = new Comprobante();
            comprobante.setTipoComprobante(TipoComprobante.FACTURA_C);
            comprobante.setPuntoVenta(1);
            comprobante.setNumeroComprobante(proximoNumero);
            comprobante.setFechaComprobante(LocalDate.now());
            comprobante.setCuitCliente(12345678901L);
            comprobante.setTipoDocumento(96); // DNI
            comprobante.setImporteTotal(new BigDecimal("500.00"));
            
            log.info("📄 Solicitando CAE Monotributo N° {}", proximoNumero);
            CAEResponse cae = adapter.solicitarCAEMonotributo(comprobante);
            
            if (cae.isSuccess()) {
                log.info("✅ CAE Monotributo obtenido: {} - Vence: {}", cae.getCae(), cae.getFechaVencimiento());
            } else {
                log.error("❌ Error CAE Monotributo: {}", cae.getErrorMessage());
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error: {}", e.getMessage());
            log.info("💡 Ejecutar primero Test7A_MonotributoAuth para obtener credenciales");
        }
    }
}