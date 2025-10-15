package com.afip.billing.model;

public enum TipoComprobante {
    FACTURA_A(1, "Factura A"),
    FACTURA_B(6, "Factura B"),
    FACTURA_C(11, "Factura C (Monotributo)"),
    NOTA_DEBITO_A(2, "Nota de Débito A"),
    NOTA_DEBITO_B(7, "Nota de Débito B"),
    NOTA_CREDITO_A(3, "Nota de Crédito A"),
    NOTA_CREDITO_B(8, "Nota de Crédito B");
    
    private final int codigo;
    private final String descripcion;
    
    TipoComprobante(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }
    
    public int getCodigo() {
        return codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public static TipoComprobante fromCodigo(int codigo) {
        for (TipoComprobante tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de comprobante no válido: " + codigo);
    }
}