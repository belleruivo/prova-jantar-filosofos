# 🍝 Jantar dos Filósofos - Soluções de Sincronização

> Implementação de 4 soluções para o problema clássico do Jantar dos Filósofos  
> **Disciplina**: Programação Paralela e Distribuída | **Data**: Dezembro 2025

## 📖 Sobre o Projeto

Este projeto implementa **quatro soluções diferentes** para o problema do **Jantar dos Filósofos** (Dijkstra, 1965), abordando os desafios de **deadlock**, **starvation** e **fairness** em sistemas concorrentes.

- **Tarefa 1**: Implementação com deadlock (demonstração)
- **Tarefa 2**: Prevenção via limite de filósofos comendo simultaneamente
- **Tarefa 3**: Prevenção via ordenação de recursos
- **Tarefa 4**: Prevenção via hierarquia com arbitrador (Mesa)

📄 **Documentação completa**: Consulte [RELATORIO.md](RELATORIO.md) para análise detalhada e [docs/ARQUITETURA.md](docs/ARQUITETURA.md) para conceitos técnicos

---

## 🗂️ Estrutura do Repositório

```
prova-jantar-filosofos/
├── README.md                    # Este arquivo - instruções gerais
├── RELATORIO.md                 # Relatório completo do projeto
├── .gitignore                   # Arquivos ignorados pelo Git
├── junit-platform-console-standalone-1.9.3.jar  # JUnit para testes
├── src/
│   ├── tarefa1/                 # ❌ Implementação com DEADLOCK
│   │   ├── Filosofo.java
│   │   ├── Garfo.java
│   │   └── Main.java
│   ├── tarefa2/                 # ✅ Solução: Limite de filósofos
│   │   ├── Filosofo.java
│   │   ├── Garfo.java
│   │   └── Main.java
│   ├── tarefa3/                 # ✅ Solução: Ordenação de recursos
│   │   ├── Filosofo.java
│   │   ├── Garfo.java
│   │   └── Main.java
│   └── tarefa4/                 # ✅ Solução: Hierarquia + Arbitrador
│       ├── Filosofo.java
│       ├── Mesa.java
│       └── Main.java
├── test/
│   ├── Tarefa2Test.java         # Testes para Tarefa 2
│   ├── Tarefa3Test.java         # Testes para Tarefa 3
│   └── Tarefa4Test.java         # Testes para Tarefa 4
└── docs/
    └── ARQUITETURA.md           # Documentação técnica da arquitetura
```

---

## ⚙️ Como Compilar e Executar

### Pré-requisitos

- **Java JDK 11** ou superior
- Terminal (PowerShell/CMD no Windows, Bash no Linux/Mac)

### Compilação

Navegue até o diretório raiz do projeto e compile todas as tarefas:

```bash
# Navegar para o diretório do projeto
cd c:\Users\isabe\prova-jantar-filosofos

# Compilar todas as tarefas
javac src/tarefa1/*.java
javac src/tarefa2/*.java
javac src/tarefa3/*.java
javac src/tarefa4/*.java
```

### Executar

```bash
# Tarefa 1 - Com Deadlock (⚠️ pode travar - use Ctrl+C)
java -cp src tarefa1.Main

# Tarefa 2 - Ordem Diferente (5 min)
java -cp src tarefa2.Main

# Tarefa 3 - Semáforos (5 min)
java -cp src tarefa3.Main

# Tarefa 4 - Monitores (5 min)
java -cp src tarefa4.Main
```

---

## 🧪 Testes

### Baixar JUnit

```bash
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.9.3/junit-platform-console-standalone-1.9.3.jar" -OutFile "junit-platform-console-standalone-1.9.3.jar"
```

### Compilar e Executar

```bash
javac -cp "src;junit-platform-console-standalone-1.9.3.jar" test/*.java
java -jar junit-platform-console-standalone-1.9.3.jar --class-path "src;test" --scan-class-path
```

---

## 📊 Resultados

| Métrica | Tarefa 2 | Tarefa 3 | Tarefa 4 | Melhor |
|---------|----------|----------|----------|--------|
| **Throughput (ref/min)** | 56,47 | 49,47 | 43,60 | Tarefa 2 ✅ |
| **Fairness (CV %)** | 6,02 | 1,56 | 1,15 | Tarefa 4 ✅ |
| **Tempo espera (ms)** | 4996 | 4308 | 2895 | Tarefa 4 ✅ |
| **Utilização garfos** | 86,95% | 91,50% | - | Tarefa 3 ✅ |
| **Deadlock** | Não | Não | Não | Todas ✅ |

📄 **Análise completa**: Ver [RELATORIO.md](RELATORIO.md)

---

## 🔧 Troubleshooting

### Programa não compila
```bash
java -version  # Verificar JDK 11+
```

### Deadlock na Tarefa 1
Comportamento esperado! Use `Ctrl+C` para interromper.

### OutOfMemoryError
```bash
java -Xmx512m -cp src tarefa2.Main
```

---

## 📚 Documentação

- **[docs/ARQUITETURA.md](docs/ARQUITETURA.md)** - Conceitos técnicos e comparação de soluções
- **[RELATORIO.md](RELATORIO.md)** - Análise experimental completa com dados e conclusões

---

**Programação Paralela e Distribuída - 2025**
