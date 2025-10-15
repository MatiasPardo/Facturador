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
        return consultarCAE.ejecutar(cae);
    }
}