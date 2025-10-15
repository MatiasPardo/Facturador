package com.afip.ui.console;

import com.afip.config.AfipConfig;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleInput {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static int leerOpcion() {
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        return opcion;
    }
    
    public static String leerTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    public static int leerPuntoVenta() {
        String input = leerTexto("Punto de Venta [" + AfipConfig.PUNTO_VENTA_DEFAULT + "]: ");
        return input.isEmpty() ? AfipConfig.PUNTO_VENTA_DEFAULT : Integer.parseInt(input);
    }
    
    public static int leerTipoComprobante() {
        String input = leerTexto("Tipo comprobante (1=A, 6=B, 11=C) [11]: ");
        return input.isEmpty() ? 11 : Integer.parseInt(input);
    }
    
    public static BigDecimal leerImporte() {
        String input = leerTexto("Importe total [$100.00]: ");
        return input.isEmpty() ? new BigDecimal("100.00") : new BigDecimal(input);
    }
    
    public static long leerCuitCliente() {
        String input = leerTexto("CUIT Cliente (0=Consumidor Final) [0]: ");
        return input.isEmpty() ? 0L : Long.parseLong(input);
    }
    
    public static String leerCAE() {
        return leerTexto("Ingrese CAE a consultar: ");
    }
    
    public static int leerCantidadItems() {
        String input = leerTexto("Cantidad de items [3]: ");
        return input.isEmpty() ? 3 : Integer.parseInt(input);
    }
    
    public static String leerNuevoCuit() {
        return leerTexto("Nuevo CUIT Emisor [" + AfipConfig.CUIT_EMISOR + "]: ");
    }
    
    public static String leerNuevoPuntoVenta() {
        return leerTexto("Nuevo Punto de Venta [" + AfipConfig.PUNTO_VENTA_DEFAULT + "]: ");
    }
}