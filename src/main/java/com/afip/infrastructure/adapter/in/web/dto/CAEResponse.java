package com.afip.infrastructure.adapter.in.web.dto;

import java.time.LocalDate;

public class CAEResponse {
    
    private boolean success;
    private String cae;
    private LocalDate fechaVencimiento;
    private Long numeroComprobante;
    private String message;
    private String observaciones;
    private String afipResponse;
    
    public CAEResponse() {}
    
    public static CAEResponse exitoso(String cae, LocalDate fechaVencimiento) {
        CAEResponse response = new CAEResponse();
        response.success = true;
        response.cae = cae;
        response.fechaVencimiento = fechaVencimiento;
        response.message = "CAE generated successfully";
        return response;
    }
    
    public static CAEResponse exitoso(String cae, LocalDate fechaVencimiento, Long numeroComprobante) {
        CAEResponse response = exitoso(cae, fechaVencimiento);
        response.numeroComprobante = numeroComprobante;
        return response;
    }
    
    public static CAEResponse error(String mensaje) {
        CAEResponse response = new CAEResponse();
        response.success = false;
        response.message = mensaje;
        return response;
    }
    
    public static CAEResponse error(String mensaje, String afipResponse) {
        CAEResponse response = error(mensaje);
        response.afipResponse = afipResponse;
        return response;
    }
    
    public boolean isSuccess() { return success; }
    public String getCae() { return cae; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public Long getNumeroComprobante() { return numeroComprobante; }
    public String getMessage() { return message; }
    public String getObservaciones() { return observaciones; }
    public String getAfipResponse() { return afipResponse; }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public void setAfipResponse(String afipResponse) {
        this.afipResponse = afipResponse;
    }
    
    // Legacy getter for backward compatibility
    public boolean isExitoso() { return success; }
}