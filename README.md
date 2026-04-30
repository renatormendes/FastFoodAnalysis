# Fast-Food Sales Analysis
Sistema CRUD em Java com SQLite e Cache Binário desenvolvido no Linux Lite.

# 🍔 Fast-Food Sales Analysis

Este projeto é uma aplicação desktop meticulosa para análise e gerenciamento de vendas de uma rede de fast-food. Desenvolvido em ambiente **Linux Lite 7.8**, o software demonstra habilidades em persistência de dados híbrida (SQL + Binário), interface gráfica (Swing) e automação de processos via Shell Script.

## 🚀 Tecnologias Utilizadas

- **Linguagem:** Java (JDK 17+)
- **Interface Gráfica:** Java Swing
- **Banco de Dados:** SQLite (via JDBC)
- **Persistência de Cache:** Java Serialization (Arquivos .bin)
- **Editor:** Sublime Text
- **Automação:** Shell Script (Bash)
- **Sistema Operacional:** Linux Lite 7.8

## 🛠️ Funcionalidades Principais

- **CRUD Completo:** Inserção, edição, listagem e exclusão de vendas.
- **Persistência Híbrida:** 
    - Salvamento imediato em cache binário (`.bin`) após cada alteração.
    - Sincronização definitiva no banco SQLite (`.db`) apenas ao encerrar o programa.
- **Relatórios Inteligentes:** Processamento de dados via **Java Streams API** para gerar estatísticas de itens mais pedidos e faturamento por categoria.
- **Exportação e Impressão:** 
    - Exportação de dados para formato CSV.
    - Sistema de impressão integrado para relatórios (suporta PDF e impressoras físicas).
- **Tratamento de Erros:** Validação robusta de entradas numéricas para evitar falhas em tempo de execução.

## 🧠 Desafios Técnicos e Soluções

Durante o desenvolvimento, foram enfrentados desafios que exigiram soluções arquiteturais específicas:

- **Conflito de Esquema no SQLite:** Após a evolução do modelo de dados, surgiu um erro de inconsistência de colunas (`SQLITE_ERROR`). A solução foi refatorar o método de persistência para usar **PreparedStatements** com mapeamento explícito de colunas, garantindo que a evolução da aplicação não quebrasse a integridade do banco de dados.
- **Sincronização de Cache:** Para evitar acessos constantes e lentos ao disco (I/O) no banco de dados, implementei uma camada de **Cache Binário** via Serialização. Isso permite que a aplicação seja extremamente rápida durante o uso, deixando a carga pesada de gravação no banco apenas para o encerramento da sessão.
- **Compatibilidade de Impressão no Linux:** Ajustei a integração com o serviço **CUPS** do Linux para garantir que o diálogo de impressão fosse nativo e interativo, permitindo a exportação precisa para PDF sem perda de formatação monoespaçada.


## 📁 Estrutura do Projeto

```text
FastFoodAnalysis/
├── src/
│   └── MainUI.java       # Código fonte principal
├── lib/
│   └── sqlite-jdbc.jar   # Driver de conexão (gerido pelo build.sh)
├── bin/                  # Bytecodes compilados (gerado no build)
├── build.sh              # Script de automação: Compila e Executa
├── push.sh               # Script de automação: Envio para GitHub
└── README.md             # Documentação do projeto
```

## ⚙️ Como Executar

No terminal do seu Linux, dentro da pasta do projeto, siga os passos:

1. **Dar permissão de execução ao script:**
   ```bash
   chmod +x build.sh
   ```

2. **Rodar a automação:**
   ```bash
   ./build.sh
   ```
   *O script baixará as dependências necessárias, compilará o código e iniciará a interface gráfica automaticamente.*

## 📈 Exemplo de Relatório
Ao clicar em "Relatórios", o sistema gera um resumo formatado:
- **Ranking de Pedidos:** Ordena os itens pela frequência de venda.
- **Faturamento por Categoria:** Soma os valores totais de Lanches, Bebidas, etc.

## ✒️ Autor
Desenvolvido por **Renato Mendes** como parte de um portfólio de engenharia de software e análise de dados.

