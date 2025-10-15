# 🌐 AFIP Electronic Billing APIs

## 🏗️ Arquitectura de APIs

### **📱 REST API (Aplicación con Lógica)**
- **Puerto**: 8080
- **Base URL**: `http://localhost:8080/api`
- **Características**: 
  - ✅ Auto-autenticación
  - ✅ Validaciones de negocio
  - ✅ Manejo de errores
  - ✅ Composición de casos de uso

### **🔌 SOAP API (Middleware/Pasamanos)**
- **Puerto**: 8080
- **WSDL**: `http://localhost:8080/soap/afip?wsdl`
- **Características**:
  - ✅ Requiere token manual
  - ✅ Pasamanos directo a AFIP
  - ✅ Formato SOAP estándar
  - ✅ Mínima lógica de negocio

## 📋 REST Endpoints

### **🔐 Autenticación** (`/api/auth`)

```http
POST /api/auth/wsfe
# Autentica servicio WSFE
# Response: 200 OK

POST /api/auth/wsmtxca  
# Autentica servicio WSMTXCA
# Response: 200 OK

GET /api/auth/status
# Verifica estado del servicio
# Response: "Servicio de autenticación activo"
```

### **📄 Facturación** (`/api/facturacion`)

```http
POST /api/facturacion/facturar
Content-Type: application/json

{
  "tipoComprobante": "C",
  "puntoVenta": 1,
  "importe": 100.00,
  "cuitCliente": 0,
  "concepto": "Productos"
}

# Response:
{
  "exitoso": true,
  "cae": "74251234567890",
  "fechaVencimiento": "2024-01-20",
  "error": null
}
```

```http
POST /api/facturacion/consumidor-final?tipo=C&puntoVenta=1&importe=100.00
# Factura rápida consumidor final
# Response: CAEResponse
```

### **📊 Consultas** (`/api/consultas`)

```http
GET /api/consultas/puntos-venta
# Response: [1, 2, 3]

GET /api/consultas/ultimo-comprobante?puntoVenta=1&tipoComprobante=11
# Response: 123

GET /api/consultas/cae/74251234567890
# Response: "Comprobante válido"
```

## 🔌 SOAP Operations

### **SolicitarCAE**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
    <solicitarCAE>
      <token>TOKEN_WSFE</token>
      <sign>SIGNATURE</sign>
      <tipoComprobante>11</tipoComprobante>
      <puntoVenta>1</puntoVenta>
      <numeroComprobante>124</numeroComprobante>
      <fechaComprobante>20240115</fechaComprobante>
      <cuitCliente>0</cuitCliente>
      <tipoDocumento>99</tipoDocumento>
      <importeNeto>100.00</importeNeto>
      <importeIva>0.00</importeIva>
      <importeTotal>100.00</importeTotal>
    </solicitarCAE>
  </soapenv:Body>
</soapenv:Envelope>
```

**Response:**
```xml
<?xml version="1.0"?>
<response>
  <success>true</success>
  <cae>74251234567890</cae>
  <fechaVencimiento>2024-01-20</fechaVencimiento>
</response>
```

### **ConsultarUltimoComprobante**
```xml
<consultarUltimoComprobante>
  <token>TOKEN_WSFE</token>
  <puntoVenta>1</puntoVenta>
  <tipoComprobante>11</tipoComprobante>
</consultarUltimoComprobante>
```

## 🔄 Flujos de Uso

### **🎯 Flujo REST (Aplicación)**
```
1. POST /api/facturacion/facturar
2. Sistema verifica autenticación automáticamente
3. Si no hay token válido → autentica automáticamente
4. Compone casos de uso: ConsultarUltimo + SolicitarCAE
5. Retorna CAE o error
```

### **🔌 Flujo SOAP (Middleware)**
```
1. Cliente obtiene token de AFIP manualmente
2. SOAP solicitarCAE con token
3. Sistema valida token
4. Pasamanos directo a SolicitarCAEUseCase
5. Retorna XML con CAE o error
```

## 🚀 Casos de Uso Expuestos

### **REST Controllers**
- ✅ `AutenticacionController` → `AutenticarWSFEUseCase`, `AutenticarWSMTXCAUseCase`
- ✅ `FacturacionController` → `GenerarFacturaUseCase` + auto-auth
- ✅ `ConsultasController` → `ConsultarPuntosVentaUseCase`, `ConsultarUltimoComprobanteUseCase`, `ConsultarCAEUseCase`

### **SOAP Service**
- ✅ `AfipSoapService` → `SolicitarCAEUseCase` directo (pasamanos)

## 🔧 Configuración

### **application.yml**
```yaml
server:
  port: 8080

spring:
  application:
    name: afip-electronic-billing-api

# AFIP Configuration
afip:
  cert:
    path: src/main/resources/certificates/certificado.p12
    password: clave123
    alias: fulloptica
  emisor:
    cuit: 27362932039
    punto-venta-default: 1
```

## 📱 Ejemplos de Uso

### **cURL REST**
```bash
# Facturar consumidor final
curl -X POST "http://localhost:8080/api/facturacion/consumidor-final?tipo=C&puntoVenta=1&importe=100.00"

# Consultar puntos de venta
curl "http://localhost:8080/api/consultas/puntos-venta"

# Facturar con JSON
curl -X POST "http://localhost:8080/api/facturacion/facturar" \
  -H "Content-Type: application/json" \
  -d '{"tipoComprobante":"C","puntoVenta":1,"importe":100.00,"cuitCliente":0}'
```

### **SOAP Client**
```java
// Cliente SOAP para solicitar CAE con token
AfipSoapService service = new AfipSoapService();
String response = service.solicitarCAE(
    "TOKEN_WSFE", "SIGNATURE", 11, 1, 124, 
    "20240115", 0, 99, "100.00", "0.00", "100.00"
);
```

Esta arquitectura proporciona **flexibilidad máxima**:
- **REST** para aplicaciones modernas con lógica automática
- **SOAP** para integración directa tipo middleware
- **Casos de uso granulares** expuestos según necesidad