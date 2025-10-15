# 🏗️ Arquitectura Hexagonal - AFIP Electronic Billing

## 📐 Estructura de la Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                        UI LAYER                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Console UI    │  │   Menu System   │                  │
│  │                 │  │                 │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                         │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ FacturacionSvc  │  │ AutenticacionSvc│                  │
│  │   (Use Cases)   │  │   (Use Cases)   │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Entities      │  │     Ports       │                  │
│  │ • FacturaElec   │  │ • Input Ports   │                  │
│  │ • Cliente       │  │ • Output Ports  │                  │
│  │ • CAE           │  │                 │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                        │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Repositories  │  │    Adapters     │                  │
│  │ • AfipRepo      │  │ • AfipAdapter   │                  │
│  │ • MonotributoR  │  │ • MonotributoA  │                  │
│  │ • AutenticaciónR│  │ • WSAA/WSFE     │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

## 🎯 Capas y Responsabilidades

### 🎨 **UI Layer** (`com.afip.ui`)
- **Responsabilidad**: Interacción con el usuario
- **Componentes**:
  - `FacturacionElectronicaApp` - Controlador principal
  - `console/ConsoleInput` - Entrada de datos
  - `menu/*` - Menús especializados

### 🔄 **Application Layer** (`com.afip.application`)
- **Responsabilidad**: Casos de uso y orquestación
- **Componentes**:
  - `FacturacionService` - Casos de uso de facturación
  - `AutenticacionService` - Casos de uso de autenticación

### 🏛️ **Domain Layer** (`com.afip.domain`)
- **Responsabilidad**: Lógica de negocio pura
- **Componentes**:
  - **Entities**: `FacturaElectronica`, `Cliente`, `CAE`
  - **Input Ports**: `FacturacionUseCase`, `AutenticacionUseCase`
  - **Output Ports**: `AfipRepository`, `MonotributoRepository`

### 🔧 **Infrastructure Layer** (`com.afip.infrastructure`)
- **Responsabilidad**: Implementaciones técnicas
- **Componentes**:
  - `AfipRepositoryImpl` - Implementación WSFE
  - `MonotributoRepositoryImpl` - Implementación WSMTXCA
  - `AutenticacionRepositoryImpl` - Implementación autenticación

## 🔌 Puertos y Adaptadores

### **Puertos de Entrada (Input Ports)**
```java
FacturacionUseCase
├── generarFactura()
├── generarFacturaConsumidorFinal()
├── consultarUltimoComprobante()
└── consultarPuntosVenta()

AutenticacionUseCase
├── autenticarWSFE()
├── autenticarWSMTXCA()
└── verificarCredenciales()
```

### **Puertos de Salida (Output Ports)**
```java
AfipRepository
├── solicitarCAE()
├── obtenerUltimoComprobante()
└── obtenerPuntosVenta()

MonotributoRepository
├── solicitarCAEMonotributo()
└── obtenerUltimoComprobanteMonotributo()
```

## ✨ Beneficios de la Arquitectura Hexagonal

### 🎯 **Separación de Responsabilidades**
- **Dominio**: Lógica de negocio pura, sin dependencias externas
- **Aplicación**: Orquestación de casos de uso
- **Infraestructura**: Detalles técnicos (SOAP, HTTP, etc.)
- **UI**: Solo presentación e interacción

### 🔄 **Inversión de Dependencias**
- El dominio no depende de la infraestructura
- Los adaptadores implementan los puertos del dominio
- Fácil intercambio de implementaciones

### 🧪 **Testabilidad**
- Casos de uso testeable sin infraestructura
- Mocks fáciles para puertos de salida
- Tests unitarios rápidos del dominio

### 📈 **Mantenibilidad**
- Cambios en AFIP solo afectan adaptadores
- Nueva UI sin cambiar lógica de negocio
- Evolución independiente de capas

## 🚀 Flujo de Ejecución

```
1. UI recibe input del usuario
2. UI llama al caso de uso (Application)
3. Caso de uso ejecuta lógica de dominio
4. Caso de uso llama a puerto de salida
5. Adaptador implementa llamada a AFIP
6. Respuesta fluye de vuelta por las capas
```

## 🔧 Configuración y Uso

### **Inicialización**
```java
// Los adaptadores se crean en AfipService
AfipAdapter wsfeAdapter = new AfipAdapter(...);
MonotributoAdapter monotributoAdapter = new MonotributoAdapter(...);

// Los repositorios implementan los puertos
AfipRepository afipRepo = new AfipRepositoryImpl(wsfeAdapter);
MonotributoRepository monoRepo = new MonotributoRepositoryImpl(monotributoAdapter);

// Los casos de uso reciben los repositorios
FacturacionUseCase facturacion = new FacturacionService(afipRepo, monoRepo);
```

### **Uso desde UI**
```java
// La UI usa los casos de uso, no los adaptadores directamente
CAE cae = facturacionUseCase.generarFacturaConsumidorFinal(
    TipoComprobante.FACTURA_C, 
    puntoVenta, 
    importe
);
```

Esta arquitectura garantiza un código limpio, testeable y mantenible, siguiendo los principios SOLID y las mejores prácticas de arquitectura de software.