package com.afip.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public class FacturaRequest {
    
    private String tipoComprobante;
    private int puntoVenta;
    private BigDecimal importe;
    private Long cuitCliente;
    private String concepto;
    
    public FacturaRequest() {}
    
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    
    public int getPuntoVenta() { return puntoVenta; }
    public void setPuntoVenta(int puntoVenta) { this.puntoVenta = puntoVenta; }
    
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    
    public Long getCuitCliente() { return cuitCliente; }
    public void setCuitCliente(Long cuitCliente) { this.cuitCliente = cuitCliente; }
    
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
}