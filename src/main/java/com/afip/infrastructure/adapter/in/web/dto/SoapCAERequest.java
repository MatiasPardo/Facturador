package com.afip.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public class SoapCAERequest {
    
    private String token;
    private String sign;
    private int tipoComprobante;
    private int puntoVenta;
    private long numeroComprobante;
    private String fechaComprobante;
    private long cuitCliente;
    private int tipoDocumento;
    private BigDecimal importeNeto;
    private BigDecimal importeIva;
    private BigDecimal importeTotal;
    
    public SoapCAERequest() {}
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getSign() { return sign; }
    public void setSign(String sign) { this.sign = sign; }
    
    public int getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(int tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    
    public int getPuntoVenta() { return puntoVenta; }
    public void setPuntoVenta(int puntoVenta) { this.puntoVenta = puntoVenta; }
    
    public long getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(long numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    
    public String getFechaComprobante() { return fechaComprobante; }
    public void setFechaComprobante(String fechaComprobante) { this.fechaComprobante = fechaComprobante; }
    
    public long getCuitCliente() { return cuitCliente; }
    public void setCuitCliente(long cuitCliente) { this.cuitCliente = cuitCliente; }
    
    public int getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(int tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    public BigDecimal getImporteNeto() { return importeNeto; }
    public void setImporteNeto(BigDecimal importeNeto) { this.importeNeto = importeNeto; }
    
    public BigDecimal getImporteIva() { return importeIva; }
    public void setImporteIva(BigDecimal importeIva) { this.importeIva = importeIva; }
    
    public BigDecimal getImporteTotal() { return importeTotal; }
    public void setImporteTotal(BigDecimal importeTotal) { this.importeTotal = importeTotal; }
}