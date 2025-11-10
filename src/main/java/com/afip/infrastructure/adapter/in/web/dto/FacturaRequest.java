package com.afip.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public class FacturaRequest {
    
    private int puntoVenta;
    private int tipoComprobante;
    private Long numeroComprobante;
    private String numeroDocumento;
    private int tipoDocumento;
    private BigDecimal importeTotal;
    private BigDecimal importeNeto;
    private BigDecimal importeIVA;
    private String fechaComprobante;
    
    public FacturaRequest() {}
    
    public int getPuntoVenta() { return puntoVenta; }
    public void setPuntoVenta(int puntoVenta) { this.puntoVenta = puntoVenta; }
    
    public int getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(int tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    
    public Long getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(Long numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    
    public int getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(int tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    public BigDecimal getImporteTotal() { return importeTotal; }
    public void setImporteTotal(BigDecimal importeTotal) { this.importeTotal = importeTotal; }
    
    public BigDecimal getImporteNeto() { return importeNeto; }
    public void setImporteNeto(BigDecimal importeNeto) { this.importeNeto = importeNeto; }
    
    public BigDecimal getImporteIVA() { return importeIVA; }
    public void setImporteIVA(BigDecimal importeIVA) { this.importeIVA = importeIVA; }
    
    public String getFechaComprobante() { return fechaComprobante; }
    public void setFechaComprobante(String fechaComprobante) { this.fechaComprobante = fechaComprobante; }
    
    // Legacy methods for backward compatibility
    public BigDecimal getImporte() { return importeTotal; }
    public Long getCuitCliente() { 
        return numeroDocumento != null ? Long.parseLong(numeroDocumento) : null; 
    }
}