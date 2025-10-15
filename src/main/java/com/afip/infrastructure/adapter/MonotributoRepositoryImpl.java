package com.afip.infrastructure.adapter.out;

import com.afip.adapter.MonotributoAdapter;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.Comprobante;
import com.afip.domain.model.CAE;
import com.afip.domain.model.FacturaElectronica;
import com.afip.domain.port.out.MonotributoRepository;

public class MonotributoRepositoryImpl implements MonotributoRepository {
    
    private final MonotributoAdapter monotributoAdapter;
    
    public MonotributoRepositoryImpl(MonotributoAdapter monotributoAdapter) {
        this.monotributoAdapter = monotributoAdapter;
    }
    
    @Override
    public CAE solicitarCAEMonotributo(FacturaElectronica factura) {
        try {
            Comprobante comprobante = mapearAComprobante(factura);
            CAEResponse response = monotributoAdapter.solicitarCAEMonotributo(comprobante);
            
            if (response.isSuccess()) {
                return CAE.exitoso(response.getCae(), response.getFechaVencimiento());
            } else {
                return CAE.error(response.getErrorMessage());
            }
        } catch (Exception e) {
            return CAE.error("Error al solicitar CAE Monotributo: " + e.getMessage());
        }
    }
    
    @Override
    public long obtenerUltimoComprobanteMonotributo(int puntoVenta) {
        try {
            return monotributoAdapter.obtenerUltimoComprobanteMonotributo(puntoVenta);
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar último comprobante monotributo", e);
        }
    }
    
    private Comprobante mapearAComprobante(FacturaElectronica factura) {
        Comprobante comprobante = new Comprobante();
        comprobante.setTipoComprobante(convertirTipoComprobante(factura.getTipo()));
        comprobante.setPuntoVenta(factura.getPuntoVenta());
        comprobante.setNumeroComprobante(factura.getNumeroComprobante());
        comprobante.setFechaComprobante(factura.getFecha());
        comprobante.setCuitCliente(factura.getCliente().getCuit());
        comprobante.setTipoDocumento(factura.getCliente().getTipoDocumento().getCodigo());
        comprobante.setImporteTotal(factura.getImporteTotal());
        return comprobante;
    }
    
    private com.afip.billing.model.TipoComprobante convertirTipoComprobante(com.afip.domain.model.TipoComprobante domainTipo) {
        switch (domainTipo) {
            case FACTURA_A: return com.afip.billing.model.TipoComprobante.FACTURA_A;
            case FACTURA_B: return com.afip.billing.model.TipoComprobante.FACTURA_B;
            case FACTURA_C: return com.afip.billing.model.TipoComprobante.FACTURA_C;
            default: throw new IllegalArgumentException("Tipo no soportado: " + domainTipo);
        }
    }
}