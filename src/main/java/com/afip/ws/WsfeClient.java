package com.afip.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated Usar AfipAdapter en su lugar
 * Esta clase se mantiene para compatibilidad hacia atrás
 */
@Deprecated
public class WsfeClient {
    
    private static final Logger log = LoggerFactory.getLogger(WsfeClient.class);
    
    public WsfeClient() {
        log.warn("⚠️ WsfeClient está deprecado. Usar AfipAdapter en su lugar.");
        log.info("💡 Ejemplo: AfipAdapter adapter = new AfipAdapter(...); adapter.solicitarCAE(comprobante);");
    }
    
    @Deprecated
    public String solicitarCAE(String token, String sign, String cuit) throws Exception {
        log.error("❌ Método deprecado. Usar AfipAdapter.solicitarCAE(comprobante) en su lugar.");
        throw new UnsupportedOperationException("Usar AfipAdapter en su lugar");
    }
}