package com.afip.ws;

import com.afip.adapter.AfipAdapter;
import com.afip.auth.AfipAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated Usar AfipAdapter en su lugar
 * Esta clase se mantiene para compatibilidad hacia atrás
 */
@Deprecated
public class WsaaClient {

    private static final Logger log = LoggerFactory.getLogger(WsaaClient.class);

    private static final String CERT_PATH = "src/main/resources/certificates/certificado.p12";
    private static final String CERT_PASSWORD = "clave123";
    private static final String CERT_ALIAS = "fulloptica";
    private static final String WSAA_URL = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private static final String WSFE_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";

    public static void main(String[] args) {
        log.warn("⚠️ WsaaClient está deprecado. Usar AfipAdapter en su lugar.");
        log.info("🔄 Redirigiendo a la nueva implementación...");
        
        try {
            AfipAdapter adapter = new AfipAdapter(CERT_PATH, CERT_PASSWORD, CERT_ALIAS, WSAA_URL, WSFE_URL);
            adapter.authenticate();
            log.info("✅ Autenticación exitosa usando AfipAdapter");
        } catch (AfipAuthenticationException e) {
            log.error("❌ Error de autenticación: {}", e.getMessage());
        }
    }
}