package com.afip.ui.menu;

import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.TipoComprobante;
import com.afip.config.AfipConfig;
import com.afip.service.AfipService;
import com.afip.ui.console.ConsoleInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class MenuFacturacion {
    
    private static final Logger log = LoggerFactory.getLogger(MenuFacturacion.class);
    private final AfipService afipService;
    
    public MenuFacturacion(AfipService afipService) {
        this.afipService = afipService;
    }
    
    public void mostrar() {
        System.out.println("\n=== TESTS DE FACTURACIÓN ===");
        System.out.println("1. 📄 Factura A (Responsable Inscripto)");
        System.out.println("2. 📄 Factura B (Responsable Inscripto)");
        System.out.println("3. 📄 Factura C (Monotributo)");
        System.out.println("4. 👤 Factura Consumidor Final");
        System.out.println("5. 💰 Factura con items detallados");
        System.out.println("6. 🧪 Factura de prueba (importe bajo)");
        System.out.println("7. ⚙️ Factura personalizada");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = ConsoleInput.leerOpcion();
        
        try {
            switch (opcion) {
                case 1: testFacturaA(); break;
                case 2: testFacturaB(); break;
                case 3: testFacturaC(); break;
                case 4: testFacturaConsumidorFinal(); break;
                case 5: testFacturaConItems(); break;
                case 6: testFacturaPrueba(); break;
                case 7: testFacturaPersonalizada(); break;
            }
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
    
    private void testFacturaA() throws Exception {
        log.info("📄 Test: Factura A");
        CAEResponse cae = afipService.generarFactura(
            TipoComprobante.FACTURA_A, 
            AfipConfig.PUNTO_VENTA_DEFAULT, 
            new BigDecimal("1210.00"), 
            Long.parseLong(AfipConfig.CUIT_CLIENTE_PRUEBA)
        );
        mostrarResultado(cae);
    }
    
    private void testFacturaB() throws Exception {
        log.info("📄 Test: Factura B");
        CAEResponse cae = afipService.generarFactura(
            TipoComprobante.FACTURA_B, 
            AfipConfig.PUNTO_VENTA_DEFAULT, 
            new BigDecimal("1210.00"), 
            Long.parseLong(AfipConfig.CUIT_CLIENTE_PRUEBA)
        );
        mostrarResultado(cae);
    }
    
    private void testFacturaC() throws Exception {
        log.info("📄 Test: Factura C");
        BigDecimal importe = ConsoleInput.leerImporte();
        CAEResponse cae = afipService.generarFactura(
            TipoComprobante.FACTURA_C, 
            AfipConfig.PUNTO_VENTA_DEFAULT, 
            importe, 
            AfipConfig.CUIT_CONSUMIDOR_FINAL
        );
        mostrarResultado(cae);
    }
    
    private void testFacturaConsumidorFinal() throws Exception {
        log.info("👤 Test: Factura Consumidor Final");
        CAEResponse cae = afipService.generarFactura(
            TipoComprobante.FACTURA_C, 
            AfipConfig.PUNTO_VENTA_DEFAULT, 
            new BigDecimal("250.00"), 
            AfipConfig.CUIT_CONSUMIDOR_FINAL
        );
        mostrarResultado(cae);
    }
    
    private void testFacturaConItems() throws Exception {
        log.info("💰 Test: Factura con Items");
        int cantItems = ConsoleInput.leerCantidadItems();
        BigDecimal importeTotal = new BigDecimal(cantItems * 100);
        
        CAEResponse cae = afipService.generarFactura(
            TipoComprobante.FACTURA_C, 
            AfipConfig.PUNTO_VENTA_DEFAULT, 
            importeTotal, 
            AfipConfig.CUIT_CONSUMIDOR_FINAL
        );
        mostrarResultado(cae);
    }
    
    private void testFacturaPrueba() throws Exception {
        log.info("🧪 Test: Factura Prueba");
        CAEResponse cae = afipService.generarFactura(
            TipoComprobante.FACTURA_C, 
            AfipConfig.PUNTO_VENTA_DEFAULT, 
            new BigDecimal(AfipConfig.IMPORTE_PRUEBA), 
            AfipConfig.CUIT_CONSUMIDOR_FINAL
        );
        mostrarResultado(cae);
    }
    
    private void testFacturaPersonalizada() throws Exception {
        log.info("⚙️ Test: Factura Personalizada");
        
        int pv = ConsoleInput.leerPuntoVenta();
        int tipoCode = ConsoleInput.leerTipoComprobante();
        TipoComprobante tipo = TipoComprobante.fromCodigo(tipoCode);
        BigDecimal importe = ConsoleInput.leerImporte();
        long cuit = ConsoleInput.leerCuitCliente();
        
        CAEResponse cae = afipService.generarFactura(tipo, pv, importe, cuit);
        mostrarResultado(cae);
    }
    
    private void mostrarResultado(CAEResponse cae) {
        if (cae.isSuccess()) {
            log.info("✅ ¡FACTURA GENERADA EXITOSAMENTE!");
            log.info("   🔐 CAE: {}", cae.getCae());
            log.info("   📅 Vence: {}", cae.getFechaVencimiento());
        } else {
            log.error("❌ Error: {}", cae.getErrorMessage());
        }
    }
}