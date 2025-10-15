package com.afip.service;

import com.afip.adapter.AfipAdapter;
import com.afip.adapter.MonotributoAdapter;
import com.afip.application.usecase.*;
import com.afip.auth.AfipAuthenticationException;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.TipoComprobante;
import com.afip.config.AfipConfig;
import com.afip.domain.model.CAE;
import com.afip.domain.port.in.*;
import com.afip.infrastructure.adapter.AfipRepositoryImpl;
import com.afip.infrastructure.adapter.AutenticacionRepositoryImpl;
import com.afip.infrastructure.adapter.MonotributoRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AfipService {
    
    private static final Logger log = LoggerFactory.getLogger(AfipService.class);
    private AfipAdapter wsfeAdapter;
    private MonotributoAdapter monotributoAdapter;
    private AutenticacionRepositoryImpl autenticacionRepository;
    
    // Casos de uso específicos
    private ConsultarPuntosVentaUseCase consultarPuntosVenta;
    private ConsultarUltimoComprobanteUseCase consultarUltimoComprobante;
    private ConsultarCAEUseCase consultarCAE;
    private SolicitarCAEUseCase solicitarCAE;
    private GenerarFacturaUseCase generarFactura;
    private AutenticarWSFEUseCase autenticarWSFE;
    private AutenticarWSMTXCAUseCase autenticarWSMTXCA;
    
    public void inicializar() throws AfipAuthenticationException {
        log.info("🚀 Inicializando servicios AFIP");
        
        // Crear adapters de infraestructura
        wsfeAdapter = new AfipAdapter(
            AfipConfig.CERT_PATH, AfipConfig.CERT_PASSWORD, AfipConfig.CERT_ALIAS,
            AfipConfig.WSAA_URL, AfipConfig.WSFE_URL
        );
        
        monotributoAdapter = new MonotributoAdapter(
            AfipConfig.CERT_PATH, AfipConfig.CERT_PASSWORD, AfipConfig.CERT_ALIAS,
            AfipConfig.WSAA_URL, AfipConfig.WSMTXCA_URL
        );
        
        // Crear repositorios
        AfipRepositoryImpl afipRepository = new AfipRepositoryImpl(wsfeAdapter);
        MonotributoRepositoryImpl monotributoRepository = new MonotributoRepositoryImpl(monotributoAdapter);
        this.autenticacionRepository = new AutenticacionRepositoryImpl(wsfeAdapter, monotributoAdapter);
        
        // Crear casos de uso específicos
        consultarPuntosVenta = new ConsultarPuntosVentaService(afipRepository);
        consultarUltimoComprobante = new ConsultarUltimoComprobanteService(afipRepository);
        consultarCAE = new ConsultarCAEService(afipRepository);
        solicitarCAE = new SolicitarCAEService(afipRepository, monotributoRepository);
        generarFactura = new GenerarFacturaService(consultarUltimoComprobante, solicitarCAE);
        autenticarWSFE = new AutenticarWSFEService(this.autenticacionRepository);
        autenticarWSMTXCA = new AutenticarWSMTXCAService(this.autenticacionRepository);
        
        // Autenticar
        wsfeAdapter.authenticate();
        log.info("✅ Servicios AFIP listos");
    }
    
    // === AUTENTICACIÓN ===
    
    public void autenticarWSFE() throws AfipAuthenticationException {
        try {
            autenticarWSFE.ejecutar();
            log.info("✅ Autenticación WSFE exitosa");
        } catch (Exception e) {
            throw new AfipAuthenticationException("Error en autenticación WSFE", e);
        }
    }
    
    public void autenticarWSMTXCA() throws AfipAuthenticationException {
        try {
            autenticarWSMTXCA.ejecutar();
            log.info("✅ Autenticación WSMTXCA exitosa");
        } catch (Exception e) {
            throw new AfipAuthenticationException("Error en autenticación WSMTXCA", e);
        }
    }
    
    public void verificarCredenciales() {
        log.info("WSFE válido: {}", autenticacionRepository.tieneCredencialesValidas("wsfe"));
        log.info("WSMTXCA válido: {}", autenticacionRepository.tieneCredencialesValidas("wsmtxca"));
    }
    
    // === CONSULTAS ===
    
    public long consultarUltimoComprobante(int puntoVenta, int tipoComprobante) throws AfipAuthenticationException {
        try {
            return consultarUltimoComprobante.ejecutar(puntoVenta, tipoComprobante);
        } catch (Exception e) {
            throw new AfipAuthenticationException("Error consultando último comprobante", e);
        }
    }
    
    public List<Integer> consultarPuntosVenta() throws AfipAuthenticationException {
        try {
            return consultarPuntosVenta.ejecutar();
        } catch (Exception e) {
            throw new AfipAuthenticationException("Error consultando puntos de venta", e);
        }
    }
    
    public String consultarPorCAE(String cae) throws AfipAuthenticationException {
        try {
            return consultarCAE.ejecutar(cae);
        } catch (Exception e) {
            throw new AfipAuthenticationException("Error consultando CAE", e);
        }
    }
    
    public Map<String, Long> resumenTodosPV(int tipoComprobante) throws AfipAuthenticationException {
        try {
            return wsfeAdapter.consultarUltimosComprobantesPorPV(tipoComprobante);
        } catch (Exception e) {
            throw new AfipAuthenticationException("Error consultando resumen", e);
        }
    }
    
    // === FACTURACIÓN ===
    
    public CAEResponse generarFactura(TipoComprobante tipo, int puntoVenta, BigDecimal importe, long cuitCliente) throws AfipAuthenticationException {
        try {
            com.afip.domain.model.TipoComprobante domainTipo = convertirTipoDominio(tipo);
            String servicio = determinarServicio(tipo.getCodigo());
            
            CAE cae;
            if (cuitCliente == 0) {
                cae = generarFactura.ejecutarConsumidorFinal(servicio, domainTipo, puntoVenta, importe);
            } else {
                cae = generarFactura.ejecutarCliente(servicio, domainTipo, puntoVenta, importe, cuitCliente);
            }
            
            return convertirCAE(cae);
        } catch (Exception e) {
            throw new AfipAuthenticationException("Error generando factura", e);
        }
    }
    
    public CAEResponse generarComprobanteMonotributo(BigDecimal importe) throws AfipAuthenticationException {
        try {
            String servicio = determinarServicio(11); // Factura C
            CAE cae = generarFactura.ejecutarConsumidorFinal(
                servicio,
                com.afip.domain.model.TipoComprobante.FACTURA_C, 
                AfipConfig.PUNTO_VENTA_DEFAULT, 
                importe
            );
            return convertirCAE(cae);
        } catch (Exception e) {
            throw new AfipAuthenticationException("Error generando comprobante monotributo", e);
        }
    }
    
    public long consultarUltimoComprobanteMonotributo(int puntoVenta) throws AfipAuthenticationException {
        return monotributoAdapter.obtenerUltimoComprobanteMonotributo(puntoVenta);
    }
    
    // === UTILIDADES ===
    
    public void limpiarCredenciales() {
        try {
            autenticacionRepository.limpiarCredenciales();
            log.info("✅ Credenciales eliminadas");
        } catch (Exception e) {
            log.error("❌ Error limpiando credenciales: {}", e.getMessage());
        }
    }
    
    public void verificarServicios() throws AfipAuthenticationException {
        try {
            List<Integer> puntosVenta = consultarPuntosVenta.ejecutar();
            log.info("✅ WSFE operativo - PVs: {}", puntosVenta.size());
        } catch (Exception e) {
            log.error("❌ WSFE no disponible: {}", e.getMessage());
        }
        
        try {
            long ultimo = monotributoAdapter.obtenerUltimoComprobanteMonotributo(AfipConfig.PUNTO_VENTA_DEFAULT);
            log.info("✅ WSMTXCA operativo - Último: {}", ultimo);
        } catch (Exception e) {
            log.error("❌ WSMTXCA no disponible: {}", e.getMessage());
        }
    }
    
    private com.afip.domain.model.TipoComprobante convertirTipoDominio(TipoComprobante billingTipo) {
        switch (billingTipo) {
            case FACTURA_A: return com.afip.domain.model.TipoComprobante.FACTURA_A;
            case FACTURA_B: return com.afip.domain.model.TipoComprobante.FACTURA_B;
            case FACTURA_C: return com.afip.domain.model.TipoComprobante.FACTURA_C;
            default: throw new IllegalArgumentException("Tipo no soportado: " + billingTipo);
        }
    }
    
    private CAEResponse convertirCAE(CAE cae) {
        if (cae.isExitoso()) {
            return new CAEResponse(cae.getNumero(), cae.getFechaVencimiento());
        } else {
            return new CAEResponse(cae.getMensajeError());
        }
    }
    
    // === GETTERS ===
    
    public AfipAdapter getWsfeAdapter() {
        return wsfeAdapter;
    }
    
    public MonotributoAdapter getMonotributoAdapter() {
        return monotributoAdapter;
    }
    
    private String determinarServicio(int tipoComprobante) {
        switch (tipoComprobante) {
            case 1:  // Factura A - Solo WSFE
            case 6:  // Factura B - Solo WSFE
                return "wsfe";
            case 11: // Factura C - WSFE por limitaciones del certificado
                return "wsfe"; // Cambiar a "wsmtxca" cuando tengas permisos
            default:
                return "wsfe";
        }
    }
}