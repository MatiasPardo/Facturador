package com.afip.infrastructure.adapter.in.web.rest;

import com.afip.domain.model.CAE;
import com.afip.domain.model.TipoComprobante;
import com.afip.domain.port.in.GenerarFacturaUseCase;
import com.afip.domain.port.out.AutenticacionRepository;
import com.afip.infrastructure.adapter.in.web.dto.CAEResponse;
import com.afip.infrastructure.adapter.in.web.dto.FacturaRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturacion")
public class FacturacionController {
    
    private final GenerarFacturaUseCase generarFactura;
    private final AutenticacionRepository autenticacionRepository;
    
    public FacturacionController(GenerarFacturaUseCase generarFactura,
                                AutenticacionRepository autenticacionRepository) {
        this.generarFactura = generarFactura;
        this.autenticacionRepository = autenticacionRepository;
    }
    
    @PostMapping("/generar")
    public CAEResponse generar(@RequestBody FacturaRequest request) {
        TipoComprobante tipo = convertirTipoNumerico(request.getTipoComprobante());
        
        // Determinar servicio basado en tipo de comprobante
        String servicio = determinarServicio(request.getTipoComprobante());
        
        // Verificar autenticación para el servicio correcto
        if (!autenticacionRepository.tieneCredencialesValidas(servicio)) {
            autenticacionRepository.autenticar(servicio);
        }
        
        CAE cae;
        if (request.getNumeroComprobante() != null) {
            // Usar número de comprobante proporcionado
            if (request.getCuitCliente() == null || request.getCuitCliente() == 0) {
                cae = generarFactura.ejecutarConsumidorFinalConNumero(servicio, tipo, request.getPuntoVenta(), request.getNumeroComprobante(), request.getImporteTotal());
            } else {
                cae = generarFactura.ejecutarClienteConNumero(servicio, tipo, request.getPuntoVenta(), request.getNumeroComprobante(), request.getImporteTotal(), request.getCuitCliente());
            }
        } else {
            // Calcular número automáticamente (comportamiento anterior)
            if (request.getCuitCliente() == null || request.getCuitCliente() == 0) {
                cae = generarFactura.ejecutarConsumidorFinal(servicio, tipo, request.getPuntoVenta(), request.getImporteTotal());
            } else {
                cae = generarFactura.ejecutarCliente(servicio, tipo, request.getPuntoVenta(), request.getImporteTotal(), request.getCuitCliente());
            }
        }
        
        return convertirCAE(cae);
    }
    
    @PostMapping("/facturar")
    public CAEResponse facturar(@RequestBody FacturaRequest request) {
        return generar(request);
    }
    
    @PostMapping("/consumidor-final")
    public CAEResponse facturarConsumidorFinal(@RequestParam String tipo,
                                              @RequestParam int puntoVenta,
                                              @RequestParam String importe) {
        // Auto-autenticación
        if (!autenticacionRepository.tieneCredencialesValidas("wsfe")) {
            autenticacionRepository.autenticar("wsfe");
        }
        
        TipoComprobante tipoComprobante = convertirTipo(tipo);
        CAE cae = generarFactura.ejecutarConsumidorFinal("wsfe", tipoComprobante, puntoVenta, new java.math.BigDecimal(importe));
        
        return convertirCAE(cae);
    }
    
    private TipoComprobante convertirTipoNumerico(int codigo) {
        switch (codigo) {
            case 1: return TipoComprobante.FACTURA_A;
            case 6: return TipoComprobante.FACTURA_B;
            case 11: return TipoComprobante.FACTURA_C;
            default: throw new IllegalArgumentException("Código de tipo no válido: " + codigo);
        }
    }
    
    private TipoComprobante convertirTipo(String tipo) {
        switch (tipo.toUpperCase()) {
            case "A": case "FACTURA_A": return TipoComprobante.FACTURA_A;
            case "B": case "FACTURA_B": return TipoComprobante.FACTURA_B;
            case "C": case "FACTURA_C": return TipoComprobante.FACTURA_C;
            default: throw new IllegalArgumentException("Tipo no válido: " + tipo);
        }
    }
    
    private CAEResponse convertirCAE(CAE cae) {
        CAEResponse response;
        if (cae.isExitoso()) {
            response = CAEResponse.exitoso(cae.getNumero(), cae.getFechaVencimiento());
        } else {
            response = CAEResponse.error(cae.getMensajeError());
        }
        
        // Agregar observaciones y respuesta de AFIP si están disponibles
        if (cae.getObservaciones() != null) {
            response.setObservaciones(cae.getObservaciones());
        }
        if (cae.getRespuestaAfip() != null) {
            response.setAfipResponse(cae.getRespuestaAfip());
        }
        
        return response;
    }
    
    /**
     * Determina qué servicio AFIP usar según el tipo de comprobante
     * WSFE: Facturas A, B, C para Responsables Inscriptos
     * WSMTXCA: Facturas C para Monotributistas
     */
    private String determinarServicio(int tipoComprobante) {
        switch (tipoComprobante) {
            case 1:  // Factura A - Solo WSFE (Responsable Inscripto)
            case 6:  // Factura B - Solo WSFE (Responsable Inscripto)
                return "wsfe";
            case 11: // Factura C - WSFE o WSMTXCA según categoría
                // Por ahora usar WSFE ya que el certificado no tiene permisos WSMTXCA
                return "wsfe"; // Cambiar a "wsmtxca" cuando tengas permisos
            default:
                return "wsfe";
        }
    }
}