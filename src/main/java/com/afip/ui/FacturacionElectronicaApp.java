package com.afip.ui;

import com.afip.config.AfipConfig;
import com.afip.service.AfipService;
import com.afip.ui.console.ConsoleInput;
import com.afip.ui.menu.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacturacionElectronicaApp {
    
    private static final Logger log = LoggerFactory.getLogger(FacturacionElectronicaApp.class);
    private final AfipService afipService;
    private final MenuAutenticacion menuAutenticacion;
    private final MenuConsultas menuConsultas;
    private final MenuFacturacion menuFacturacion;
    private final MenuMonotributo menuMonotributo;
    private final MenuConfiguracion menuConfiguracion;
    
    public FacturacionElectronicaApp() {
        this.afipService = new AfipService();
        this.menuAutenticacion = new MenuAutenticacion(afipService);
        this.menuConsultas = new MenuConsultas(afipService);
        this.menuFacturacion = new MenuFacturacion(afipService);
        this.menuMonotributo = new MenuMonotributo(afipService);
        this.menuConfiguracion = new MenuConfiguracion();
    }
    
    public static void main(String[] args) {
        try {
            FacturacionElectronicaApp app = new FacturacionElectronicaApp();
            app.inicializar();
            app.ejecutar();
        } catch (Exception e) {
            log.error("❌ Error general: {}", e.getMessage(), e);
        }
    }
    
    private void inicializar() throws Exception {
        log.info("🚀 Inicializando Facturación Electrónica AFIP");
        log.info("📋 CUIT Emisor: {}", AfipConfig.CUIT_EMISOR);
        log.info("🏪 Punto de Venta: {}", AfipConfig.PUNTO_VENTA_DEFAULT);
        log.info("🏢 Razón Social: {}", AfipConfig.RAZON_SOCIAL_EMISOR);
        
        afipService.inicializar();
    }
    
    private void ejecutar() {
        while (true) {
            MenuPrincipal.mostrar();
            int opcion = MenuPrincipal.leerOpcion();
            
            try {
                switch (opcion) {
                    case 1: menuAutenticacion.mostrar(); break;
                    case 2: menuConsultas.mostrar(); break;
                    case 3: menuFacturacion.mostrar(); break;
                    case 4: menuMonotributo.mostrar(); break;
                    case 5: consultarPorCAE(); break;
                    case 6: consultarPuntosVenta(); break;
                    case 7: resumenTodosPV(); break;
                    case 8: limpiarCredenciales(); break;
                    case 9: menuConfiguracion.mostrar(); break;
                    case 0: 
                        log.info("👋 Finalizando sistema");
                        return;
                    default:
                        System.out.println("❌ Opción inválida");
                }
            } catch (Exception e) {
                log.error("❌ Error: {}", e.getMessage());
            }
        }
    }
    
    // === ACCIONES DIRECTAS ===
    
    private void consultarPorCAE() throws Exception {
        String cae = ConsoleInput.leerCAE();
        log.info("🔍 Consultando CAE: {}", cae);
        String resultado = afipService.consultarPorCAE(cae);
        log.info("📋 Resultado: {}", resultado);
    }
    
    private void consultarPuntosVenta() throws Exception {
        log.info("🏪 Consultando puntos de venta habilitados...");
        var puntosVenta = afipService.consultarPuntosVenta();
        
        if (puntosVenta.isEmpty()) {
            log.warn("⚠️ No hay puntos de venta electrónicos habilitados");
        } else {
            log.info("✅ Puntos de venta habilitados:");
            for (Integer pv : puntosVenta) {
                log.info("   🏪 PV: {}", pv);
            }
        }
    }
    
    private void resumenTodosPV() throws Exception {
        log.info("📊 Consultando resumen de todos los PV...");
        var resumen = afipService.resumenTodosPV(11); // Factura C
        
        log.info("📋 Resumen Facturas C:");
        resumen.forEach((pv, ultimo) -> 
            log.info("   {} → Último: {}", pv, ultimo)
        );
    }
    
    private void limpiarCredenciales() {
        log.info("🧩 Limpiando credenciales...");
        afipService.limpiarCredenciales();
    }
    
    // === MÉTODOS PÚBLICOS PARA USO EXTERNO ===
    
    public AfipService getAfipService() {
        return afipService;
    }
}