package com.afip.domain.port.in;

import com.afip.domain.model.CAE;
import com.afip.domain.model.TipoComprobante;

import java.math.BigDecimal;

public interface GenerarFacturaUseCase {
    CAE ejecutarConsumidorFinal(TipoComprobante tipo, int puntoVenta, BigDecimal importe);
    CAE ejecutarCliente(TipoComprobante tipo, int puntoVenta, BigDecimal importe, long cuitCliente);
}