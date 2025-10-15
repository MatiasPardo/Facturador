package com.afip.ui.menu;

import com.afip.ui.console.ConsoleInput;

public class MenuPrincipal {
    
    public static void mostrar() {
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
    }
    
    public static int leerOpcion() {
        return ConsoleInput.leerOpcion();
    }
}