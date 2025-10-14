package com.afip.billing.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Comprobante {
    private TipoComprobante tipoComprobante;
    private int puntoVenta;
    private long numeroComprobante;
    private LocalDate fechaComprobante;
    private long cuitCliente;
    private int tipoDocumento;
    private BigDecimal importeTotal;
    private BigDecimal importeNeto;
    private BigDecimal importeIva;
    private String concepto;
    
    // Constructor
    public Comprobante() {}
    
    // Getters y Setters
    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }
    
    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }
    
    public int getPuntoVenta() {
        return puntoVenta;
    }
    
    public void setPuntoVenta(int puntoVenta) {
        this.puntoVenta = puntoVenta;
    }
    
    public long getNumeroComprobante() {
        return numeroComprobante;
    }
    
    public void setNumeroComprobante(long numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }
    
    public LocalDate getFechaComprobante() {
        return fechaComprobante;
    }
    
    public void setFechaComprobante(LocalDate fechaComprobante) {
        this.fechaComprobante = fechaComprobante;
    }
    
    public long getCuitCliente() {
        return cuitCliente;
    }
    
    public void setCuitCliente(long cuitCliente) {
        this.cuitCliente = cuitCliente;
    }
    
    public int getTipoDocumento() {
        return tipoDocumento;
    }
    
    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    public BigDecimal getImporteTotal() {
        return importeTotal;
    }
    
    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }
    
    public BigDecimal getImporteNeto() {
        return importeNeto;
    }
    
    public void setImporteNeto(BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
    }
    
    public BigDecimal getImporteIva() {
        return importeIva;
    }
    
    public void setImporteIva(BigDecimal importeIva) {
        this.importeIva = importeIva;
    }
    
    public String getConcepto() {
        return concepto;
    }
    
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
}