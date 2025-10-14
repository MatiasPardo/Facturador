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

public class Test7_Monotributo {
    
    private static final Logger log = LoggerFactory.getLogger(Test7_Monotributo.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSMTXCA_URL = "https://servicios1.afip.gov.ar/wsmtxca/services/MTXCAService";
    
    public static void main(String[] args) {
        log.info("=== TEST 7: MONOTRIBUTO (WSMTXCA) ===");
        
        try {
            MonotributoAdapter adapter = new MonotributoAdapter(
                CERT_PATH, CERT_PASSWORD, CERT_ALIAS, 
                WSAA_URL, WSMTXCA_URL
            );
            
            // Autenticar para Monotributo
            log.info("🔐 Autenticando para WSMTXCA...");
            adapter.authenticate();
            
            // Consultar último comprobante
            log.info("🔍 Consultando último comprobante Monotributo...");
            long ultimoComprobante = adapter.obtenerUltimoComprobanteMonotributo(1);
            log.info("📊 Último comprobante Monotributo PV 1: {}", ultimoComprobante);
            
            // Crear comprobante Monotributo
            Comprobante comprobante = new Comprobante();
            comprobante.setTipoComprobante(TipoComprobante.FACTURA_C); // Monotributo usa tipo C
            comprobante.setPuntoVenta(1);
            comprobante.setNumeroComprobante(ultimoComprobante + 1);
            comprobante.setFechaComprobante(LocalDate.now());
            comprobante.setCuitCliente(12345678901L);
            comprobante.setTipoDocumento(96); // DNI
            comprobante.setImporteTotal(new BigDecimal("500.00"));
            
            // Solicitar CAE Monotributo
            log.info("📄 Solicitando CAE Monotributo N° {}", comprobante.getNumeroComprobante());
            CAEResponse cae = adapter.solicitarCAEMonotributo(comprobante);
            
            if (cae.isSuccess()) {
                log.info("✅ CAE Monotributo obtenido: {} - Vence: {}", cae.getCae(), cae.getFechaVencimiento());
            } else {
                log.error("❌ Error CAE Monotributo: {}", cae.getErrorMessage());
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
}