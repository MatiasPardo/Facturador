package com.afip.domain.port.out;

import com.afip.domain.model.CAE;
import com.afip.domain.model.FacturaElectronica;

public interface MonotributoRepository {
    
    CAE solicitarCAEMonotributo(FacturaElectronica factura);
    
    long obtenerUltimoComprobanteMonotributo(int puntoVenta);
}