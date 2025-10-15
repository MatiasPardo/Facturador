package com.afip.infrastructure.adapter.in.web.soap;

import com.afip.domain.model.CAE;
import com.afip.domain.model.Cliente;
import com.afip.domain.model.FacturaElectronica;
import com.afip.domain.model.TipoComprobante;
import com.afip.domain.port.in.SolicitarCAEUseCase;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebService(serviceName = "AfipSoapService")
@Service
public class AfipSoapService {
    
    private final SolicitarCAEUseCase solicitarCAE;
    
    public AfipSoapService(SolicitarCAEUseCase solicitarCAE) {
        this.solicitarCAE = solicitarCAE;
    }
    
    @WebMethod
    public String solicitarCAE(@WebParam(name = "token") String token,
                              @WebParam(name = "sign") String sign,
                              @WebParam(name = "tipoComprobante") int tipoComprobante,
                              @WebParam(name = "puntoVenta") int puntoVenta,
                              @WebParam(name = "numeroComprobante") long numeroComprobante,
                              @WebParam(name = "fechaComprobante") String fechaComprobante,
                              @WebParam(name = "cuitCliente") long cuitCliente,
                              @WebParam(name = "tipoDocumento") int tipoDocumento,
                              @WebParam(name = "importeNeto") String importeNeto,
                              @WebParam(name = "importeIva") String importeIva,
                              @WebParam(name = "importeTotal") String importeTotal) {
        
        try {
            // Validar token (middleware - pasamanos)
            if (token == null || token.isEmpty()) {
                return crearRespuestaError("Token requerido");
            }
            
            // Crear factura electrónica
            FacturaElectronica factura = crearFacturaDesdeSOAP(
                tipoComprobante, puntoVenta, numeroComprobante, fechaComprobante,
                cuitCliente, tipoDocumento, importeNeto, importeIva, importeTotal
            );
            
            // Determinar servicio basado en tipo de comprobante
            String servicio = determinarServicio(tipoComprobante);
            
            // Solicitar CAE directamente (pasamanos)
            CAE cae = solicitarCAE.ejecutar(servicio, factura);
            
            if (cae.isExitoso()) {
                return crearRespuestaExitosa(cae);
            } else {
                return crearRespuestaError(cae.getMensajeError());
            }
            
        } catch (Exception e) {
            return crearRespuestaError("Error procesando solicitud: " + e.getMessage());
        }
    }
    
    @WebMethod
    public String consultarUltimoComprobante(@WebParam(name = "token") String token,
                                           @WebParam(name = "puntoVenta") int puntoVenta,
                                           @WebParam(name = "tipoComprobante") int tipoComprobante) {
        
        if (token == null || token.isEmpty()) {
            return "ERROR: Token requerido";
        }
        
        // Aquí iría la lógica de consulta usando el caso de uso
        return "Último comprobante: " + puntoVenta + "-" + tipoComprobante;
    }
    
    private FacturaElectronica crearFacturaDesdeSOAP(int tipoComprobante, int puntoVenta, 
                                                    long numeroComprobante, String fechaComprobante,
                                                    long cuitCliente, int tipoDocumento,
                                                    String importeNeto, String importeIva, String importeTotal) {
        
        TipoComprobante tipo = convertirTipoComprobante(tipoComprobante);
        LocalDate fecha = LocalDate.parse(fechaComprobante, DateTimeFormatter.ofPattern("yyyyMMdd"));
        Cliente cliente = crearCliente(cuitCliente, tipoDocumento);
        
        return new FacturaElectronica(
            tipo, puntoVenta, numeroComprobante, fecha, cliente,
            new java.math.BigDecimal(importeNeto),
            new java.math.BigDecimal(importeIva),
            new java.math.BigDecimal(importeTotal),
            "Productos"
        );
    }
    
    private TipoComprobante convertirTipoComprobante(int codigo) {
        switch (codigo) {
            case 1: return TipoComprobante.FACTURA_A;
            case 6: return TipoComprobante.FACTURA_B;
            case 11: return TipoComprobante.FACTURA_C;
            default: throw new IllegalArgumentException("Tipo comprobante no válido: " + codigo);
        }
    }
    
    private Cliente crearCliente(long cuit, int tipoDocumento) {
        if (cuit == 0) {
            return Cliente.consumidorFinal();
        } else {
            return Cliente.conCuit(cuit);
        }
    }
    
    private String crearRespuestaExitosa(CAE cae) {
        return String.format("<?xml version=\"1.0\"?><response><success>true</success><cae>%s</cae><fechaVencimiento>%s</fechaVencimiento></response>",
                cae.getNumero(), cae.getFechaVencimiento());
    }
    
    private String crearRespuestaError(String mensaje) {
        return String.format("<?xml version=\"1.0\"?><response><success>false</success><error>%s</error></response>", mensaje);
    }
    
    private String determinarServicio(int tipoComprobante) {
        switch (tipoComprobante) {
            case 1:  // Factura A - Solo WSFE
            case 6:  // Factura B - Solo WSFE
                return "wsfe";
            case 11: // Factura C - WSFE por limitaciones del certificado
                return "wsfe"; // Cambiar a "wsmtxca" cuando tengas permisos
            default:
                return "wsfe";
        }
    }
}