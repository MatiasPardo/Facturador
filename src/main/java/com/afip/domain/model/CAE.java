package com.afip.domain.model;

import java.time.LocalDate;

public class CAE {
    
    private final String numero;
    private final LocalDate fechaVencimiento;
    private final boolean exitoso;
    private final String mensajeError;
    
    private CAE(String numero, LocalDate fechaVencimiento, boolean exitoso, String mensajeError) {
        this.numero = numero;
        this.fechaVencimiento = fechaVencimiento;
        this.exitoso = exitoso;
        this.mensajeError = mensajeError;
    }
    
    public static CAE exitoso(String numero, LocalDate fechaVencimiento) {
        return new CAE(numero, fechaVencimiento, true, null);
    }
    
    public static CAE error(String mensajeError) {
        return new CAE(null, null, false, mensajeError);
    }
    
    public String getNumero() { return numero; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public boolean isExitoso() { return exitoso; }
    public String getMensajeError() { return mensajeError; }
}