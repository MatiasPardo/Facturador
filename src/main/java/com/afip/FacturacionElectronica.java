package com.afip;

import com.afip.adapter.AfipAdapter;
import com.afip.adapter.MonotributoAdapter;
import com.afip.auth.AfipAuthenticationException;
import com.afip.auth.CredentialsManager;
import com.afip.billing.model.CAEResponse;
import com.afip.billing.model.Comprobante;
import com.afip.billing.model.TipoComprobante;
import com.afip.config.AfipConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FacturacionElectronica {
    
    private static final Logger log = LoggerFactory.getLogger(FacturacionElectronica.class);
    private static AfipAdapter adapter;
    private static MonotributoAdapter monotributoAdapter;
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        try {
            inicializar();
            mostrarMenu();
        } catch (Exception e) {
            log.error("❌ Error general: {}", e.getMessage(), e);
        }
    }
    
    private static void inicializar() throws AfipAuthenticationException {
        log.info("🚀 Inicializando Facturación Electrónica AFIP");
        log.info("📋 CUIT Emisor: {}", AfipConfig.CUIT_EMISOR);
        log.info("🏪 Punto de Venta: {}", AfipConfig.PUNTO_VENTA_DEFAULT);
        log.info("🏢 Razón Social: {}", AfipConfig.RAZON_SOCIAL_EMISOR);
        
        adapter = new AfipAdapter(
            AfipConfig.CERT_PATH, 
            AfipConfig.CERT_PASSWORD, 
            AfipConfig.CERT_ALIAS,
            AfipConfig.WSAA_URL, 
            AfipConfig.WSFE_URL
        );
        
        monotributoAdapter = new MonotributoAdapter(
            AfipConfig.CERT_PATH, 
            AfipConfig.CERT_PASSWORD, 
            AfipConfig.CERT_ALIAS,
            AfipConfig.WSAA_URL, 
            AfipConfig.WSMTXCA_URL
        );
        
        log.info("🔐 Autenticando con AFIP WSFE...");
        adapter.authenticate();
        log.info("✅ Sistema listo para facturar");
    }
    
    private static void mostrarMenu() {
        while (true) {
            System.out.println("\n=== FACTURACIÓN ELECTRÓNICA AFIP ===");
            System.out.println("1. 🔐 Tests de Autenticación");
            System.out.println("2. 📊 Tests de Consulta");
            System.out.println("3. 📄 Tests de Facturación");
            System.out.println("4. 🧪 Tests de Monotributo");
            System.out.println("5. 📋 Consultar por CAE");
            System.out.println("6. 🏪 Ver puntos de venta");
            System.out.println("7. 📊 Resumen todos los PV");
            System.out.println("8. 🧩 Limpiar credenciales");
            System.out.println("9. ⚙️ Configuración");
            System.out.println("0. ❌ Salir");
            System.out.print("Seleccionar opción: ");
            
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer
            
            try {
                switch (opcion) {
                    case 1: menuAutenticacion(); break;
                    case 2: menuConsultas(); break;
                    case 3: menuFacturacion(); break;
                    case 4: menuMonotributo(); break;
                    case 5: consultarPorCAE(); break;
                    case 6: consultarPuntosVenta(); break;
                    case 7: resumenTodosPV(); break;
                    case 8: limpiarCredenciales(); break;
                    case 9: menuConfiguracion(); break;
                    case 0: 
                        log.info("👋 Finalizando sistema");
                        return;
                    default:
                        System.out.println("❌ Opción inválida");
                }
            } catch (Exception e) {
                log.error("❌ Error: {}", e.getMessage());
            }
        }
    }
    
    // === MÉTODOS DE CONSULTA ===
    
    public static void consultarUltimoComprobante() throws AfipAuthenticationException {
        System.out.print("Ingrese Punto de Venta [" + AfipConfig.PUNTO_VENTA_DEFAULT + "]: ");
        String pvInput = scanner.nextLine();
        int pv = pvInput.isEmpty() ? AfipConfig.PUNTO_VENTA_DEFAULT : Integer.parseInt(pvInput);
        
        System.out.print("Tipo comprobante (1=A, 6=B, 11=C) [11]: ");
        String tipoInput = scanner.nextLine();
        int tipo = tipoInput.isEmpty() ? 11 : Integer.parseInt(tipoInput);
        
        log.info("🔍 Consultando último comprobante PV {} tipo {}", pv, tipo);
        long ultimo = adapter.obtenerUltimoComprobante(pv, tipo);
        log.info("📊 Último comprobante: {}", ultimo);
    }
    
    public static void consultarPuntosVenta() throws AfipAuthenticationException {
        log.info("🏪 Consultando puntos de venta habilitados...");
        List<Integer> puntosVenta = adapter.consultarPuntosVenta();
        
        if (puntosVenta.isEmpty()) {
            log.warn("⚠️ No hay puntos de venta electrónicos habilitados");
        } else {
            log.info("✅ Puntos de venta habilitados:");
            for (Integer pv : puntosVenta) {
                log.info("   🏪 PV: {}", pv);
            }
        }
    }
    
    public static void consultarPorCAE() throws AfipAuthenticationException {
        System.out.print("Ingrese CAE a consultar: ");
        String cae = scanner.nextLine();
        
        log.info("🔍 Consultando CAE: {}", cae);
        String resultado = adapter.consultarComprobantePorCAE(cae);
        log.info("📋 Resultado: {}", resultado);
    }
    
    public static void resumenTodosPV() throws AfipAuthenticationException {
        log.info("📊 Consultando resumen de todos los PV...");
        Map<String, Long> resumen = adapter.consultarUltimosComprobantesPorPV(TipoComprobante.FACTURA_C.getCodigo());
        
        log.info("📋 Resumen Facturas C:");
        resumen.forEach((pv, ultimo) -> 
            log.info("   {} → Último: {}", pv, ultimo)
        );
    }
    
    // === MÉTODOS DE FACTURACIÓN ===
    
    public static CAEResponse generarFacturaC() throws AfipAuthenticationException {
        return generarFacturaC(null, null, null);
    }
    
    public static CAEResponse generarFacturaC(Integer puntoVenta, BigDecimal importe, Long cuitCliente) throws AfipAuthenticationException {
        // Parámetros por defecto
        int pv = puntoVenta != null ? puntoVenta : AfipConfig.PUNTO_VENTA_DEFAULT;
        BigDecimal importeTotal = importe != null ? importe : solicitarImporte();
        long cuit = cuitCliente != null ? cuitCliente : AfipConfig.CUIT_CONSUMIDOR_FINAL;
        
        // Obtener próximo número
        long ultimoComprobante = adapter.obtenerUltimoComprobante(pv, TipoComprobante.FACTURA_C.getCodigo());
        long proximoNumero = ultimoComprobante + 1;
        
        // Crear comprobante
        Comprobante factura = crearComprobanteBase(pv, proximoNumero, importeTotal, cuit);
        
        log.info("📄 Generando Factura C PV {} N° {}", pv, proximoNumero);
        log.info("   💰 Importe: ${}", importeTotal);
        log.info("   👤 Cliente: {}", cuit == 0 ? "Consumidor Final" : cuit);
        
        CAEResponse cae = adapter.solicitarCAE(factura);
        
        if (cae.isSuccess()) {
            log.info("✅ ¡FACTURA GENERADA EXITOSAMENTE!");
            log.info("   🔐 CAE: {}", cae.getCae());
            log.info("   📅 Vence: {}", cae.getFechaVencimiento());
            log.info("   🏪 Buscar en AFIP con PV: {:03d}", pv);
        } else {
            log.error("❌ Error: {}", cae.getErrorMessage());
        }
        
        return cae;
    }
    
    public static CAEResponse modoPrueba() throws AfipAuthenticationException {
        log.info("🧪 Modo de prueba - Factura con importe bajo");
        return generarFacturaC(null, new BigDecimal("10.00"), null);
    }
    
    // === MÉTODOS AUXILIARES ===
    
    private static Comprobante crearComprobanteBase(int puntoVenta, long numeroComprobante, BigDecimal importeTotal, long cuitCliente) {
        Comprobante factura = new Comprobante();
        
        // Datos básicos
        factura.setTipoComprobante(TipoComprobante.FACTURA_C);
        factura.setPuntoVenta(puntoVenta);
        factura.setNumeroComprobante(numeroComprobante);
        factura.setFechaComprobante(LocalDate.now());
        
        // Cliente
        factura.setCuitCliente(cuitCliente);
        factura.setTipoDocumento(cuitCliente == 0 ? AfipConfig.DOC_TIPO_CONSUMIDOR_FINAL : AfipConfig.DOC_TIPO_CUIT);
        
        // Importes (Monotributo - sin discriminar IVA)
        factura.setImporteTotal(importeTotal);
        factura.setImporteNeto(importeTotal);
        factura.setImporteIva(BigDecimal.ZERO);
        
        factura.setConcepto("Productos");
        
        return factura;
    }
    
    private static BigDecimal solicitarImporte() {
        System.out.print("Ingrese importe total [$100.00]: ");
        String importeInput = scanner.nextLine();
        return importeInput.isEmpty() ? new BigDecimal("100.00") : new BigDecimal(importeInput);
    }
    
    // === MÉTODOS PÚBLICOS PARA USO EXTERNO ===
    
    public static AfipAdapter getAdapter() {
        return adapter;
    }
    
    public static CAEResponse facturarConsumidorFinal(BigDecimal importe) throws AfipAuthenticationException {
        return generarFacturaC(null, importe, AfipConfig.CUIT_CONSUMIDOR_FINAL);
    }
    
    public static CAEResponse facturarCliente(BigDecimal importe, long cuitCliente) throws AfipAuthenticationException {
        return generarFacturaC(null, importe, cuitCliente);
    }
    
    public static long consultarUltimoNumero() throws AfipAuthenticationException {
        return adapter.obtenerUltimoComprobante(AfipConfig.PUNTO_VENTA_DEFAULT, TipoComprobante.FACTURA_C.getCodigo());
    }
    
    // === MENÚS DE CATEGORÍAS ===
    
    private static void menuAutenticacion() {
        System.out.println("\n=== TESTS DE AUTENTICACIÓN ===");
        System.out.println("1. 🔐 Autenticar WSFE");
        System.out.println("2. 🔐 Autenticar WSMTXCA (Monotributo)");
        System.out.println("3. ✅ Verificar credenciales");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = scanner.nextInt();
        scanner.nextLine();
        
        try {
            switch (opcion) {
                case 1: testAutenticarWSFE(); break;
                case 2: testAutenticarWSMTXCA(); break;
                case 3: testVerificarCredenciales(); break;
            }
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
    
    private static void menuConsultas() {
        System.out.println("\n=== TESTS DE CONSULTA ===");
        System.out.println("1. 📊 Último comprobante por tipo");
        System.out.println("2. 🏪 Puntos de venta habilitados");
        System.out.println("3. 📋 Consultar por CAE");
        System.out.println("4. 📊 Resumen todos los PV");
        System.out.println("5. 🔍 Verificar servicios AFIP");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = scanner.nextInt();
        scanner.nextLine();
        
        try {
            switch (opcion) {
                case 1: consultarUltimoComprobante(); break;
                case 2: consultarPuntosVenta(); break;
                case 3: consultarPorCAE(); break;
                case 4: resumenTodosPV(); break;
                case 5: verificarServicios(); break;
            }
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
    
    private static void menuFacturacion() {
        System.out.println("\n=== TESTS DE FACTURACIÓN ===");
        System.out.println("1. 📄 Factura A (Responsable Inscripto)");
        System.out.println("2. 📄 Factura B (Responsable Inscripto)");
        System.out.println("3. 📄 Factura C (Monotributo)");
        System.out.println("4. 👤 Factura Consumidor Final");
        System.out.println("5. 💰 Factura con items detallados");
        System.out.println("6. 🧪 Factura de prueba (importe bajo)");
        System.out.println("7. ⚙️ Factura personalizada");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = scanner.nextInt();
        scanner.nextLine();
        
        try {
            switch (opcion) {
                case 1: testFacturaA(); break;
                case 2: testFacturaB(); break;
                case 3: generarFacturaC(); break;
                case 4: testFacturaConsumidorFinal(); break;
                case 5: testFacturaConItems(); break;
                case 6: modoPrueba(); break;
                case 7: testFacturaPersonalizada(); break;
            }
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
    
    private static void menuMonotributo() {
        System.out.println("\n=== TESTS DE MONOTRIBUTO ===");
        System.out.println("1. 📊 Último comprobante monotributo");
        System.out.println("2. 📄 Generar CAE monotributo");
        System.out.println("3. 🧪 Prueba monotributo (importe bajo)");
        System.out.println("4. 🔐 Autenticar WSMTXCA");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = scanner.nextInt();
        scanner.nextLine();
        
        try {
            switch (opcion) {
                case 1: testUltimoComprobanteMonotributo(); break;
                case 2: testCAEMonotributo(); break;
                case 3: testPruebaMonotributo(); break;
                case 4: testAutenticarWSMTXCA(); break;
            }
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
        }
    }
    
    private static void menuConfiguracion() {
        System.out.println("\n=== CONFIGURACIÓN ===");
        System.out.println("1. 📋 Ver configuración actual");
        System.out.println("2. ✏️ Cambiar CUIT emisor");
        System.out.println("3. 🏪 Cambiar punto de venta");
        System.out.println("0. ⬅️ Volver");
        System.out.print("Opción: ");
        
        int opcion = scanner.nextInt();
        scanner.nextLine();
        
        switch (opcion) {
            case 1: mostrarConfiguracion(); break;
            case 2: cambiarCuitEmisor(); break;
            case 3: cambiarPuntoVenta(); break;
        }
    }
    
    // === MÉTODOS DE TESTS ===
    
    public static void testAutenticarWSFE() throws AfipAuthenticationException {
        log.info("🔐 Test: Autenticación WSFE");
        adapter.authenticate();
        log.info("✅ Autenticación WSFE exitosa");
    }
    
    public static void testAutenticarWSMTXCA() throws AfipAuthenticationException {
        log.info("🔐 Test: Autenticación WSMTXCA");
        monotributoAdapter.authenticate();
        log.info("✅ Autenticación WSMTXCA exitosa");
    }
    
    public static void testVerificarCredenciales() {
        log.info("✅ Test: Verificar Credenciales");
        log.info("WSFE válido: {}", CredentialsManager.hasValidCredentials("wsfe"));
        log.info("WSMTXCA válido: {}", CredentialsManager.hasValidCredentials("wsmtxca"));
    }
    
    public static CAEResponse testFacturaA() throws AfipAuthenticationException {
        log.info("📄 Test: Factura A");
        return generarFacturaTipo(TipoComprobante.FACTURA_A, new BigDecimal("1210.00"), Long.parseLong(AfipConfig.CUIT_CLIENTE_PRUEBA));
    }
    
    public static CAEResponse testFacturaB() throws AfipAuthenticationException {
        log.info("📄 Test: Factura B");
        return generarFacturaTipo(TipoComprobante.FACTURA_B, new BigDecimal("1210.00"), Long.parseLong(AfipConfig.CUIT_CLIENTE_PRUEBA));
    }
    
    public static CAEResponse testFacturaConsumidorFinal() throws AfipAuthenticationException {
        log.info("👤 Test: Factura Consumidor Final");
        return generarFacturaC(null, new BigDecimal("250.00"), AfipConfig.CUIT_CONSUMIDOR_FINAL);
    }
    
    public static CAEResponse testFacturaConItems() throws AfipAuthenticationException {
        log.info("💰 Test: Factura con Items");
        System.out.print("Cantidad de items [3]: ");
        String cantInput = scanner.nextLine();
        int cantItems = cantInput.isEmpty() ? 3 : Integer.parseInt(cantInput);
        
        BigDecimal importeTotal = new BigDecimal(cantItems * 100);
        return generarFacturaC(null, importeTotal, AfipConfig.CUIT_CONSUMIDOR_FINAL);
    }
    
    public static CAEResponse testFacturaPersonalizada() throws AfipAuthenticationException {
        log.info("⚙️ Test: Factura Personalizada");
        
        System.out.print("Punto de Venta [" + AfipConfig.PUNTO_VENTA_DEFAULT + "]: ");
        String pvInput = scanner.nextLine();
        int pv = pvInput.isEmpty() ? AfipConfig.PUNTO_VENTA_DEFAULT : Integer.parseInt(pvInput);
        
        System.out.print("Tipo (1=A, 6=B, 11=C) [11]: ");
        String tipoInput = scanner.nextLine();
        int tipoCode = tipoInput.isEmpty() ? 11 : Integer.parseInt(tipoInput);
        TipoComprobante tipo = TipoComprobante.fromCodigo(tipoCode);
        
        System.out.print("Importe total [100.00]: ");
        String importeInput = scanner.nextLine();
        BigDecimal importe = importeInput.isEmpty() ? new BigDecimal("100.00") : new BigDecimal(importeInput);
        
        System.out.print("CUIT Cliente (0=Consumidor Final) [0]: ");
        String cuitInput = scanner.nextLine();
        long cuit = cuitInput.isEmpty() ? 0L : Long.parseLong(cuitInput);
        
        return generarFacturaTipo(tipo, importe, cuit, pv);
    }
    
    public static void testUltimoComprobanteMonotributo() throws AfipAuthenticationException {
        log.info("📊 Test: Último Comprobante Monotributo");
        long ultimo = monotributoAdapter.obtenerUltimoComprobanteMonotributo(AfipConfig.PUNTO_VENTA_DEFAULT);
        log.info("Último comprobante Monotributo PV {}: {}", AfipConfig.PUNTO_VENTA_DEFAULT, ultimo);
    }
    
    public static CAEResponse testCAEMonotributo() throws AfipAuthenticationException {
        log.info("📄 Test: CAE Monotributo");
        return generarComprobanteMonotributo(new BigDecimal("300.00"));
    }
    
    public static CAEResponse testPruebaMonotributo() throws AfipAuthenticationException {
        log.info("🧪 Test: Prueba Monotributo");
        return generarComprobanteMonotributo(new BigDecimal(AfipConfig.IMPORTE_PRUEBA));
    }
    
    public static void verificarServicios() throws AfipAuthenticationException {
        log.info("🔍 Test: Verificar Servicios AFIP");
        
        try {
            List<Integer> puntosVenta = adapter.consultarPuntosVenta();
            log.info("✅ WSFE operativo - PVs: {}", puntosVenta.size());
        } catch (Exception e) {
            log.error("❌ WSFE no disponible: {}", e.getMessage());
        }
        
        try {
            long ultimo = monotributoAdapter.obtenerUltimoComprobanteMonotributo(AfipConfig.PUNTO_VENTA_DEFAULT);
            log.info("✅ WSMTXCA operativo - Último: {}", ultimo);
        } catch (Exception e) {
            log.error("❌ WSMTXCA no disponible: {}", e.getMessage());
        }
    }
    
    // === MÉTODOS AUXILIARES ADICIONALES ===
    
    private static CAEResponse generarFacturaTipo(TipoComprobante tipo, BigDecimal importe, long cuitCliente) throws AfipAuthenticationException {
        return generarFacturaTipo(tipo, importe, cuitCliente, AfipConfig.PUNTO_VENTA_DEFAULT);
    }
    
    private static CAEResponse generarFacturaTipo(TipoComprobante tipo, BigDecimal importe, long cuitCliente, int puntoVenta) throws AfipAuthenticationException {
        long ultimo = adapter.obtenerUltimoComprobante(puntoVenta, tipo.getCodigo());
        long proximoNumero = ultimo + 1;
        
        Comprobante factura = new Comprobante();
        factura.setTipoComprobante(tipo);
        factura.setPuntoVenta(puntoVenta);
        factura.setNumeroComprobante(proximoNumero);
        factura.setFechaComprobante(LocalDate.now());
        factura.setCuitCliente(cuitCliente);
        factura.setTipoDocumento(cuitCliente == 0 ? AfipConfig.DOC_TIPO_CONSUMIDOR_FINAL : AfipConfig.DOC_TIPO_CUIT);
        
        if (tipo == TipoComprobante.FACTURA_C) {
            factura.setImporteTotal(importe);
            factura.setImporteNeto(importe);
            factura.setImporteIva(BigDecimal.ZERO);
        } else {
            BigDecimal neto = importe.divide(new BigDecimal("1.21"), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal iva = importe.subtract(neto);
            factura.setImporteNeto(neto);
            factura.setImporteIva(iva);
            factura.setImporteTotal(importe);
        }
        
        factura.setConcepto("Productos");
        
        log.info("Generando {} N° {} - Importe: ${}", tipo.name(), proximoNumero, importe);
        CAEResponse cae = adapter.solicitarCAE(factura);
        
        if (cae.isSuccess()) {
            log.info("✅ CAE: {} - Vence: {}", cae.getCae(), cae.getFechaVencimiento());
        } else {
            log.error("❌ Error: {}", cae.getErrorMessage());
        }
        
        return cae;
    }
    
    private static CAEResponse generarComprobanteMonotributo(BigDecimal importe) throws AfipAuthenticationException {
        long ultimo = monotributoAdapter.obtenerUltimoComprobanteMonotributo(AfipConfig.PUNTO_VENTA_DEFAULT);
        long proximoNumero = ultimo + 1;
        
        Comprobante comprobante = new Comprobante();
        comprobante.setTipoComprobante(TipoComprobante.FACTURA_C);
        comprobante.setPuntoVenta(AfipConfig.PUNTO_VENTA_DEFAULT);
        comprobante.setNumeroComprobante(proximoNumero);
        comprobante.setFechaComprobante(LocalDate.now());
        comprobante.setCuitCliente(AfipConfig.CUIT_CONSUMIDOR_FINAL);
        comprobante.setTipoDocumento(AfipConfig.DOC_TIPO_CONSUMIDOR_FINAL);
        comprobante.setImporteTotal(importe);
        
        log.info("Generando Comprobante Monotributo N° {} - Importe: ${}", proximoNumero, importe);
        CAEResponse cae = monotributoAdapter.solicitarCAEMonotributo(comprobante);
        
        if (cae.isSuccess()) {
            log.info("✅ CAE Monotributo: {} - Vence: {}", cae.getCae(), cae.getFechaVencimiento());
        } else {
            log.error("❌ Error Monotributo: {}", cae.getErrorMessage());
        }
        
        return cae;
    }
    
    public static void limpiarCredenciales() {
        log.info("🧩 Limpiando credenciales...");
        CredentialsManager.clearCredentials();
        log.info("✅ Credenciales eliminadas");
    }
    
    private static void mostrarConfiguracion() {
        log.info("🔧 === CONFIGURACIÓN ACTUAL ===");
        log.info("📋 CUIT Emisor: {}", AfipConfig.CUIT_EMISOR);
        log.info("🏪 Punto de Venta: {}", AfipConfig.PUNTO_VENTA_DEFAULT);
        log.info("🏢 Razón Social: {}", AfipConfig.RAZON_SOCIAL_EMISOR);
        log.info("🔐 Certificado: {}", AfipConfig.CERT_PATH);
        log.info("💰 Importe Prueba: ${}", AfipConfig.IMPORTE_PRUEBA);
    }
    
    private static void cambiarCuitEmisor() {
        System.out.print("Nuevo CUIT Emisor [" + AfipConfig.CUIT_EMISOR + "]: ");
        String nuevoCuit = scanner.nextLine();
        if (!nuevoCuit.isEmpty()) {
            AfipConfig.setCuitEmisor(nuevoCuit);
            log.info("✅ CUIT Emisor actualizado a: {}", nuevoCuit);
        }
    }
    
    private static void cambiarPuntoVenta() {
        System.out.print("Nuevo Punto de Venta [" + AfipConfig.PUNTO_VENTA_DEFAULT + "]: ");
        String nuevoPV = scanner.nextLine();
        if (!nuevoPV.isEmpty()) {
            AfipConfig.setPuntoVentaDefault(Integer.parseInt(nuevoPV));
            log.info("✅ Punto de Venta actualizado a: {}", nuevoPV);
        }
    }
    
    // === MÉTODOS PÚBLICOS ADICIONALES ===
    
    public static MonotributoAdapter getMonotributoAdapter() {
        return monotributoAdapter;
    }
    
    public static CAEResponse facturarRapido(BigDecimal importe) throws AfipAuthenticationException {
        return generarFacturaC(null, importe, AfipConfig.CUIT_CONSUMIDOR_FINAL);
    }
}