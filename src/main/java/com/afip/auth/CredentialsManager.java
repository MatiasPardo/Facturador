package com.afip.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class CredentialsManager {
    
    private static final Logger log = LoggerFactory.getLogger(CredentialsManager.class);
    private static final String CREDENTIALS_FILE = getCredentialsFilePath();
    
    private static String getCredentialsFilePath() {
        // En Linux usar directorio de trabajo actual, en Windows usar temp
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "afip-credentials.properties";
        } else {
            // En Linux usar directorio de trabajo actual (/opt/AFIP)
            return System.getProperty("user.dir") + "/afip-credentials.properties";
        }
    }
    
    public static void saveCredentials(String service, AfipCredentials credentials) {
        try {
            Properties props = loadAllCredentials();
            
            props.setProperty(service + ".token", credentials.getToken());
            props.setProperty(service + ".sign", credentials.getSign());
            props.setProperty(service + ".timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Guardar fecha de expiración si está disponible
            if (credentials.getExpiration() != null) {
                props.setProperty(service + ".expiration", credentials.getExpiration().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            try (FileOutputStream fos = new FileOutputStream(CREDENTIALS_FILE)) {
                props.store(fos, "AFIP Credentials - Updated at " + LocalDateTime.now());
            }
            
            log.info("💾 Credenciales guardadas para servicio {} en {}", service, CREDENTIALS_FILE);
            
        } catch (IOException e) {
            log.error("❌ Error guardando credenciales", e);
        }
    }
    
    public static AfipCredentials loadCredentials(String service) {
        try {
            Properties props = loadAllCredentials();
            
            String token = props.getProperty(service + ".token");
            String sign = props.getProperty(service + ".sign");
            String timestamp = props.getProperty(service + ".timestamp");
            String expirationStr = props.getProperty(service + ".expiration");
            
            if (token == null || sign == null) {
                log.info("📄 No hay credenciales para servicio: {}", service);
                return null;
            }
            
            // Verificar expiración
            LocalDateTime expiration = null;
            if (expirationStr != null) {
                try {
                    expiration = LocalDateTime.parse(expirationStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    if (expiration.isBefore(LocalDateTime.now().plusMinutes(5))) {
                        log.warn("⏰ Token expirado o próximo a expirar para servicio: {}", service);
                        return null;
                    }
                } catch (Exception e) {
                    log.warn("⚠️ No se pudo parsear fecha de expiración para {}", service);
                }
            }
            
            log.info("📂 Credenciales válidas cargadas para {} (expira: {})", service, expiration);
            return new AfipCredentials(token, sign, expiration);
            
        } catch (IOException e) {
            log.error("❌ Error cargando credenciales", e);
            return null;
        }
    }
    
    public static boolean hasValidCredentials(String service) {
        AfipCredentials credentials = loadCredentials(service);
        return credentials != null && !credentials.isExpired();
    }
    
    public static void clearCredentials(String service) {
        try {
            Properties props = loadAllCredentials();
            props.remove(service + ".token");
            props.remove(service + ".sign");
            props.remove(service + ".timestamp");
            
            try (FileOutputStream fos = new FileOutputStream(CREDENTIALS_FILE)) {
                props.store(fos, "AFIP Credentials - Cleared " + service + " at " + LocalDateTime.now());
            }
            
            log.info("🗑️ Credenciales eliminadas para servicio: {}", service);
            
        } catch (IOException e) {
            log.error("❌ Error eliminando credenciales", e);
        }
    }
    
    public static void clearAllCredentials() {
        try {
            Path credentialsPath = Paths.get(CREDENTIALS_FILE);
            if (Files.exists(credentialsPath)) {
                Files.delete(credentialsPath);
                log.info("🗑️ Todas las credenciales eliminadas");
            }
        } catch (IOException e) {
            log.error("❌ Error eliminando credenciales", e);
        }
    }
    
    private static Properties loadAllCredentials() throws IOException {
        Properties props = new Properties();
        Path credentialsPath = Paths.get(CREDENTIALS_FILE);
        
        if (Files.exists(credentialsPath)) {
            try (FileInputStream fis = new FileInputStream(CREDENTIALS_FILE)) {
                props.load(fis);
            }
        }
        
        return props;
    }
}