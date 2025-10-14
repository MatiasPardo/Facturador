package com.afip.monotributo;

import com.afip.auth.AfipCredentials;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.Comprobante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

public class WsmtxcaService {
    
    private static final Logger log = LoggerFactory.getLogger(WsmtxcaService.class);
    
    private final String wsmtxcaUrl;
    
    public WsmtxcaService(String wsmtxcaUrl) {
        this.wsmtxcaUrl = wsmtxcaUrl;
    }
    
    public CAEResponse solicitarCAEMonotributo(Comprobante comprobante, AfipCredentials credentials) {
        try {
            log.info("📋 Solicitando CAE Monotributo para comprobante N° {}", comprobante.getNumeroComprobante());
            
            String soapRequest = buildCAEMonotributoRequest(comprobante, credentials);
            String soapResponse = sendSoapRequest(soapRequest);
            
            return parseCAEResponse(soapResponse);
            
        } catch (Exception e) {
            log.error("❌ Error al solicitar CAE Monotributo", e);
            return new CAEResponse("Error al solicitar CAE Monotributo: " + e.getMessage());
        }
    }
    
    public long obtenerUltimoComprobanteMonotributo(int puntoVenta, AfipCredentials credentials) {
        try {
            log.info("🔍 Obteniendo último comprobante Monotributo para PV {}", puntoVenta);
            
            String soapRequest = buildUltimoComprobanteMonotributoRequest(puntoVenta, credentials);
            String soapResponse = sendSoapRequest(soapRequest);
            
            return parseUltimoComprobanteResponse(soapResponse);
            
        } catch (Exception e) {
            log.error("❌ Error al obtener último comprobante Monotributo", e);
            return 0;
        }
    }
    
    private String buildCAEMonotributoRequest(Comprobante comprobante, AfipCredentials credentials) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();
        
        // SOAP Envelope
        org.w3c.dom.Element envelope = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
        envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:mtx", "http://ar.gov.afip.dif.facturaelectronica/");
        doc.appendChild(envelope);
        
        // Header
        org.w3c.dom.Element header = doc.createElement("soapenv:Header");
        envelope.appendChild(header);
        
        // Body
        org.w3c.dom.Element body = doc.createElement("soapenv:Body");
        envelope.appendChild(body);
        
        // MTXCAESolicitar
        org.w3c.dom.Element mtxCAESolicitar = doc.createElement("mtx:MTXCAESolicitar");
        body.appendChild(mtxCAESolicitar);
        
        // Auth
        org.w3c.dom.Element auth = doc.createElement("mtx:Auth");
        mtxCAESolicitar.appendChild(auth);
        
        org.w3c.dom.Element token = doc.createElement("mtx:Token");
        token.setTextContent(credentials.getToken());
        auth.appendChild(token);
        
        org.w3c.dom.Element sign = doc.createElement("mtx:Sign");
        sign.setTextContent(credentials.getSign());
        auth.appendChild(sign);
        
        org.w3c.dom.Element cuit = doc.createElement("mtx:Cuit");
        cuit.setTextContent("27362932039"); // Tu CUIT
        auth.appendChild(cuit);
        
        // MTXCAEReq
        org.w3c.dom.Element mtxCAEReq = doc.createElement("mtx:MtxCAEReq");
        mtxCAESolicitar.appendChild(mtxCAEReq);
        
        org.w3c.dom.Element mtxCabReq = doc.createElement("mtx:MtxCabReq");
        mtxCAEReq.appendChild(mtxCabReq);
        
        org.w3c.dom.Element cantReg = doc.createElement("mtx:CantReg");
        cantReg.setTextContent("1");
        mtxCabReq.appendChild(cantReg);
        
        org.w3c.dom.Element ptoVta = doc.createElement("mtx:PtoVta");
        ptoVta.setTextContent(String.valueOf(comprobante.getPuntoVenta()));
        mtxCabReq.appendChild(ptoVta);
        
        // MTXDetReq
        org.w3c.dom.Element mtxDetReq = doc.createElement("mtx:MtxDetReq");
        mtxCAEReq.appendChild(mtxDetReq);
        
        org.w3c.dom.Element mtxCAEDetRequest = doc.createElement("mtx:MTXCAEDetRequest");
        mtxDetReq.appendChild(mtxCAEDetRequest);
        
        org.w3c.dom.Element cbteDesde = doc.createElement("mtx:CbteDesde");
        cbteDesde.setTextContent(String.valueOf(comprobante.getNumeroComprobante()));
        mtxCAEDetRequest.appendChild(cbteDesde);
        
        org.w3c.dom.Element cbteHasta = doc.createElement("mtx:CbteHasta");
        cbteHasta.setTextContent(String.valueOf(comprobante.getNumeroComprobante()));
        mtxCAEDetRequest.appendChild(cbteHasta);
        
        org.w3c.dom.Element cbteFch = doc.createElement("mtx:CbteFch");
        cbteFch.setTextContent(comprobante.getFechaComprobante().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        mtxCAEDetRequest.appendChild(cbteFch);
        
        org.w3c.dom.Element impTotal = doc.createElement("mtx:ImpTotal");
        impTotal.setTextContent(comprobante.getImporteTotal().toString());
        mtxCAEDetRequest.appendChild(impTotal);
        
        // Convertir a String
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
        return outputStream.toString(StandardCharsets.UTF_8);
    }
    
    private String buildUltimoComprobanteMonotributoRequest(int puntoVenta, AfipCredentials credentials) throws Exception {
        // Implementación similar para MTXCompUltimoAutorizado
        return ""; // Placeholder
    }
    
    private String sendSoapRequest(String soapEnvelope) throws Exception {
        URL url = new URL(wsmtxcaUrl);
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
    
    private CAEResponse parseCAEResponse(String soapResponse) {
        log.info("📩 Respuesta WSMTXCA recibida");
        return new CAEResponse("Implementar parsing Monotributo");
    }
    
    private long parseUltimoComprobanteResponse(String soapResponse) {
        return 0;
    }
}