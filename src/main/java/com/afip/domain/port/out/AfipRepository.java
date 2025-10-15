package com.afip.domain.port.out;

import com.afip.domain.model.CAE;
import com.afip.domain.model.FacturaElectronica;

import java.util.List;
import java.util.Map;

public interface AfipRepository {
    
    CAE solicitarCAE(FacturaElectronica factura);
    
    long obtenerUltimoComprobante(int puntoVenta, int tipoComprobante);
    
    List<Integer> obtenerPuntosVenta();
    
    String consultarComprobantePorCAE(String cae);
    
    Map<String, Long> obtenerResumenComprobantesPorTipo(int tipoComprobante);
}