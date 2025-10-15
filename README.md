# AFIP Electronic Billing System - Testing Guide

## Overview
This application provides three interfaces to interact with AFIP web services for electronic billing:
- **UI Console**: Interactive command-line interface
- **REST API**: HTTP REST endpoints
- **SOAP API**: SOAP web service interface

## Prerequisites
- Java 11 or higher
- Maven 3.6+
- Valid AFIP certificate (.p12 file)
- Configured `afip.properties` file

## Configuration
Ensure your `afip.properties` file contains:
```properties
afip.certificate.path=C:/path/to/your/certificate.p12
afip.certificate.password=your_password
afip.cuit=your_cuit
afip.wsaa.url=https://wsaa.afip.gov.ar/ws/services/LoginCms
afip.wsfe.url=https://servicios1.afip.gov.ar/wsfev1/service.asmx
afip.wsmtxca.url=https://servicios1.afip.gov.ar/wsmtxca/service.asmx
```

## AFIP Services and Voucher Types Mapping

### WSFE (Web Service Factura Electrónica)
**Used by**: Responsables Inscriptos and Monotributistas with WSFE permissions
- **Factura A** (Code 1): For registered taxpayers to other registered taxpayers
- **Factura B** (Code 6): For registered taxpayers to final consumers
- **Factura C** (Code 11): For monotributistas or when WSMTXCA is not available

### WSMTXCA (Web Service Monotributo)
**Used by**: Monotributistas with specific WSMTXCA permissions
- **Factura C** (Code 11): Exclusive for monotributistas

### Current Implementation
**All voucher types use WSFE** due to certificate limitations. To use WSMTXCA:
1. Request WSMTXCA permissions from AFIP for your certificate
2. Change `return "wsfe"` to `return "wsmtxca"` in `determinarServicio()` method for Code 11

### Voucher Type Selection Guide
| Your Category | Client Type | Voucher Type | Service |
|---------------|-------------|--------------|----------|
| Responsable Inscripto | Responsable Inscripto | Factura A (1) | WSFE |
| Responsable Inscripto | Consumidor Final | Factura B (6) | WSFE |
| Monotributista | Any | Factura C (11) | WSMTXCA* |

*Currently using WSFE for all due to certificate permissions

---

## 1. UI Console Interface

### Starting the Application
```bash
mvn clean compile exec:java -Dexec.mainClass="com.afip.Main"
```

### Main Menu Options
```
=== AFIP Electronic Billing System ===
1. Authentication Menu
2. Consultation Menu  
3. Billing Menu
4. Configuration Menu
5. Exit
```

### Testing Authentication
1. Select option `1` (Authentication Menu)
2. Choose service to authenticate:
   - `1` for WSFE (Electronic Billing)
   - `2` for WSMTXCA (Monotributo)
3. System will authenticate and save credentials

### Testing Consultations
1. Select option `2` (Consultation Menu)
2. Available options:
   - `1` - Consult Sales Points
   - `2` - Consult Last Voucher
   - `3` - Consult CAE

#### Example: Consulting Sales Points
```
Select option: 1
Service (wsfe/wsmtxca): wsfe
Sales Points found:
- Point: 1, Description: Electronic PV, Type: RECE
```

#### Example: Consulting Last Voucher
```
Select option: 2
Service: wsfe
Sales Point: 1
Voucher Type (1-Factura A, 6-Factura B, 11-Factura C): 11
Last voucher number: 15
```

### Testing Billing
1. Select option `3` (Billing Menu)
2. Choose `1` (Generate Electronic Invoice)
3. Follow prompts:
   - Sales Point: `1`
   - Voucher Type: `11` (Factura C for Monotributista)
   - Client Document: `20123456789`
   - Amount: `1000.00`

#### Expected Response
```
CAE Generated Successfully:
- CAE: 74251234567890
- Due Date: 2024-01-20
- Voucher Number: 16
```

---

## 2. REST API Interface

### Starting the REST Server
```bash
mvn spring-boot:run
```
Server starts on `http://localhost:8080`

### Available Endpoints

#### Authentication Endpoints
```http
POST /api/auth/wsfe
POST /api/auth/wsmtxca
```

#### Consultation Endpoints
```http
GET /api/consultas/puntos-venta?service={wsfe|wsmtxca}
GET /api/consultas/ultimo-comprobante?service={service}&puntoVenta={pv}&tipoComprobante={tipo}
GET /api/consultas/cae?service={service}&puntoVenta={pv}&tipoComprobante={tipo}&numero={num}
```

#### Billing Endpoints
```http
POST /api/facturacion/generar
```

### Testing with cURL

#### 1. Authenticate WSFE
```bash
curl -X POST http://localhost:8080/api/auth/wsfe \
  -H "Content-Type: application/json"
```

#### 2. Consult Sales Points
```bash
curl -X GET "http://localhost:8080/api/consultas/puntos-venta?service=wsfe"
```

#### 3. Consult Last Voucher
```bash
curl -X GET "http://localhost:8080/api/consultas/ultimo-comprobante?service=wsfe&puntoVenta=1&tipoComprobante=11"
```

#### 4. Generate Invoice (with Client CUIT)
```bash
curl -X POST http://localhost:8080/api/facturacion/generar \
  -H "Content-Type: application/json" \
  -d '{
    "puntoVenta": 1,
    "tipoComprobante": 11,
    "numeroDocumento": "20123456789",
    "tipoDocumento": 80,
    "importeTotal": 1000.00,
    "importeNeto": 1000.00,
    "importeIVA": 0.00,
    "fechaComprobante": "2024-01-15"
  }'
```

#### 5. Generate Invoice (Consumer Final - Factura B)
```bash
curl -X POST http://localhost:8080/api/facturacion/generar \
  -H "Content-Type: application/json" \
  -d '{
    "puntoVenta": 1,
    "tipoComprobante": 6,
    "tipoDocumento": 99,
    "importeTotal": 1000.00,
    "importeNeto": 826.45,
    "importeIVA": 173.55,
    "fechaComprobante": "2024-01-15"
  }'
```
```

#### Expected JSON Response
```json
{
  "success": true,
  "cae": "74251234567890",
  "fechaVencimiento": "2024-01-20",
  "numeroComprobante": 16,
  "message": "CAE generated successfully",
  "observaciones": null,
  "afipResponse": "<?xml version=\"1.0\" encoding=\"utf-8\"?>..."
}
```

**Note**: For consumer final invoices, use:
- **Factura B** (code 6): For amounts with IVA included
- **Factura C** (code 11): For monotributista (no IVA breakdown)
- **tipoDocumento**: 99 (Consumer Final)
- **numeroDocumento**: Can be omitted or empty

---

## 3. SOAP API Interface

### SOAP Service URL
```
http://localhost:8080/ws/afip
```

### WSDL Location
```
http://localhost:8080/ws/afip?wsdl
```

### Available SOAP Operations
- `autenticarWSFE`
- `autenticarWSMTXCA`
- `consultarPuntosVenta`
- `consultarUltimoComprobante`
- `consultarCAE`
- `generarFactura`

### Testing with SOAP Requests

#### 1. Authenticate WSFE
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:afip="http://afip.com/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <afip:autenticarWSFERequest/>
   </soapenv:Body>
</soapenv:Envelope>
```

#### 2. Consult Sales Points
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:afip="http://afip.com/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <afip:consultarPuntosVentaRequest>
         <afip:service>wsfe</afip:service>
      </afip:consultarPuntosVentaRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

#### 3. Generate Invoice
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:afip="http://afip.com/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <afip:generarFacturaRequest>
         <afip:puntoVenta>1</afip:puntoVenta>
         <afip:tipoComprobante>11</afip:tipoComprobante>
         <afip:numeroDocumento>20123456789</afip:numeroDocumento>
         <afip:tipoDocumento>80</afip:tipoDocumento>
         <afip:importeTotal>1000.00</afip:importeTotal>
         <afip:importeNeto>1000.00</afip:importeNeto>
         <afip:importeIVA>0.00</afip:importeIVA>
         <afip:fechaComprobante>2024-01-15</afip:fechaComprobante>
      </afip:generarFacturaRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

#### Using cURL for SOAP
```bash
curl -X POST http://localhost:8080/ws/afip \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"http://afip.com/ws/generarFactura\"" \
  -d @soap_request.xml
```

#### Expected SOAP Response
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:generarFacturaResponse xmlns:ns2="http://afip.com/ws">
         <ns2:success>true</ns2:success>
         <ns2:cae>74251234567890</ns2:cae>
         <ns2:fechaVencimiento>2024-01-20</ns2:fechaVencimiento>
         <ns2:numeroComprobante>16</ns2:numeroComprobante>
         <ns2:message>CAE generated successfully</ns2:message>
      </ns2:generarFacturaResponse>
   </soap:Body>
</soap:Envelope>
```

---

## Troubleshooting

### Common Issues

#### Certificate Problems
- **Error**: "Certificate not found"
- **Solution**: Verify certificate path in `afip.properties`
- **Check**: Certificate file exists and has proper permissions

#### Authentication Failures
- **Error**: "Authentication failed"
- **Solution**: 
  1. Clear credentials: Use Configuration Menu → Clear Credentials
  2. Re-authenticate with correct certificate
  3. Verify CUIT matches certificate

#### Point of Sale Issues
- **Error**: "Invalid sales point"
- **Solution**: Use electronic sales points (type RECE) for web services
- **Check**: Consult available sales points first

#### Voucher Type Errors
- **Error**: "Invalid voucher type for taxpayer"
- **Solution**: 
  - Monotributista: Use type 11 (Factura C)
  - Responsable Inscripto: Use type 1 (Factura A) or 6 (Factura B)

### Testing Tips

1. **Start with Authentication**: Always authenticate before other operations
2. **Use Correct Sales Points**: Electronic PV for web services, not manual ones
3. **Check Voucher Types**: Match voucher type with taxpayer category
4. **Verify Amounts**: Use realistic amounts (avoid 0 or negative values)
5. **Date Format**: Use YYYY-MM-DD format for dates

### Log Files
Check application logs for detailed error information:
- Console output shows request/response details
- Authentication tokens are logged (masked for security)
- SOAP envelopes are logged for debugging

---

## Production Considerations

- **Certificate Security**: Store certificates securely, never in version control
- **Credential Management**: Tokens expire, implement automatic re-authentication
- **Error Handling**: Implement proper retry logic for network failures
- **Rate Limiting**: AFIP services have rate limits, implement appropriate delays
- **Monitoring**: Log all transactions for audit purposes