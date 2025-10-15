package com.afip.infrastructure.adapter.in.web.dto;

import java.time.LocalDate;

public class CAEResponse {
    
    private boolean exitoso;
    private String cae;
    private LocalDate fechaVencimiento;
    private String error;
    
    public CAEResponse() {}
    
    public static CAEResponse exitoso(String cae, LocalDate fechaVencimiento) {
        CAEResponse response = new CAEResponse();
        response.exitoso = true;
        response.cae = cae;
        response.fechaVencimiento = fechaVencimiento;
        return response;
    }
    
    public static CAEResponse error(String mensaje) {
        CAEResponse response = new CAEResponse();
        response.exitoso = false;
        response.error = mensaje;
        return response;
    }
    
    public boolean isExitoso() { return exitoso; }
    public String getCae() { return cae; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public String getError() { return error; }
}