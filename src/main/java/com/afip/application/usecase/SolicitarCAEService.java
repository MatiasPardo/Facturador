package com.afip.application.usecase;

import com.afip.domain.model.CAE;
import com.afip.domain.model.FacturaElectronica;
import com.afip.domain.port.in.SolicitarCAEUseCase;
import com.afip.domain.port.out.AfipRepository;
import com.afip.domain.port.out.MonotributoRepository;

public class SolicitarCAEService implements SolicitarCAEUseCase {
    
    private final AfipRepository afipRepository;
    private final MonotributoRepository monotributoRepository;
    
    public SolicitarCAEService(AfipRepository afipRepository, MonotributoRepository monotributoRepository) {
        this.afipRepository = afipRepository;
        this.monotributoRepository = monotributoRepository;
    }
    
    @Override
    public CAE ejecutar(String servicio, FacturaElectronica factura) {
        if ("wsmtxca".equals(servicio)) {
            return monotributoRepository.solicitarCAEMonotributo(factura);
        }
        return afipRepository.solicitarCAE(factura);
    }
    
    private boolean esMonotributo() {
        return true; // Configuración actual
    }
}