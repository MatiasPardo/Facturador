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

public class MonotributistaFlow {
    
    private static final Logger log = LoggerFactory.getLogger(MonotributistaFlow.class);
    
    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    
    public static void main(String[] args) {
        log.info("=== FLUJO MONOTRIBUTISTA: CONTINUAR FACTURACIÓN ===");
        
        try {
            AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            
            // PASO 1: Obtener último comprobante emitido (desde web AFIP)
            log.info("🔍 PASO 1: Consultando último comprobante emitido...");
            long ultimoComprobante = adapter.obtenerUltimoComprobante(1, TipoComprobante.FACTURA_C.getCodigo());
            log.info("📊 Último comprobante emitido: {}", ultimoComprobante);
            
            // PASO 2: Calcular próximo número
            long proximoNumero = ultimoComprobante + 1;
            log.info("➡️ PASO 2: Próximo comprobante a emitir: {}", proximoNumero);
            
            // PASO 3: Crear factura a consumidor final
            log.info("📄 PASO 3: Creando Factura C a Consumidor Final...");
            Comprobante factura = crearFacturaConsumidorFinal(proximoNumero);
            
            // PASO 4: Solicitar CAE
            log.info("🎯 PASO 4: Solicitando CAE...");
            CAEResponse cae = adapter.solicitarCAE(factura);
            
            if (cae.isSuccess()) {
                log.info("✅ ¡FACTURA EMITIDA EXITOSAMENTE!");
                log.info("   📋 Número: {}", proximoNumero);
                log.info("   🔐 CAE: {}", cae.getCae());
                log.info("   📅 Vence: {}", cae.getFechaVencimiento());
                log.info("   💰 Importe: ${}", factura.getImporteTotal());
            } else {
                log.error("❌ Error al emitir factura: {}", cae.getErrorMessage());
            }
            
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error de autenticación: {}", e.getMessage());
            log.info("💡 Ejecutar primero Test1_Authentication");
        }
    }
    
    private static Comprobante crearFacturaConsumidorFinal(long numeroComprobante) {
        Comprobante factura = new Comprobante();
        
        // Configuración para Monotributista
        factura.setTipoComprobante(TipoComprobante.FACTURA_C); // Factura C para Monotributo
        factura.setPuntoVenta(1);
        factura.setNumeroComprobante(numeroComprobante);
        factura.setFechaComprobante(LocalDate.now());
        
        // Consumidor Final
        factura.setCuitCliente(0L); // 0 = Consumidor Final
        factura.setTipoDocumento(99); // 99 = Sin identificar/Consumidor Final
        
        // Importes (Monotributo - sin discriminar IVA)
        BigDecimal importeTotal = new BigDecimal("100.00");
        factura.setImporteTotal(importeTotal);
        factura.setImporteNeto(importeTotal); // En Monotributo, neto = total
        factura.setImporteIva(BigDecimal.ZERO); // Monotributo no discrimina IVA
        
        factura.setConcepto("Productos");
        
        log.info("   👤 Cliente: Consumidor Final");
        log.info("   💰 Importe: ${}", importeTotal);
        log.info("   📋 Tipo: Factura C (Monotributo)");
        
        return factura;
    }
}