package com.afip.application.usecase;

import com.afip.domain.model.*;
import com.afip.domain.port.in.FacturacionUseCase;
import com.afip.domain.port.out.AfipRepository;
import com.afip.domain.port.out.MonotributoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class FacturacionService implements FacturacionUseCase {
    
    private final AfipRepository afipRepository;
    private final MonotributoRepository monotributoRepository;
    
    public FacturacionService(AfipRepository afipRepository, MonotributoRepository monotributoRepository) {
        this.afipRepository = afipRepository;
        this.monotributoRepository = monotributoRepository;
    }
    
    @Override
    public CAE generarFactura(FacturaElectronica factura) {
        if (factura.getTipo() == TipoComprobante.FACTURA_C && esMonotributo()) {
            return monotributoRepository.solicitarCAEMonotributo(factura);
        }
        return afipRepository.solicitarCAE(factura);
    }
    
    @Override
    public CAE generarFacturaConsumidorFinal(TipoComprobante tipo, int puntoVenta, BigDecimal importe) {
        long proximoNumero = obtenerProximoNumero(puntoVenta, tipo.getCodigo());
        
        FacturaElectronica factura = new FacturaElectronica(
            tipo, puntoVenta, proximoNumero, LocalDate.now(),
            Cliente.consumidorFinal(),
            calcularImporteNeto(tipo, importe),
            calcularImporteIva(tipo, importe),
            importe,
            "Productos"
        );
        
        return generarFactura(factura);
    }
    
    @Override
    public CAE generarFacturaCliente(TipoComprobante tipo, int puntoVenta, BigDecimal importe, long cuitCliente) {
        long proximoNumero = obtenerProximoNumero(puntoVenta, tipo.getCodigo());
        
        FacturaElectronica factura = new FacturaElectronica(
            tipo, puntoVenta, proximoNumero, LocalDate.now(),
            Cliente.conCuit(cuitCliente),
            calcularImporteNeto(tipo, importe),
            calcularImporteIva(tipo, importe),
            importe,
            "Productos"
        );
        
        return generarFactura(factura);
    }
    
    @Override
    public long consultarUltimoComprobante(int puntoVenta, int tipoComprobante) {
        return afipRepository.obtenerUltimoComprobante(puntoVenta, tipoComprobante);
    }
    
    @Override
    public List<Integer> consultarPuntosVenta() {
        return afipRepository.obtenerPuntosVenta();
    }
    
    @Override
    public String consultarComprobantePorCAE(String cae) {
        return afipRepository.consultarComprobantePorCAE(cae);
    }
    
    @Override
    public Map<String, Long> consultarResumenPorTipo(int tipoComprobante) {
        return afipRepository.obtenerResumenComprobantesPorTipo(tipoComprobante);
    }
    
    private long obtenerProximoNumero(int puntoVenta, int tipoComprobante) {
        long ultimo = consultarUltimoComprobante(puntoVenta, tipoComprobante);
        return ultimo + 1;
    }
    
    private BigDecimal calcularImporteNeto(TipoComprobante tipo, BigDecimal importeTotal) {
        if (tipo == TipoComprobante.FACTURA_C) {
            return importeTotal; // Monotributo no discrimina IVA
        }
        return importeTotal.divide(new BigDecimal("1.21"), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    private BigDecimal calcularImporteIva(TipoComprobante tipo, BigDecimal importeTotal) {
        if (tipo == TipoComprobante.FACTURA_C) {
            return BigDecimal.ZERO; // Monotributo no discrimina IVA
        }
        BigDecimal neto = calcularImporteNeto(tipo, importeTotal);
        return importeTotal.subtract(neto);
    }
    
    private boolean esMonotributo() {
        // Lógica para determinar si el emisor es monotributista
        // Por ahora asumimos que sí basado en la configuración
        return true;
    }
}