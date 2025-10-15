package com.afip.ui.menu;

import com.afip.billing.model.TipoComprobante;
import com.afip.service.AfipService;
import com.afip.ui.console.ConsoleInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MenuConsultas {
    
    private static final Logger log = LoggerFactory.getLogger(MenuConsultas.class);
    private final AfipService afipService;
    
    public MenuConsultas(AfipService afipService) {
        this.afipService = afipService;
    }
    
    public void mostrar() {
        System.out.println("\n=== TESTS DE CONSULTA ===");
        System.out.println("1. 📊 Último comprobante por tipo");
        System.out.println("2. 🏪 Puntos de venta habilitados");
        System.out.println("3. 📋 Consultar por CAE");
        System.out.println("4. 📊 Resumen todos los PV");
        System.out.println("5. 🔍 Verificar servicios AFIP");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = ConsoleInput.leerOpcion();
        
        try {
            switch (opcion) {
                case 1: consultarUltimoComprobante(); break;
                case 2: consultarPuntosVenta(); break;
                case 3: consultarPorCAE(); break;
                case 4: resumenTodosPV(); break;
                case 5: verificarServicios(); break;
            }
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
    
    private void consultarUltimoComprobante() throws Exception {
        int pv = ConsoleInput.leerPuntoVenta();
        int tipo = ConsoleInput.leerTipoComprobante();
        
        log.info("🔍 Consultando último comprobante PV {} tipo {}", pv, tipo);
        long ultimo = afipService.consultarUltimoComprobante(pv, tipo);
        log.info("📊 Último comprobante: {}", ultimo);
    }
    
    private void consultarPuntosVenta() throws Exception {
        log.info("🏪 Consultando puntos de venta habilitados...");
        List<Integer> puntosVenta = afipService.consultarPuntosVenta();
        
        if (puntosVenta.isEmpty()) {
            log.warn("⚠️ No hay puntos de venta electrónicos habilitados");
        } else {
            log.info("✅ Puntos de venta habilitados:");
            for (Integer pv : puntosVenta) {
                log.info("   🏪 PV: {}", pv);
            }
        }
    }
    
    private void consultarPorCAE() throws Exception {
        String cae = ConsoleInput.leerCAE();
        log.info("🔍 Consultando CAE: {}", cae);
        String resultado = afipService.consultarPorCAE(cae);
        log.info("📋 Resultado: {}", resultado);
    }
    
    private void resumenTodosPV() throws Exception {
        log.info("📊 Consultando resumen de todos los PV...");
        Map<String, Long> resumen = afipService.resumenTodosPV(TipoComprobante.FACTURA_C.getCodigo());
        
        log.info("📋 Resumen Facturas C:");
        resumen.forEach((pv, ultimo) -> 
            log.info("   {} → Último: {}", pv, ultimo)
        );
    }
    
    private void verificarServicios() throws Exception {
        log.info("🔍 Test: Verificar Servicios AFIP");
        afipService.verificarServicios();
    }
}