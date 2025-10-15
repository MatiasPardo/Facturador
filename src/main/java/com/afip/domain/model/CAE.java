package com.afip.domain.model;

import java.time.LocalDate;

public class CAE {
    
    private final String numero;
    private final LocalDate fechaVencimiento;
    private final boolean exitoso;
    private final String mensajeError;
    private final String observaciones;
    private final String respuestaAfip;
    
    private CAE(String numero, LocalDate fechaVencimiento, boolean exitoso, String mensajeError, String observaciones, String respuestaAfip) {
        this.numero = numero;
        this.fechaVencimiento = fechaVencimiento;
        this.exitoso = exitoso;
        this.mensajeError = mensajeError;
        this.observaciones = observaciones;
        this.respuestaAfip = respuestaAfip;
    }
    
    public static CAE exitoso(String numero, LocalDate fechaVencimiento) {
        return new CAE(numero, fechaVencimiento, true, null, null, null);
    }
    
    public static CAE exitoso(String numero, LocalDate fechaVencimiento, String respuestaAfip) {
        return new CAE(numero, fechaVencimiento, true, null, null, respuestaAfip);
    }
    
    public static CAE error(String mensajeError) {
        return new CAE(null, null, false, mensajeError, null, null);
    }
    
    public static CAE error(String mensajeError, String observaciones, String respuestaAfip) {
        return new CAE(null, null, false, mensajeError, observaciones, respuestaAfip);
    }
    
    public String getNumero() { return numero; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public boolean isExitoso() { return exitoso; }
    public String getMensajeError() { return mensajeError; }
    public String getObservaciones() { return observaciones; }
    public String getRespuestaAfip() { return respuestaAfip; }
}