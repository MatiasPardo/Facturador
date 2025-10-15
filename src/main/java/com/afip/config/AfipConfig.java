package com.afip.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AfipConfig {
    
    private static Properties props = new Properties();
    
    static {
        try {
            props.load(new FileInputStream("src/main/resources/application.properties"));
        } catch (IOException e) {
            // Usar valores por defecto si no se encuentra el archivo
        }
    }
    
    // === CONFIGURACIÓN PARAMETRIZABLE ===
    
    // Certificado
    public static final String CERT_PATH = getProperty("afip.cert.path", "src/main/resources/certificates/certificado.p12");
    public static final String CERT_PASSWORD = getProperty("afip.cert.password", "clave123");
    public static final String CERT_ALIAS = getProperty("afip.cert.alias", "fulloptica");
    
    // URLs AFIP
    public static final String WSAA_URL = getProperty("afip.wsaa.url", "https://wsaa.afip.gov.ar/ws/services/LoginCms");
    public static final String WSFE_URL = getProperty("afip.wsfe.url", "https://servicios1.afip.gov.ar/wsfev1/service.asmx");
    public static final String WSMTXCA_URL = getProperty("afip.wsmtxca.url", "https://servicios1.afip.gov.ar/wsmtxca/services/MTXCAService");
    
    // Datos del Emisor (PARAMETRIZABLE)
    public static final String CUIT_EMISOR = getProperty("afip.emisor.cuit", "27362932039");
    public static final int PUNTO_VENTA_DEFAULT = Integer.parseInt(getProperty("afip.punto.venta.default", "1"));
    public static final String RAZON_SOCIAL_EMISOR = getProperty("afip.emisor.razon.social", "FULLOPTICA");
    
    // Configuración por defecto
    public static final String MONEDA_DEFAULT = getProperty("afip.moneda.default", "PES");
    public static final String COTIZACION_DEFAULT = getProperty("afip.cotizacion.default", "1.00");
    
    // Tipos de documento
    public static final int DOC_TIPO_CUIT = 80;
    public static final int DOC_TIPO_DNI = 96;
    public static final int DOC_TIPO_CONSUMIDOR_FINAL = 99;
    
    // Cliente Consumidor Final
    public static final long CUIT_CONSUMIDOR_FINAL = 0L;
    
    // Configuración de testing
    public static final String IMPORTE_PRUEBA = getProperty("afip.test.importe", "10.00");
    public static final String CUIT_CLIENTE_PRUEBA = getProperty("afip.test.cuit.cliente", "20123456789");
    
    private static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
    
    // Métodos para configuración dinámica
    public static void setCuitEmisor(String cuit) {
        props.setProperty("afip.emisor.cuit", cuit);
    }
    
    public static void setPuntoVentaDefault(int pv) {
        props.setProperty("afip.punto.venta.default", String.valueOf(pv));
    }
}