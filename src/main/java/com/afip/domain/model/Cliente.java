package com.afip.domain.model;

public class Cliente {
    
    private final long cuit;
    private final TipoDocumento tipoDocumento;
    private final boolean esConsumidorFinal;
    
    public Cliente(long cuit, TipoDocumento tipoDocumento) {
        this.cuit = cuit;
        this.tipoDocumento = tipoDocumento;
        this.esConsumidorFinal = cuit == 0;
    }
    
    public static Cliente consumidorFinal() {
        return new Cliente(0L, TipoDocumento.CONSUMIDOR_FINAL);
    }
    
    public static Cliente conCuit(long cuit) {
        return new Cliente(cuit, TipoDocumento.CUIT);
    }
    
    public long getCuit() { return cuit; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public boolean esConsumidorFinal() { return esConsumidorFinal; }
}