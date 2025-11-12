# Guía de Despliegue - Digital Ocean

## Pasos para desplegar en Digital Ocean

### 1. Construir la aplicación (Windows)
```bash
# Ejecutar en Windows
build.bat
```
Esto genera: `target\arca-integration-1.0.0-SNAPSHOT.jar`

### 2. Preparar archivos para subir
Archivos necesarios:
- `afip-billing-1.0.jar` (JAR ejecutable)
- `certificado.p12` (tu certificado AFIP)
- `application-prod.properties`
- `deploy.sh`

### 3. Subir al droplet
```bash
# Subir archivos al droplet
scp afip-billing-1.0.jar root@YOUR_DROPLET_IP:/tmp/
scp certificado.p12 root@YOUR_DROPLET_IP:/tmp/
scp application-prod.properties root@YOUR_DROPLET_IP:/tmp/
scp deploy.sh root@YOUR_DROPLET_IP:/tmp/
```

### 4. Ejecutar despliegue en el droplet
```bash
# Conectar al droplet
ssh root@YOUR_DROPLET_IP

# Ir al directorio temporal
cd /tmp

# Dar permisos y ejecutar
chmod +x deploy.sh
./deploy.sh
```

### 5. Configurar firewall (si es necesario)
```bash
# Abrir puerto 8080
sudo ufw allow 8080
sudo ufw enable
```

### 6. Verificar despliegue
```bash
# Estado del servicio
sudo systemctl status afip-billing

# Ver logs en tiempo real
sudo journalctl -u afip-billing -f

# Probar API
curl http://localhost:8080/api/consultas/puntos-venta
```

## Variables de entorno (opcional)
```bash
# Configurar password del certificado
export AFIP_CERT_PASSWORD=tu_password_real
export AFIP_CUIT=tu_cuit_real
```

## Comandos útiles
```bash
# Reiniciar servicio
sudo systemctl restart afip-billing

# Parar servicio
sudo systemctl stop afip-billing

# Ver logs
sudo journalctl -u afip-billing --since "1 hour ago"

# Actualizar aplicación
sudo systemctl stop afip-billing
cp nuevo-jar.jar /opt/afip-billing/afip-billing-1.0.jar
sudo systemctl start afip-billing
```

## URLs de la aplicación
- **REST API**: `http://YOUR_DROPLET_IP:8080/api/`
- **SOAP API**: `http://YOUR_DROPLET_IP:8080/ws/afip?wsdl`
- **Health Check**: `http://YOUR_DROPLET_IP:8080/actuator/health`