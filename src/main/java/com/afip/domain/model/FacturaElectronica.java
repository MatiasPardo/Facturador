package com.afip.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FacturaElectronica {
    
    private final TipoComprobante tipo;
    private final int puntoVenta;
    private final long numeroComprobante;
    private final LocalDate fecha;
    private final Cliente cliente;
    private final BigDecimal importeNeto;
    private final BigDecimal importeIva;
    private final BigDecimal importeTotal;
    private final String concepto;
    
    public FacturaElectronica(TipoComprobante tipo, int puntoVenta, long numeroComprobante, 
                             LocalDate fecha, Cliente cliente, BigDecimal importeNeto, 
                             BigDecimal importeIva, BigDecimal importeTotal, String concepto) {
        this.tipo = tipo;
        this.puntoVenta = puntoVenta;
        this.numeroComprobante = numeroComprobante;
        this.fecha = fecha;
        this.cliente = cliente;
        this.importeNeto = importeNeto;
        this.importeIva = importeIva;
        this.importeTotal = importeTotal;
        this.concepto = concepto;
    }
    
    // Getters
    public TipoComprobante getTipo() { return tipo; }
    public int getPuntoVenta() { return puntoVenta; }
    public long getNumeroComprobante() { return numeroComprobante; }
    public LocalDate getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public BigDecimal getImporteNeto() { return importeNeto; }
    public BigDecimal getImporteIva() { return importeIva; }
    public BigDecimal getImporteTotal() { return importeTotal; }
    public String getConcepto() { return concepto; }
}