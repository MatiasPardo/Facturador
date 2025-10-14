package com.afip.auth;

import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;

public class WsaaAuthenticator {
    
    private static final Logger log = LoggerFactory.getLogger(WsaaAuthenticator.class);
    
    private final String certPath;
    private final String certPassword;
    private final String certAlias;
    private final String wsaaUrl;
    
    public WsaaAuthenticator(String certPath, String certPassword, String certAlias, String wsaaUrl) {
        this.certPath = certPath;
        this.certPassword = certPassword;
        this.certAlias = certAlias;
        this.wsaaUrl = wsaaUrl;
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public AfipCredentials authenticate(String service) throws AfipAuthenticationException {
        try {
            log.info("🔐 Iniciando autenticación WSAA para servicio: {}", service);
            
            KeyStore keyStore = loadCertificate();
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(certAlias);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(certAlias, certPassword.toCharArray());
            
            log.info("✅ Certificado cargado: {}", cert.getSubjectX500Principal());
            
            String loginTicketRequest = generateLoginTicketRequest(service);
            byte[] cmsSigned = signCms(loginTicketRequest.getBytes(StandardCharsets.UTF_8), cert, privateKey);
            String signedRequest = Base64.getEncoder().encodeToString(cmsSigned);
            
            log.info("📤 Enviando request SOAP...");
            String soapEnvelope = buildSoapEnvelope(signedRequest);
            String soapResponse = sendSoapRequest(soapEnvelope);
            
            return parseWsaaResponse(soapResponse, service);
        } catch (Exception e) {
            throw new AfipAuthenticationException("Error en autenticación WSAA", e);
        }
    }
    
    private KeyStore loadCertificate() throws Exception {
        File certFile = new File(certPath);
        if (!certFile.exists() || certFile.length() == 0) {
            throw new IllegalStateException("El archivo de certificado no existe o está vacío: " + certPath);
        }
        
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(certPath), certPassword.toCharArray());
        return keyStore;
    }
    
    private String generateLoginTicketRequest(String service) throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        org.w3c.dom.Element root = doc.createElement("loginTicketRequest");
        root.setAttribute("version", "1.0");
        doc.appendChild(root);
        
        org.w3c.dom.Element header = doc.createElement("header");
        root.appendChild(header);
        
        org.w3c.dom.Element uniqueId = doc.createElement("uniqueId");
        uniqueId.setTextContent(String.valueOf(System.currentTimeMillis() / 1000 + (int)(Math.random() * 1000)));
        header.appendChild(uniqueId);
        
        org.w3c.dom.Element generationTime = doc.createElement("generationTime");
        generationTime.setTextContent(now.minusMinutes(10).format(fmt));
        header.appendChild(generationTime);
        
        org.w3c.dom.Element expirationTime = doc.createElement("expirationTime");
        expirationTime.setTextContent(now.plusMinutes(10).format(fmt));
        header.appendChild(expirationTime);
        
        org.w3c.dom.Element serviceElement = doc.createElement("service");
        serviceElement.setTextContent(service);
        root.appendChild(serviceElement);
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        
        return writer.toString();
    }
    
    private byte[] signCms(byte[] data, X509Certificate cert, PrivateKey privateKey) throws Exception {
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privateKey);
        
        generator.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(
                        new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()
                ).build(signer, cert));
        
        generator.addCertificates(new org.bouncycastle.cert.jcajce.JcaCertStore(Collections.singleton(cert)));
        
        CMSSignedData signedData = generator.generate(new CMSProcessableByteArray(data), true);
        return signedData.getEncoded();
    }
    
    private String buildSoapEnvelope(String cmsBase64) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        
        org.w3c.dom.Element envelope = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
        envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wsaa", "http://wsaa.view.sua.dvadac.desein.afip.gov");
        doc.appendChild(envelope);
        
        org.w3c.dom.Element header = doc.createElement("soapenv:Header");
        envelope.appendChild(header);
        
        org.w3c.dom.Element body = doc.createElement("soapenv:Body");
        envelope.appendChild(body);
        
        org.w3c.dom.Element loginCms = doc.createElement("wsaa:loginCms");
        body.appendChild(loginCms);
        
        org.w3c.dom.Element in0 = doc.createElement("wsaa:in0");
        in0.setTextContent(cmsBase64);
        loginCms.appendChild(in0);
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
        return outputStream.toString(StandardCharsets.UTF_8);
    }
    
    private String sendSoapRequest(String soapEnvelope) throws Exception {
        URL url = new URL(wsaaUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", "");
        
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(soapEnvelope.getBytes(StandardCharsets.UTF_8));
        }
        
        int responseCode = connection.getResponseCode();
        InputStream responseStream = (responseCode == 200)
                ? connection.getInputStream()
                : connection.getErrorStream();
        
        return new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private AfipCredentials parseWsaaResponse(String soapResponse, String service) throws AfipAuthenticationException {
        log.info("📩 Respuesta WSAA recibida");
        
        if (soapResponse.contains("alreadyAuthenticated")) {
            throw new AfipAuthenticationException("Ya tienes un token válido. Los tokens duran 12 horas.");
        }
        
        java.util.regex.Pattern tokenPattern = java.util.regex.Pattern.compile("&lt;token&gt;([^&]+)&lt;/token&gt;");
        java.util.regex.Pattern signPattern = java.util.regex.Pattern.compile("&lt;sign&gt;([^&]+)&lt;/sign&gt;");
        
        java.util.regex.Matcher tokenMatcher = tokenPattern.matcher(soapResponse);
        java.util.regex.Matcher signMatcher = signPattern.matcher(soapResponse);
        
        if (!tokenMatcher.find() || !signMatcher.find()) {
            log.error("❌ Respuesta WSAA completa para diagnóstico:\n{}", soapResponse);
            throw new AfipAuthenticationException("No se pudieron extraer token y sign de la respuesta");
        }
        
        String token = tokenMatcher.group(1);
        String sign = signMatcher.group(1);
        
        log.info("🔑 Token obtenido exitosamente");
        log.info("✍️ Firma obtenida exitosamente");
        
        AfipCredentials credentials = new AfipCredentials(token, sign);
        CredentialsManager.saveCredentials(service, credentials);
        
        return credentials;
    }
}