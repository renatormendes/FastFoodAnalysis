#!/bin/bash

# Identifica automaticamente o nome da pasta atual como o nome do REPO
USER="renatormendes"
REPO=$(basename "$(pwd)")
URL="https://github.com"

echo "### Iniciando Automação Total: $REPO ###"

# 1. Inicializa o Git se não existir
if [ ! -d ".git" ]; then
    echo "[!] Inicializando repositório Git local em: $REPO"
    git init
    git branch -M main
fi

# 2. Configura o .gitignore
cat <<EOF > .gitignore
*.class
bin/
lib/*.jar
*.db
*.bin
.DS_Store
deploy.sh
EOF

# 3. Adiciona arquivos e Comita
echo "[!] Preparando arquivos..."
git add .
read -p "Mensagem do commit (Enter para padrão): " msg
[ -z "$msg" ] && msg="Auto-deploy: $(date +'%d/%m/%Y %H:%M')"
git commit -m "$msg"

# 4. Configura o Remote
git remote set-url origin "$URL" 2>/dev/null || git remote add origin "$URL"

# 5. Envio
echo "[!] Enviando para: $URL"
git push -u origin main

if [ $? -eq 0 ]; then
    echo -e "\n### [OK] SUCESSO! ABRINDO O NAVEGADOR... ###"
    # Comando para abrir o navegador no Linux (Linux Lite/Ubuntu/Debian)
    xdg-open "https://github.com" &
else
    echo -e "\n### [ERRO] O envio falhou. ###"
    echo "Verifique se o repositório '$REPO' foi criado no seu GitHub."
fi
