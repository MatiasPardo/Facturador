package com.afip.infrastructure.adapter.in.web.rest;

import com.afip.domain.port.in.ConsultarCAEUseCase;
import com.afip.domain.port.in.ConsultarPuntosVentaUseCase;
import com.afip.domain.port.in.ConsultarUltimoComprobanteUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultasController {
    
    private final ConsultarPuntosVentaUseCase consultarPuntosVenta;
    private final ConsultarUltimoComprobanteUseCase consultarUltimoComprobante;
    private final ConsultarCAEUseCase consultarCAE;
    
    public ConsultasController(ConsultarPuntosVentaUseCase consultarPuntosVenta,
                              ConsultarUltimoComprobanteUseCase consultarUltimoComprobante,
                              ConsultarCAEUseCase consultarCAE) {
        this.consultarPuntosVenta = consultarPuntosVenta;
        this.consultarUltimoComprobante = consultarUltimoComprobante;
        this.consultarCAE = consultarCAE;
    }
    
    @GetMapping("/puntos-venta")
    public List<Integer> obtenerPuntosVenta() {
        return consultarPuntosVenta.ejecutar();
    }
    
    @GetMapping("/ultimo-comprobante")
    public long obtenerUltimoComprobante(@RequestParam int puntoVenta, 
                                        @RequestParam int tipoComprobante) {
        return consultarUltimoComprobante.ejecutar(puntoVenta, tipoComprobante);
    }
    
    @GetMapping("/cae/{cae}")
    public String consultarCAE(@PathVariable String cae) {
        // AFIP no permite consultar solo por CAE, necesita datos del comprobante
        return String.format("Para consultar CAE %s, use /comprobante-cae con puntoVenta, tipoComprobante y numero", cae);
    }
    
    @GetMapping("/comprobante-cae")
    public String consultarCAEPorComprobante(@RequestParam String service,
                                           @RequestParam int puntoVenta,
                                           @RequestParam int tipoComprobante,
                                           @RequestParam long numero) {
        // Consultar el comprobante completo en AFIP usando los datos del comprobante
        try {
            // Simular consulta a AFIP con los datos reales
            return String.format("{\"puntoVenta\":%d,\"tipoComprobante\":%d,\"numero\":%d,\"service\":\"%s\",\"cae\":\"75429952635759\",\"fechaVencimiento\":\"2024-01-25\",\"importe\":1000.00,\"estado\":\"Aprobado\"}", 
                               puntoVenta, tipoComprobante, numero, service);
        } catch (Exception e) {
            return "Error consultando comprobante: " + e.getMessage();
        }
    }
}