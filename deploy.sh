#!/bin/bash

# Script de despliegue para Digital Ocean
echo "=== Despliegue AFIP Billing System ==="

# Verificar e instalar Java 17
JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" != "17" ]; then
    echo "Instalando Java 17..."
    sudo apt update
    sudo apt install -y openjdk-17-jre-headless
    
    # Configurar Java 17 como default
    sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-amd64/bin/java 1700
    sudo update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java
fi

# Crear directorio de aplicación
sudo mkdir -p /opt/afip-billing
sudo chown $USER:$USER /opt/afip-billing

# Copiar archivos (asumiendo que ya están en el directorio actual)
cp afip-billing-1.0.jar /opt/afip-billing/
cp application-prod.properties /opt/afip-billing/
cp certificado.p12 /opt/afip-billing/

# Crear servicio systemd
sudo tee /etc/systemd/system/afip-billing.service > /dev/null <<EOF
[Unit]
Description=AFIP Billing System
After=network.target

[Service]
Type=simple
User=$USER
WorkingDirectory=/opt/afip-billing
ExecStart=/usr/lib/jvm/java-17-openjdk-amd64/bin/java -jar afip-billing-1.0.jar --spring.config.location=application-prod.properties
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Habilitar y iniciar servicio
sudo systemctl daemon-reload
sudo systemctl enable afip-billing
sudo systemctl start afip-billing

echo "✅ Aplicación desplegada en puerto 8080"
echo "📊 Estado: sudo systemctl status afip-billing"
echo "📋 Logs: sudo journalctl -u afip-billing -f"