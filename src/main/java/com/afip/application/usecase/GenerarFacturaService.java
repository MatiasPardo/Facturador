package com.afip.application.usecase;

import com.afip.domain.model.*;
import com.afip.domain.port.in.ConsultarUltimoComprobanteUseCase;
import com.afip.domain.port.in.GenerarFacturaUseCase;
import com.afip.domain.port.in.SolicitarCAEUseCase;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GenerarFacturaService implements GenerarFacturaUseCase {
    
    private final ConsultarUltimoComprobanteUseCase consultarUltimoComprobante;
    private final SolicitarCAEUseCase solicitarCAE;
    
    public GenerarFacturaService(ConsultarUltimoComprobanteUseCase consultarUltimoComprobante, 
                                SolicitarCAEUseCase solicitarCAE) {
        this.consultarUltimoComprobante = consultarUltimoComprobante;
        this.solicitarCAE = solicitarCAE;
    }
    
    @Override
    public CAE ejecutarConsumidorFinal(TipoComprobante tipo, int puntoVenta, BigDecimal importe) {
        long proximoNumero = obtenerProximoNumero(puntoVenta, tipo.getCodigo());
        
        FacturaElectronica factura = new FacturaElectronica(
            tipo, puntoVenta, proximoNumero, LocalDate.now(),
            Cliente.consumidorFinal(),
            calcularImporteNeto(tipo, importe),
            calcularImporteIva(tipo, importe),
            importe,
            "Productos"
        );
        
        return solicitarCAE.ejecutar(factura);
    }
    
    @Override
    public CAE ejecutarCliente(TipoComprobante tipo, int puntoVenta, BigDecimal importe, long cuitCliente) {
        long proximoNumero = obtenerProximoNumero(puntoVenta, tipo.getCodigo());
        
        FacturaElectronica factura = new FacturaElectronica(
            tipo, puntoVenta, proximoNumero, LocalDate.now(),
            Cliente.conCuit(cuitCliente),
            calcularImporteNeto(tipo, importe),
            calcularImporteIva(tipo, importe),
            importe,
            "Productos"
        );
        
        return solicitarCAE.ejecutar(factura);
    }
    
    private long obtenerProximoNumero(int puntoVenta, int tipoComprobante) {
        long ultimo = consultarUltimoComprobante.ejecutar(puntoVenta, tipoComprobante);
        return ultimo + 1;
    }
    
    private BigDecimal calcularImporteNeto(TipoComprobante tipo, BigDecimal importeTotal) {
        if (tipo == TipoComprobante.FACTURA_C) {
            return importeTotal;
        }
        return importeTotal.divide(new BigDecimal("1.21"), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    private BigDecimal calcularImporteIva(TipoComprobante tipo, BigDecimal importeTotal) {
        if (tipo == TipoComprobante.FACTURA_C) {
            return BigDecimal.ZERO;
        }
        BigDecimal neto = calcularImporteNeto(tipo, importeTotal);
        return importeTotal.subtract(neto);
    }
}