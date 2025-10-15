package com.afip.domain.model;

public enum TipoDocumento {
    CUIT(80),
    DNI(96),
    CONSUMIDOR_FINAL(99);
    
    private final int codigo;
    
    TipoDocumento(int codigo) {
        this.codigo = codigo;
    }
    
    public int getCodigo() {
        return codigo;
    }
}