#!/bin/bash
JAR_URL="https://maven.org"
LIB_DIR="lib"
JAR_NAME="sqlite-jdbc.jar"
SRC_FILE="src/MainUI.java"
CLASS_NAME="MainUI"

echo "### Build Portfólio Fast Food - Linux Lite ###"

mkdir -p $LIB_DIR
if [ ! -f "$LIB_DIR/$JAR_NAME" ]; then
    echo "[!] Baixando driver SQLite..."
    curl -L $JAR_URL -o "$LIB_DIR/$JAR_NAME"
fi

echo "[!] Compilando $SRC_FILE..."
rm -rf bin && mkdir bin
javac -cp "$LIB_DIR/$JAR_NAME" "$SRC_FILE" -d bin

if [ $? -eq 0 ]; then
    echo "[!] Iniciando aplicação..."
    java -cp "bin:$LIB_DIR/$JAR_NAME" "$CLASS_NAME"
else
    echo "[ERRO] Erro na compilação. Verifique o código."
fi
