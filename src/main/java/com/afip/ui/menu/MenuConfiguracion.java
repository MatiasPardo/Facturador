package com.afip.ui.menu;

import com.afip.config.AfipConfig;
import com.afip.ui.console.ConsoleInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuConfiguracion {
    
    private static final Logger log = LoggerFactory.getLogger(MenuConfiguracion.class);
    
    public void mostrar() {
        System.out.println("\n=== CONFIGURACIÓN ===");
        System.out.println("1. 📋 Ver configuración actual");
        System.out.println("2. ✏️ Cambiar CUIT emisor");
        System.out.println("3. 🏪 Cambiar punto de venta");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = ConsoleInput.leerOpcion();
        
        switch (opcion) {
            case 1: mostrarConfiguracion(); break;
            case 2: cambiarCuitEmisor(); break;
            case 3: cambiarPuntoVenta(); break;
        }
    }
    
    private void mostrarConfiguracion() {
        log.info("🔧 === CONFIGURACIÓN ACTUAL ===");
        log.info("📋 CUIT Emisor: {}", AfipConfig.CUIT_EMISOR);
        log.info("🏪 Punto de Venta: {}", AfipConfig.PUNTO_VENTA_DEFAULT);
        log.info("🏢 Razón Social: {}", AfipConfig.RAZON_SOCIAL_EMISOR);
        log.info("🔐 Certificado: {}", AfipConfig.CERT_PATH);
        log.info("💰 Importe Prueba: ${}", AfipConfig.IMPORTE_PRUEBA);
    }
    
    private void cambiarCuitEmisor() {
        String nuevoCuit = ConsoleInput.leerNuevoCuit();
        if (!nuevoCuit.isEmpty()) {
            AfipConfig.setCuitEmisor(nuevoCuit);
            log.info("✅ CUIT Emisor actualizado a: {}", nuevoCuit);
        }
    }
    
    private void cambiarPuntoVenta() {
        String nuevoPV = ConsoleInput.leerNuevoPuntoVenta();
        if (!nuevoPV.isEmpty()) {
            AfipConfig.setPuntoVentaDefault(Integer.parseInt(nuevoPV));
            log.info("✅ Punto de Venta actualizado a: {}", nuevoPV);
        }
    }
}