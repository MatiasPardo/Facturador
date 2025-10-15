package com.afip.ui.menu;

import com.afip.billing.model.CAEResponse;
import com.afip.config.AfipConfig;
import com.afip.service.AfipService;
import com.afip.ui.console.ConsoleInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class MenuMonotributo {
    
    private static final Logger log = LoggerFactory.getLogger(MenuMonotributo.class);
    private final AfipService afipService;
    
    public MenuMonotributo(AfipService afipService) {
        this.afipService = afipService;
    }
    
    public void mostrar() {
        System.out.println("\n=== TESTS DE MONOTRIBUTO ===");
        System.out.println("1. 📊 Último comprobante monotributo");
        System.out.println("2. 📄 Generar CAE monotributo");
        System.out.println("3. 🧪 Prueba monotributo (importe bajo)");
        System.out.println("4. 🔐 Autenticar WSMTXCA");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = ConsoleInput.leerOpcion();
        
        try {
            switch (opcion) {
                case 1: consultarUltimoComprobanteMonotributo(); break;
                case 2: generarCAEMonotributo(); break;
                case 3: pruebaMonotributo(); break;
                case 4: autenticarWSMTXCA(); break;
            }
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
    
    private void consultarUltimoComprobanteMonotributo() throws Exception {
        log.info("📊 Test: Último Comprobante Monotributo");
        long ultimo = afipService.consultarUltimoComprobanteMonotributo(AfipConfig.PUNTO_VENTA_DEFAULT);
        log.info("Último comprobante Monotributo PV {}: {}", AfipConfig.PUNTO_VENTA_DEFAULT, ultimo);
    }
    
    private void generarCAEMonotributo() throws Exception {
        log.info("📄 Test: CAE Monotributo");
        BigDecimal importe = ConsoleInput.leerImporte();
        CAEResponse cae = afipService.generarComprobanteMonotributo(importe);
        mostrarResultado(cae);
    }
    
    private void pruebaMonotributo() throws Exception {
        log.info("🧪 Test: Prueba Monotributo");
        CAEResponse cae = afipService.generarComprobanteMonotributo(new BigDecimal(AfipConfig.IMPORTE_PRUEBA));
        mostrarResultado(cae);
    }
    
    private void autenticarWSMTXCA() throws Exception {
        log.info("🔐 Test: Autenticación WSMTXCA");
        afipService.autenticarWSMTXCA();
    }
    
    private void mostrarResultado(CAEResponse cae) {
        if (cae.isSuccess()) {
            log.info("✅ ¡CAE MONOTRIBUTO GENERADO!");
            log.info("   🔐 CAE: {}", cae.getCae());
            log.info("   📅 Vence: {}", cae.getFechaVencimiento());
        } else {
            log.error("❌ Error: {}", cae.getErrorMessage());
        }
    }
}