package com.afip.domain.port.in;

import com.afip.domain.model.CAE;
import com.afip.domain.model.TipoComprobante;

import java.math.BigDecimal;

public interface GenerarFacturaUseCase {
    CAE ejecutarConsumidorFinal(String servicio, TipoComprobante tipo, int puntoVenta, BigDecimal importe);
    CAE ejecutarCliente(String servicio, TipoComprobante tipo, int puntoVenta, BigDecimal importe, long cuitCliente);
    CAE ejecutarConsumidorFinalConNumero(String servicio, TipoComprobante tipo, int puntoVenta, long numeroComprobante, BigDecimal importe);
    CAE ejecutarClienteConNumero(String servicio, TipoComprobante tipo, int puntoVenta, long numeroComprobante, BigDecimal importe, long cuitCliente);
}