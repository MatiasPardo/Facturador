package com.afip.domain.port.in;

import com.afip.domain.model.CAE;
import com.afip.domain.model.FacturaElectronica;

public interface SolicitarCAEUseCase {
    CAE ejecutar(String servicio, FacturaElectronica factura);
}