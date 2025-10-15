package com.afip.ui.menu;

import com.afip.service.AfipService;
import com.afip.ui.console.ConsoleInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuAutenticacion {
    
    private static final Logger log = LoggerFactory.getLogger(MenuAutenticacion.class);
    private final AfipService afipService;
    
    public MenuAutenticacion(AfipService afipService) {
        this.afipService = afipService;
    }
    
    public void mostrar() {
        System.out.println("\n=== TESTS DE AUTENTICACIÓN ===");
        System.out.println("1. 🔐 Autenticar WSFE");
        System.out.println("2. 🔐 Autenticar WSMTXCA (Monotributo)");
        System.out.println("3. ✅ Verificar credenciales");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = ConsoleInput.leerOpcion();
        
        try {
            switch (opcion) {
                case 1: 
                    log.info("🔐 Test: Autenticación WSFE");
                    afipService.autenticarWSFE();
                    break;
                case 2: 
                    log.info("🔐 Test: Autenticación WSMTXCA");
                    afipService.autenticarWSMTXCA();
                    break;
                case 3: 
                    log.info("✅ Test: Verificar Credenciales");
                    afipService.verificarCredenciales();
                    break;
            }
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
}