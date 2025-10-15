package com.afip.infrastructure.adapter;

import com.afip.adapter.AfipAdapter;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.Comprobante;
import com.afip.domain.model.CAE;
import com.afip.domain.model.FacturaElectronica;
import com.afip.domain.port.out.AfipRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AfipRepositoryImpl implements AfipRepository {
    
    private final AfipAdapter afipAdapter;
    
    public AfipRepositoryImpl(AfipAdapter afipAdapter) {
        this.afipAdapter = afipAdapter;
    }
    
    @Override
    public CAE solicitarCAE(FacturaElectronica factura) {
        try {
            Comprobante comprobante = mapearAComprobante(factura);
            CAEResponse response = afipAdapter.solicitarCAE(comprobante);
            
            if (response.isSuccess()) {
                return CAE.exitoso(response.getCae(), response.getFechaVencimiento());
            } else {
                return CAE.error(response.getErrorMessage());
            }
        } catch (Exception e) {
            return CAE.error("Error al solicitar CAE: " + e.getMessage());
        }
    }
    
    @Override
    public long obtenerUltimoComprobante(int puntoVenta, int tipoComprobante) {
        try {
            return afipAdapter.obtenerUltimoComprobante(puntoVenta, tipoComprobante);
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar último comprobante", e);
        }
    }
    
    @Override
    public List<Integer> obtenerPuntosVenta() {
        try {
            return afipAdapter.consultarPuntosVenta();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar puntos de venta", e);
        }
    }
    
    @Override
    public String consultarComprobantePorCAE(String cae) {
        try {
            return afipAdapter.consultarComprobantePorCAE(cae);
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar CAE", e);
        }
    }
    
    @Override
    public Map<String, Long> obtenerResumenComprobantesPorTipo(int tipoComprobante) {
        try {
            return afipAdapter.consultarUltimosComprobantesPorPV(tipoComprobante);
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar resumen", e);
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
        comprobante.setImporteNeto(factura.getImporteNeto());
        comprobante.setImporteIva(factura.getImporteIva());
        comprobante.setImporteTotal(factura.getImporteTotal());
        comprobante.setConcepto(factura.getConcepto());
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