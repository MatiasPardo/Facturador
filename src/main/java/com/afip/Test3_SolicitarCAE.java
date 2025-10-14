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

public class Test3_SolicitarCAE {
    
    private static final Logger log = LoggerFactory.getLogger(Test3_SolicitarCAE.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        log.info("=== TEST 3: SOLICITAR CAE ===");
        
        try {
            AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            
            // Obtener próximo número de comprobante
            long ultimoComprobante = adapter.obtenerUltimoComprobante(1, TipoComprobante.FACTURA_B.getCodigo());
            long proximoNumero = ultimoComprobante + 1;
            
            // Crear Factura B
            Comprobante factura = new Comprobante();
            factura.setTipoComprobante(TipoComprobante.FACTURA_B);
            factura.setPuntoVenta(1);
            factura.setNumeroComprobante(proximoNumero);
            factura.setFechaComprobante(LocalDate.now());
            factura.setCuitCliente(12345678901L);
            factura.setTipoDocumento(96); // DNI
            factura.setImporteNeto(new BigDecimal("826.45"));
            factura.setImporteIva(new BigDecimal("173.55"));
            factura.setImporteTotal(new BigDecimal("1000.00"));
            
            log.info("📄 Solicitando CAE para Factura B N° {}", proximoNumero);
            CAEResponse cae = adapter.solicitarCAE(factura);
            
            if (cae.isSuccess()) {
                log.info("✅ CAE obtenido: {} - Vence: {}", cae.getCae(), cae.getFechaVencimiento());
            } else {
                log.error("❌ Error al obtener CAE: {}", cae.getErrorMessage());
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error: {}", e.getMessage());
            log.info("💡 Ejecutar primero Test1_Authentication para obtener credenciales");
        }
    }
}