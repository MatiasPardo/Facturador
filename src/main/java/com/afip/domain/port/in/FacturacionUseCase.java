package com.afip.domain.port.in;

import com.afip.domain.model.CAE;
import com.afip.domain.model.FacturaElectronica;
import com.afip.domain.model.TipoComprobante;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FacturacionUseCase {
    
    CAE generarFactura(FacturaElectronica factura);
    
    CAE generarFacturaConsumidorFinal(TipoComprobante tipo, int puntoVenta, BigDecimal importe);
    
    CAE generarFacturaCliente(TipoComprobante tipo, int puntoVenta, BigDecimal importe, long cuitCliente);
    
    long consultarUltimoComprobante(int puntoVenta, int tipoComprobante);
    
    List<Integer> consultarPuntosVenta();
    
    String consultarComprobantePorCAE(String cae);
    
    Map<String, Long> consultarResumenPorTipo(int tipoComprobante);
}