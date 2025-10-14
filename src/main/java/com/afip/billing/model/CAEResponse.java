package com.afip.billing.model;

import java.time.LocalDate;

public class CAEResponse {
    private String cae;
    private LocalDate fechaVencimiento;
    private boolean success;
    private String errorMessage;
    
    public CAEResponse() {}
    
    public CAEResponse(String cae, LocalDate fechaVencimiento) {
        this.cae = cae;
        this.fechaVencimiento = fechaVencimiento;
        this.success = true;
    }
    
    public CAEResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }
    
    public String getCae() {
        return cae;
    }
    
    public void setCae(String cae) {
        this.cae = cae;
    }
    
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}