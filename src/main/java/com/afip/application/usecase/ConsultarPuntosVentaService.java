package com.afip.application.usecase;

import com.afip.domain.port.in.ConsultarPuntosVentaUseCase;
import com.afip.domain.port.out.AfipRepository;

import java.util.List;

public class ConsultarPuntosVentaService implements ConsultarPuntosVentaUseCase {
    
    private final AfipRepository afipRepository;
    
    public ConsultarPuntosVentaService(AfipRepository afipRepository) {
        this.afipRepository = afipRepository;
    }
    
    @Override
    public List<Integer> ejecutar() {
        return afipRepository.obtenerPuntosVenta();
    }
}