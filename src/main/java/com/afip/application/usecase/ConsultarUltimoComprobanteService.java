package com.afip.application.usecase;

import com.afip.domain.port.in.ConsultarUltimoComprobanteUseCase;
import com.afip.domain.port.out.AfipRepository;

public class ConsultarUltimoComprobanteService implements ConsultarUltimoComprobanteUseCase {
    
    private final AfipRepository afipRepository;
    
    public ConsultarUltimoComprobanteService(AfipRepository afipRepository) {
        this.afipRepository = afipRepository;
    }
    
    @Override
    public long ejecutar(int puntoVenta, int tipoComprobante) {
        return afipRepository.obtenerUltimoComprobante(puntoVenta, tipoComprobante);
    }
}