package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.adapter.MonotributoAdapter;
import com.afip.auth.AfipAuthenticationException;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.TipoComprobante;
import com.afip.config.AfipConfig;
import com.afip.service.AfipService;
import com.afip.ui.FacturacionElectronicaApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Facade class for AFIP Electronic Billing
 * Delegates to the new UI architecture while maintaining backward compatibility
 */
public class FacturacionElectronica {
    
    private static final Logger log = LoggerFactory.getLogger(FacturacionElectronica.class);
    private static FacturacionElectronicaApp app;
    private static AfipService afipService;
    
    public static void main(String[] args) {
        FacturacionElectronicaApp.main(args);
    }
    
    private static void ensureInitialized() {
        if (app == null) {
            try {
                app = new FacturacionElectronicaApp();
                afipService = app.getAfipService();
                afipService.inicializar();
            } catch (Exception e) {
                log.error("Error initializing: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
    
    // === MÉTODOS PÚBLICOS PARA COMPATIBILIDAD ===
    
    public static CAEResponse facturarConsumidorFinal(BigDecimal importe) throws AfipAuthenticationException {
        ensureInitialized();
        return afipService.generarFactura(TipoComprobante.FACTURA_C, AfipConfig.PUNTO_VENTA_DEFAULT, importe, AfipConfig.CUIT_CONSUMIDOR_FINAL);
    }
    
    public static CAEResponse facturarCliente(BigDecimal importe, long cuitCliente) throws AfipAuthenticationException {
        ensureInitialized();
        return afipService.generarFactura(TipoComprobante.FACTURA_C, AfipConfig.PUNTO_VENTA_DEFAULT, importe, cuitCliente);
    }
    
    public static CAEResponse facturarRapido(BigDecimal importe) throws AfipAuthenticationException {
        return facturarConsumidorFinal(importe);
    }
    
    public static long consultarUltimoNumero() throws AfipAuthenticationException {
        ensureInitialized();
        return afipService.consultarUltimoComprobante(AfipConfig.PUNTO_VENTA_DEFAULT, TipoComprobante.FACTURA_C.getCodigo());
    }
    
    public static AfipAdapter getAdapter() {
        ensureInitialized();
        return afipService.getWsfeAdapter();
    }
    
    public static MonotributoAdapter getMonotributoAdapter() {
        ensureInitialized();
        return afipService.getMonotributoAdapter();
    }
}