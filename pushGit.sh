#!/bin/bash

echo "### Preparando envio para o GitHub ###"

# Garante que o ignore não envie arquivos desnecessários
echo "*.class" > .gitignore
echo "bin/" >> .gitignore
echo "*.db" >> .gitignore
echo "*.bin" >> .gitignore
echo "lib/" >> .gitignore

# Adiciona arquivos
git add .

read -p "Digite a mensagem do commit: " msg
git commit -m "$msg"

# Ajusta o branch para main
git branch -M main

# URL correta baseada no seu perfil
URL="https://github.com/FastFoodAnalysis"

# Se o 'origin' já existir, ele atualiza a URL. Se não, ele cria.
if git remote | grep -q "origin"; then
    git remote set-url origin "$URL"
else
    git remote add origin "$URL"
fi

echo "[!] Enviando para: $URL"
git push -u origin main

echo "### [OK] Processo concluído! ###"

