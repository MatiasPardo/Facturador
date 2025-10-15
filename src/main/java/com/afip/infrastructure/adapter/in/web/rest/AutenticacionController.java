package com.afip.infrastructure.adapter.in.web.rest;

import com.afip.domain.port.in.AutenticarWSFEUseCase;
import com.afip.domain.port.in.AutenticarWSMTXCAUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {
    
    private final AutenticarWSFEUseCase autenticarWSFE;
    private final AutenticarWSMTXCAUseCase autenticarWSMTXCA;
    
    public AutenticacionController(AutenticarWSFEUseCase autenticarWSFE, 
                                  AutenticarWSMTXCAUseCase autenticarWSMTXCA) {
        this.autenticarWSFE = autenticarWSFE;
        this.autenticarWSMTXCA = autenticarWSMTXCA;
    }
    
    @PostMapping("/wsfe")
    public void autenticarWSFE() {
        autenticarWSFE.ejecutar();
    }
    
    @PostMapping("/wsmtxca")
    public void autenticarWSMTXCA() {
        autenticarWSMTXCA.ejecutar();
    }
    
    @GetMapping("/status")
    public String verificarEstado() {
        return "Servicio de autenticación activo";
    }
}