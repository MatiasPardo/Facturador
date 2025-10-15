package com.afip.domain.model;

public enum TipoComprobante {
    FACTURA_A(1),
    FACTURA_B(6), 
    FACTURA_C(11);
    
    private final int codigo;
    
    TipoComprobante(int codigo) {
        this.codigo = codigo;
    }
    
    public int getCodigo() {
        return codigo;
    }
}