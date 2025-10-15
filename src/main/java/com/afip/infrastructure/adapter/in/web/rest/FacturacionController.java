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
    
    @PostMapping("/facturar")
    public CAEResponse facturar(@RequestBody FacturaRequest request) {
        // Verificar autenticación automáticamente
        if (!autenticacionRepository.tieneCredencialesValidas("wsfe")) {
            autenticacionRepository.autenticar("wsfe");
        }
        
        TipoComprobante tipo = convertirTipo(request.getTipoComprobante());
        
        CAE cae;
        if (request.getCuitCliente() == null || request.getCuitCliente() == 0) {
            cae = generarFactura.ejecutarConsumidorFinal(tipo, request.getPuntoVenta(), request.getImporte());
        } else {
            cae = generarFactura.ejecutarCliente(tipo, request.getPuntoVenta(), request.getImporte(), request.getCuitCliente());
        }
        
        return convertirCAE(cae);
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
        CAE cae = generarFactura.ejecutarConsumidorFinal(tipoComprobante, puntoVenta, new java.math.BigDecimal(importe));
        
        return convertirCAE(cae);
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
        if (cae.isExitoso()) {
            return CAEResponse.exitoso(cae.getNumero(), cae.getFechaVencimiento());
        } else {
            return CAEResponse.error(cae.getMensajeError());
        }
    }
}