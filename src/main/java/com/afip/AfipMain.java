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

public class AfipMain {
    
    private static final Logger log = LoggerFactory.getLogger(AfipMain.class);
    
    // Configuración
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        try {
            // Crear adaptador AFIP
            AfipAdapter afipAdapter = new AfipAdapter(
                CERT_PATH, CERT_PASSWORD, CERT_ALIAS, 
                WSAA_URL, WSFE_URL
            );
            
            // Ejemplo 1: Autenticación
            log.info("=== EJEMPLO 1: AUTENTICACIÓN ===");
            afipAdapter.authenticate();
            
            // Ejemplo 2: Consultar último comprobante
            log.info("\\n=== EJEMPLO 2: ÚLTIMO COMPROBANTE ===");
            long ultimoComprobante = afipAdapter.obtenerUltimoComprobante(1, TipoComprobante.FACTURA_A.getCodigo());
            log.info("📊 Último comprobante: {}", ultimoComprobante);
            
            // Ejemplo 3: Crear y solicitar CAE para Factura A
            log.info("\\n=== EJEMPLO 3: SOLICITAR CAE FACTURA A ===");
            Comprobante facturaA = crearFacturaA(ultimoComprobante + 1);
            CAEResponse caeResponse = afipAdapter.solicitarCAE(facturaA);
            
            if (caeResponse.isSuccess()) {
                log.info("✅ CAE obtenido: {} - Vence: {}", caeResponse.getCae(), caeResponse.getFechaVencimiento());
            } else {
                log.error("❌ Error al obtener CAE: {}", caeResponse.getErrorMessage());
            }
            
            // Ejemplo 4: Crear y solicitar CAE para Factura B
            log.info("\\n=== EJEMPLO 4: SOLICITAR CAE FACTURA B ===");
            Comprobante facturaB = crearFacturaB(ultimoComprobante + 2);
            CAEResponse caeResponseB = afipAdapter.solicitarCAE(facturaB);
            
            if (caeResponseB.isSuccess()) {
                log.info("✅ CAE obtenido: {} - Vence: {}", caeResponseB.getCae(), caeResponseB.getFechaVencimiento());
            } else {
                log.error("❌ Error al obtener CAE: {}", caeResponseB.getErrorMessage());
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error de autenticación: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error general: {}", e.getMessage(), e);
        }
    }
    
    private static Comprobante crearFacturaA(long numeroComprobante) {
        Comprobante comprobante = new Comprobante();
        comprobante.setTipoComprobante(TipoComprobante.FACTURA_A);
        comprobante.setPuntoVenta(1);
        comprobante.setNumeroComprobante(numeroComprobante);
        comprobante.setFechaComprobante(LocalDate.now());
        comprobante.setCuitCliente(20123456789L); // CUIT del cliente
        comprobante.setTipoDocumento(80); // CUIT
        comprobante.setImporteNeto(new BigDecimal("100.00"));
        comprobante.setImporteIva(new BigDecimal("21.00"));
        comprobante.setImporteTotal(new BigDecimal("121.00"));
        comprobante.setConcepto("Productos");
        
        return comprobante;
    }
    
    private static Comprobante crearFacturaB(long numeroComprobante) {
        Comprobante comprobante = new Comprobante();
        comprobante.setTipoComprobante(TipoComprobante.FACTURA_B);
        comprobante.setPuntoVenta(1);
        comprobante.setNumeroComprobante(numeroComprobante);
        comprobante.setFechaComprobante(LocalDate.now());
        comprobante.setCuitCliente(12345678901L); // DNI del cliente
        comprobante.setTipoDocumento(96); // DNI
        comprobante.setImporteNeto(new BigDecimal("826.45"));
        comprobante.setImporteIva(new BigDecimal("173.55"));
        comprobante.setImporteTotal(new BigDecimal("1000.00"));
        comprobante.setConcepto("Productos");
        
        return comprobante;
    }
}