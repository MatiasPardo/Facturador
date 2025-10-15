package com.afip.domain.port.in;

public interface ConsultarUltimoComprobanteUseCase {
    long ejecutar(int puntoVenta, int tipoComprobante);
}