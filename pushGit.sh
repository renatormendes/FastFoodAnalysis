#!/bin/bash

echo "### Enviando para o GitHub ###"

git init
echo "*.class" >> .gitignore
echo "*.db" >> .gitignore
echo "*.bin" >> .gitignore
echo "lib/" >> .gitignore

git add .
read -p "Mensagem do commit: " msg
git commit -m "$msg"
git branch -M main
read -p "https://github.com/renatormendes/ " url
git remote add origin $url
git push -u origin main

echo "[OK] Repositório atualizado!"