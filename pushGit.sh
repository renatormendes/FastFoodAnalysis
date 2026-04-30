#!/bin/bash

# 1. Configuração Manual Garantida
USUARIO="renatormendes"
NOME_PASTA=$(basename "$(pwd)")
URL_FINAL="https://github.com/${USUARIO}/${NOME_PASTA}.git"

echo "### Iniciando Automação Total: $NOME_PASTA ###"

# 2. Inicialização do Git
if [ ! -d ".git" ]; then
    git init
    git branch -M main
fi

# 3. Configuração do .gitignore
echo -e "*.class\nbin/\nlib/*.jar\n*.db\n*.bin\ndeploy.sh" > .gitignore

# 4. Commit
git add .
read -p "Mensagem do commit: " msg
[ -z "$msg" ] && msg="Deploy $(date +'%d/%m/%Y')"
git commit -m "$msg"

# 5. Reset do Remote (Limpa qualquer erro anterior de URL)
git remote remove origin 2>/dev/null
git remote add origin "$URL_FINAL"

# 6. Envio e Abertura do Navegador
echo "[!] Forçando envio para: $URL_FINAL"
if git push -u origin main; then
    echo -e "\n### [OK] SUCESSO! ###"
    xdg-open "https://github.com/${USUARIO}/${NOME_PASTA}" &
else
    echo -e "\n### [ERRO] O GitHub recusou o envio. ###"
    echo "Certifique-se que o repositório '${NOME_PASTA}' existe em ://github.com/${USUARIO}"
fi
