package com.afip.billing;

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

public class WsfeService {
    
    private static final Logger log = LoggerFactory.getLogger(WsfeService.class);
    
    private final String wsfeUrl;
    
    public WsfeService(String wsfeUrl) {
        this.wsfeUrl = wsfeUrl;
    }
    
    public CAEResponse solicitarCAE(Comprobante comprobante, AfipCredentials credentials) {
        try {
            log.info("📋 Solicitando CAE para comprobante tipo {} N° {}", 
                    comprobante.getTipoComprobante().getDescripcion(), 
                    comprobante.getNumeroComprobante());
            
            String soapRequest = buildCAERequest(comprobante, credentials);
            String soapResponse = sendSoapRequest(soapRequest, "http://ar.gov.afip.dif.FEV1/FECAESolicitar");
            
            return parseCAEResponse(soapResponse);
            
        } catch (Exception e) {
            log.error("❌ Error al solicitar CAE", e);
            return new CAEResponse("Error al solicitar CAE: " + e.getMessage());
        }
    }
    
    public long obtenerUltimoComprobante(int puntoVenta, int tipoComprobante, AfipCredentials credentials) {
        try {
            log.info("🔍 Obteniendo último comprobante para PV {} tipo {}", puntoVenta, tipoComprobante);
            
            String soapRequest = buildUltimoComprobanteRequest(puntoVenta, tipoComprobante, credentials);
            String soapResponse = sendSoapRequest(soapRequest, "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado");
            
            return parseUltimoComprobanteResponse(soapResponse);
            
        } catch (Exception e) {
            log.error("❌ Error al obtener último comprobante", e);
            return 0;
        }
    }
    
    public java.util.List<Integer> consultarPuntosVenta(AfipCredentials credentials) {
        try {
            log.info("🏪 Consultando puntos de venta habilitados");
            
            String soapRequest = buildPuntosVentaRequest(credentials);
            String soapResponse = sendSoapRequest(soapRequest, "http://ar.gov.afip.dif.FEV1/FEParamGetPtosVenta");
            
            return parsePuntosVentaResponse(soapResponse);
            
        } catch (Exception e) {
            log.error("❌ Error al consultar puntos de venta", e);
            return java.util.Collections.emptyList();
        }
    }
    
    public String consultarComprobantePorCAE(String cae, AfipCredentials credentials) {
        try {
            log.info("🔍 Consultando comprobante por CAE: {}", cae);
            
            String soapRequest = buildConsultaCAERequest(cae, credentials);
            String soapResponse = sendSoapRequest(soapRequest, "http://ar.gov.afip.dif.FEV1/FECompConsultar");
            
            return parseConsultaCAEResponse(soapResponse);
            
        } catch (Exception e) {
            log.error("❌ Error al consultar CAE", e);
            return "Error: " + e.getMessage();
        }
    }
    
    private String buildCAERequest(Comprobante comprobante, AfipCredentials credentials) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();
        
        // SOAP Envelope
        org.w3c.dom.Element envelope = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
        envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wsfe", "http://ar.gov.afip.dif.FEV1/");
        doc.appendChild(envelope);
        
        // Header
        org.w3c.dom.Element header = doc.createElement("soapenv:Header");
        envelope.appendChild(header);
        
        // Body
        org.w3c.dom.Element body = doc.createElement("soapenv:Body");
        envelope.appendChild(body);
        
        // FECAESolicitar
        org.w3c.dom.Element feCAESolicitar = doc.createElement("wsfe:FECAESolicitar");
        body.appendChild(feCAESolicitar);
        
        // Auth
        org.w3c.dom.Element auth = doc.createElement("wsfe:Auth");
        feCAESolicitar.appendChild(auth);
        
        org.w3c.dom.Element token = doc.createElement("wsfe:Token");
        token.setTextContent(credentials.getToken());
        auth.appendChild(token);
        
        org.w3c.dom.Element sign = doc.createElement("wsfe:Sign");
        sign.setTextContent(credentials.getSign());
        auth.appendChild(sign);
        
        org.w3c.dom.Element cuit = doc.createElement("wsfe:Cuit");
        cuit.setTextContent("27362932039"); // Tu CUIT
        auth.appendChild(cuit);
        
        // FeCAEReq
        org.w3c.dom.Element feCAEReq = doc.createElement("wsfe:FeCAEReq");
        feCAESolicitar.appendChild(feCAEReq);
        
        org.w3c.dom.Element feCabReq = doc.createElement("wsfe:FeCabReq");
        feCAEReq.appendChild(feCabReq);
        
        org.w3c.dom.Element cantReg = doc.createElement("wsfe:CantReg");
        cantReg.setTextContent("1");
        feCabReq.appendChild(cantReg);
        
        org.w3c.dom.Element ptoVta = doc.createElement("wsfe:PtoVta");
        ptoVta.setTextContent(String.valueOf(comprobante.getPuntoVenta()));
        feCabReq.appendChild(ptoVta);
        
        org.w3c.dom.Element cbteTipo = doc.createElement("wsfe:CbteTipo");
        cbteTipo.setTextContent(String.valueOf(comprobante.getTipoComprobante().getCodigo()));
        feCabReq.appendChild(cbteTipo);
        
        // FeDetReq
        org.w3c.dom.Element feDetReq = doc.createElement("wsfe:FeDetReq");
        feCAEReq.appendChild(feDetReq);
        
        org.w3c.dom.Element feCAEDetRequest = doc.createElement("wsfe:FECAEDetRequest");
        feDetReq.appendChild(feCAEDetRequest);
        
        org.w3c.dom.Element concepto = doc.createElement("wsfe:Concepto");
        concepto.setTextContent("1"); // Productos
        feCAEDetRequest.appendChild(concepto);
        
        org.w3c.dom.Element docTipo = doc.createElement("wsfe:DocTipo");
        docTipo.setTextContent(String.valueOf(comprobante.getTipoDocumento()));
        feCAEDetRequest.appendChild(docTipo);
        
        org.w3c.dom.Element docNro = doc.createElement("wsfe:DocNro");
        docNro.setTextContent(String.valueOf(comprobante.getCuitCliente()));
        feCAEDetRequest.appendChild(docNro);
        
        org.w3c.dom.Element cbteDesde = doc.createElement("wsfe:CbteDesde");
        cbteDesde.setTextContent(String.valueOf(comprobante.getNumeroComprobante()));
        feCAEDetRequest.appendChild(cbteDesde);
        
        org.w3c.dom.Element cbteHasta = doc.createElement("wsfe:CbteHasta");
        cbteHasta.setTextContent(String.valueOf(comprobante.getNumeroComprobante()));
        feCAEDetRequest.appendChild(cbteHasta);
        
        org.w3c.dom.Element cbteFch = doc.createElement("wsfe:CbteFch");
        cbteFch.setTextContent(comprobante.getFechaComprobante().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        feCAEDetRequest.appendChild(cbteFch);
        
        org.w3c.dom.Element impTotal = doc.createElement("wsfe:ImpTotal");
        impTotal.setTextContent(comprobante.getImporteTotal().toString());
        feCAEDetRequest.appendChild(impTotal);
        
        org.w3c.dom.Element impTotConc = doc.createElement("wsfe:ImpTotConc");
        impTotConc.setTextContent("0.00");
        feCAEDetRequest.appendChild(impTotConc);
        
        org.w3c.dom.Element impNeto = doc.createElement("wsfe:ImpNeto");
        impNeto.setTextContent(comprobante.getImporteNeto().toString());
        feCAEDetRequest.appendChild(impNeto);
        
        org.w3c.dom.Element impOpEx = doc.createElement("wsfe:ImpOpEx");
        impOpEx.setTextContent("0.00");
        feCAEDetRequest.appendChild(impOpEx);
        
        org.w3c.dom.Element impIVA = doc.createElement("wsfe:ImpIVA");
        impIVA.setTextContent(comprobante.getImporteIva().toString());
        feCAEDetRequest.appendChild(impIVA);
        
        org.w3c.dom.Element impTrib = doc.createElement("wsfe:ImpTrib");
        impTrib.setTextContent("0.00");
        feCAEDetRequest.appendChild(impTrib);
        
        org.w3c.dom.Element monId = doc.createElement("wsfe:MonId");
        monId.setTextContent("PES");
        feCAEDetRequest.appendChild(monId);
        
        org.w3c.dom.Element monCotiz = doc.createElement("wsfe:MonCotiz");
        monCotiz.setTextContent("1.00");
        feCAEDetRequest.appendChild(monCotiz);
        
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
    
    private String buildUltimoComprobanteRequest(int puntoVenta, int tipoComprobante, AfipCredentials credentials) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();
        
        // SOAP Envelope
        org.w3c.dom.Element envelope = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
        envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wsfe", "http://ar.gov.afip.dif.FEV1/");
        doc.appendChild(envelope);
        
        // Header
        org.w3c.dom.Element header = doc.createElement("soapenv:Header");
        envelope.appendChild(header);
        
        // Body
        org.w3c.dom.Element body = doc.createElement("soapenv:Body");
        envelope.appendChild(body);
        
        // FECompUltimoAutorizado
        org.w3c.dom.Element feCompUltimo = doc.createElement("wsfe:FECompUltimoAutorizado");
        body.appendChild(feCompUltimo);
        
        // Auth
        org.w3c.dom.Element auth = doc.createElement("wsfe:Auth");
        feCompUltimo.appendChild(auth);
        
        org.w3c.dom.Element token = doc.createElement("wsfe:Token");
        token.setTextContent(credentials.getToken());
        auth.appendChild(token);
        
        org.w3c.dom.Element sign = doc.createElement("wsfe:Sign");
        sign.setTextContent(credentials.getSign());
        auth.appendChild(sign);
        
        org.w3c.dom.Element cuit = doc.createElement("wsfe:Cuit");
        cuit.setTextContent("27362932039");
        auth.appendChild(cuit);
        
        // Parámetros
        org.w3c.dom.Element ptoVta = doc.createElement("wsfe:PtoVta");
        ptoVta.setTextContent(String.valueOf(puntoVenta));
        feCompUltimo.appendChild(ptoVta);
        
        org.w3c.dom.Element cbteTipo = doc.createElement("wsfe:CbteTipo");
        cbteTipo.setTextContent(String.valueOf(tipoComprobante));
        feCompUltimo.appendChild(cbteTipo);
        
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
    
    private String sendSoapRequest(String soapEnvelope, String soapAction) throws Exception {
        URL url = new URL(wsfeUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", soapAction);
        
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
        log.info("📩 Respuesta WSFE recibida");
        
        // Buscar CAE en la respuesta
        java.util.regex.Pattern caePattern = java.util.regex.Pattern.compile("<CAE>([^<]+)</CAE>");
        java.util.regex.Matcher caeMatcher = caePattern.matcher(soapResponse);
        
        if (caeMatcher.find()) {
            String cae = caeMatcher.group(1);
            log.info("✅ CAE encontrado: {}", cae);
            return new CAEResponse(cae, java.time.LocalDate.now().plusDays(10));
        } else {
            log.error("❌ Respuesta WSFE completa:\n{}", soapResponse);
            return new CAEResponse("No se pudo obtener CAE - revisar respuesta en logs");
        }
    }
    
    private long parseUltimoComprobanteResponse(String soapResponse) {
        log.info("📩 Respuesta último comprobante recibida");
        
        // Buscar CbteNro en la respuesta
        java.util.regex.Pattern nroPattern = java.util.regex.Pattern.compile("<CbteNro>([^<]+)</CbteNro>");
        java.util.regex.Matcher nroMatcher = nroPattern.matcher(soapResponse);
        
        if (nroMatcher.find()) {
            long ultimoNro = Long.parseLong(nroMatcher.group(1));
            log.info("✅ Último comprobante: {}", ultimoNro);
            return ultimoNro;
        } else {
            log.error("❌ Respuesta último comprobante completa:\n{}", soapResponse);
            return 0;
        }
    }
    
    private String buildPuntosVentaRequest(AfipCredentials credentials) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();
        
        // SOAP Envelope
        org.w3c.dom.Element envelope = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
        envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wsfe", "http://ar.gov.afip.dif.FEV1/");
        doc.appendChild(envelope);
        
        // Header
        org.w3c.dom.Element header = doc.createElement("soapenv:Header");
        envelope.appendChild(header);
        
        // Body
        org.w3c.dom.Element body = doc.createElement("soapenv:Body");
        envelope.appendChild(body);
        
        // FEParamGetPtosVenta
        org.w3c.dom.Element fePtosVenta = doc.createElement("wsfe:FEParamGetPtosVenta");
        body.appendChild(fePtosVenta);
        
        // Auth
        org.w3c.dom.Element auth = doc.createElement("wsfe:Auth");
        fePtosVenta.appendChild(auth);
        
        org.w3c.dom.Element token = doc.createElement("wsfe:Token");
        token.setTextContent(credentials.getToken());
        auth.appendChild(token);
        
        org.w3c.dom.Element sign = doc.createElement("wsfe:Sign");
        sign.setTextContent(credentials.getSign());
        auth.appendChild(sign);
        
        org.w3c.dom.Element cuit = doc.createElement("wsfe:Cuit");
        cuit.setTextContent("27362932039"); // Tu CUIT
        auth.appendChild(cuit);
        
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
    
    private java.util.List<Integer> parsePuntosVentaResponse(String soapResponse) {
        // Implementar parsing de puntos de venta
        log.info("📩 Respuesta puntos de venta recibida");
        return java.util.Arrays.asList(1, 2, 3); // Placeholder
    }
    
    private String buildConsultaCAERequest(String cae, AfipCredentials credentials) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();
        
        // SOAP Envelope
        org.w3c.dom.Element envelope = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
        envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wsfe", "http://ar.gov.afip.dif.FEV1/");
        doc.appendChild(envelope);
        
        // Header
        org.w3c.dom.Element header = doc.createElement("soapenv:Header");
        envelope.appendChild(header);
        
        // Body
        org.w3c.dom.Element body = doc.createElement("soapenv:Body");
        envelope.appendChild(body);
        
        // FECompConsultar
        org.w3c.dom.Element feCompConsultar = doc.createElement("wsfe:FECompConsultar");
        body.appendChild(feCompConsultar);
        
        // Auth
        org.w3c.dom.Element auth = doc.createElement("wsfe:Auth");
        feCompConsultar.appendChild(auth);
        
        org.w3c.dom.Element token = doc.createElement("wsfe:Token");
        token.setTextContent(credentials.getToken());
        auth.appendChild(token);
        
        org.w3c.dom.Element sign = doc.createElement("wsfe:Sign");
        sign.setTextContent(credentials.getSign());
        auth.appendChild(sign);
        
        org.w3c.dom.Element cuit = doc.createElement("wsfe:Cuit");
        cuit.setTextContent("27362932039");
        auth.appendChild(cuit);
        
        // FeCompConsReq
        org.w3c.dom.Element feCompConsReq = doc.createElement("wsfe:FeCompConsReq");
        feCompConsultar.appendChild(feCompConsReq);
        
        org.w3c.dom.Element caeElement = doc.createElement("wsfe:CAE");
        caeElement.setTextContent(cae);
        feCompConsReq.appendChild(caeElement);
        
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
    
    private String parseConsultaCAEResponse(String soapResponse) {
        log.info("📩 Respuesta consulta CAE recibida");
        log.info("📄 Respuesta completa:\n{}", soapResponse);
        return soapResponse;
    }
}