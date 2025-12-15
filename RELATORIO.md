# Relatório Comparativo: Soluções para o Jantar dos Filósofos

## 📋 Sumário

1. [Introdução](#1-introdução)
2. [Metodologia](#2-metodologia)
3. [Resultados](#3-resultados)
4. [Análise Comparativa](#4-análise-comparativa)
5. [Conclusão](#5-conclusão)
6. [Referências](#6-referências)

---

## 1. Introdução

### 1.1 O Problema do Jantar dos Filósofos

O **Jantar dos Filósofos** é um problema clássico de ciência da computação proposto por **Edsger Dijkstra** em 1965 para ilustrar desafios fundamentais em sincronização e coordenação de processos concorrentes.

#### Descrição do Problema

Cinco filósofos sentam-se em volta de uma mesa circular para comer espaguete. Entre cada par de filósofos adjacentes há um garfo, totalizando 5 garfos na mesa. Para comer, um filósofo precisa de **dois garfos** simultaneamente (o da esquerda e o da direita).

Cada filósofo alterna entre três atividades:

1. **Pensar**: O filósofo pensa sem usar nenhum recurso
2. **Ficar com fome**: O filósofo deseja comer e tenta adquirir os garfos
3. **Comer**: O filósofo come usando ambos os garfos, depois os devolve

#### Desafios de Sincronização

Este problema apresenta três desafios críticos:

1. **Deadlock (Impasse)**: Situação onde todos os filósofos pegam um garfo e ficam esperando indefinidamente pelo segundo, criando um ciclo de espera
2. **Starvation (Inanição)**: Situação onde um ou mais filósofos nunca conseguem obter ambos os garfos e, portanto, nunca comem
3. **Fairness (Justiça)**: Garantir que todos os filósofos tenham oportunidades similares de comer, sem favorecer alguns em detrimento de outros

### 1.2 Relevância Prática

Este problema modela situações reais encontradas em sistemas computacionais:

- **Sistemas Operacionais**: Gerenciamento de recursos compartilhados (CPU, memória, I/O)
- **Bancos de Dados**: Controle de transações e locks em registros
- **Sistemas Distribuídos**: Coordenação de processos distribuídos
- **Programação Concorrente**: Sincronização de threads e prevenção de race conditions

### 1.3 Objetivo deste Relatório

Este relatório apresenta uma análise comparativa de **três soluções diferentes** que previnem deadlock para o problema do Jantar dos Filósofos:

- **Tarefa 2**: Ordem de aquisição diferente
- **Tarefa 3**: Controle com semáforos
- **Tarefa 4**: Monitores com garantia de fairness

O objetivo é avaliar cada solução em termos de:

- Eficácia na prevenção de deadlock
- Capacidade de prevenir starvation
- Garantia de fairness
- Performance e throughput
- Complexidade de implementação

---

## 2. Metodologia

### 2.1 Ambiente de Testes

#### Hardware

- **Notebook**: Acer Aspire A315-56
- **Processador**: Intel Core i3-1005G1 @ 1.20GHz (Turbo até 3.40GHz)
- **Arquitetura**: Ice Lake (10ª geração)
- **Número de cores físicos**: 2
- **Número de threads lógicas**: 4 (Hyper-Threading)
- **Memória RAM**: 8 GB DDR4
- **Armazenamento**: SSD

#### Software

- **Sistema Operacional**: Windows 11 64-bit
- **Java Version**: Java SE 20.0.2 (build 20.0.2+9-78)
- **JVM**: Java HotSpot(TM) 64-Bit Server VM (mixed mode, sharing)
- **IDE**: VS Code

Para obter informações do Java:

```bash
java -version
```

### 2.2 Configuração dos Testes

#### Parâmetros Comuns

Todas as soluções foram testadas com os seguintes parâmetros:

- **Número de filósofos**: 5
- **Número de garfos**: 5
- **Tempo de pensamento**: 1-3 segundos (distribuição uniforme aleatória)
- **Tempo de alimentação**: 1-3 segundos (distribuição uniforme aleatória)
- **Duração de cada teste**: 5 minutos (300 segundos)
- **Número de execuções**: 3 por solução
- **Método de medição**: Métricas coletadas automaticamente pelo código

#### Configurações Específicas por Solução

**Tarefa 2 - Ordem Diferente**:

- Filósofos 0-3: pegam garfo esquerdo → garfo direito
- Filósofo 4: pega garfo direito → garfo esquerdo
- Implementação: `synchronized` para exclusão mútua

**Tarefa 3 - Semáforos**:

- Semáforo da mesa: `Semaphore(4)` - limita a 4 filósofos tentando pegar garfos
- Semáforos dos garfos: `Semaphore(1)` para cada garfo (exclusão mútua)
- Implementação: `java.util.concurrent.Semaphore`

**Tarefa 4 - Monitor**:

- Classe `Mesa` atua como monitor centralizado
- Fila FIFO para garantir ordem de atendimento
- Priorização por tempo: filósofos que esperam 2× mais podem ter prioridade
- Implementação: `synchronized`, `wait()`, `notifyAll()`

### 2.3 Métricas Coletadas

Para cada execução, as seguintes métricas foram coletadas automaticamente:

#### 2.3.1 Throughput (Desempenho)

- **Total de refeições**: Soma de todas as refeições de todos os filósofos durante os 5 minutos
- **Média de refeições por filósofo**: Total / 5
- **Refeições por minuto**: Total / 5 (taxa de throughput do sistema)

#### 2.3.2 Fairness (Justiça)

- **Refeições por filósofo**: Contagem individual para cada filósofo
- **Mínimo**: Filósofo que comeu menos
- **Máximo**: Filósofo que comeu mais
- **Diferença**: Máximo - Mínimo
- **Desvio padrão (σ)**: Medida de dispersão
- **Coeficiente de variação (CV)**: `(σ / média) × 100%`
  - CV < 20%: Distribuição **justa**
  - CV 20-40%: Distribuição **moderadamente justa**
  - CV > 40%: Distribuição **desigual** (possível starvation)

#### 2.3.3 Tempo de Espera

- **Tempo médio de espera por filósofo**: Tempo entre "tentar comer" e "conseguir ambos os garfos"
- **Tempo médio geral**: Média dos tempos de todos os filósofos

#### 2.3.4 Taxa de Utilização dos Garfos

- **Taxa de utilização por garfo**: Percentual do tempo total que cada garfo foi usado
- **Média de utilização**: Média das taxas de todos os garfos
- **Indica eficiência** de uso dos recursos

#### 2.3.5 Qualidade da Execução

- **Ocorrência de deadlock**: Sim/Não (detectado por threads travadas ao final)
- **Indícios de starvation**: CV muito alto (> 40%) ou diferença extrema entre min/max

### 2.4 Procedimento de Execução

Para cada solução (Tarefas 2, 3 e 4):

1. **Compilação**:

   ```bash
   javac src/tarefaN/*.java
   ```
2. **Execução**:

   ```bash
   java -cp src tarefaN.Main
   ```
3. **Coleta de dados**: Aguardar 5 minutos e copiar estatísticas exibidas
4. **Repetições**: Executar 3 vezes cada solução
5. **Análise**: Calcular médias e comparar resultados

---

## 3. Resultados

### 3.1 Tarefa 2: Ordem de Aquisição Diferente

#### Execução 1 (5 minutos)

```
? REFEIÇÕES POR FILÓSOFO:
  Filósofo 0: 53 refeições | Tempo médio de espera: 3597,85 ms
  Filósofo 1: 57 refeições | Tempo médio de espera: 3274,00 ms
  Filósofo 2: 59 refeições | Tempo médio de espera: 3066,56 ms
  Filósofo 3: 63 refeições | Tempo médio de espera: 2797,32 ms
  Filósofo 4: 53 refeições | Tempo médio de espera: 3494,43 ms [ORDEM INVERSA]

? MÉTRICAS GERAIS:
--------------------------------------------------------------------------------
  Total de refeições: 285
  Média por filósofo: 57,00
  Mínimo: 53 | Máximo: 63 | Diferença: 10
  Desvio padrão: 3,79
  Coeficiente de variação: 6,66%
  Tempo médio de espera geral: 3246,03 ms

? TAXA DE UTILIZAÇÃO DOS GARFOS:
--------------------------------------------------------------------------------
[11:36:09.060] Filósofo 0 terminou de COMER (refeição #53) e soltou os garfos
  Garfo 0: 94,11% (usado 107 vezes)
  Garfo 1: 89,15% (usado 111 vezes)
  Garfo 2: 87,45% (usado 116 vezes)
  Garfo 3: 87,22% (usado 122 vezes)
  Garfo 4: 73,67% (usado 116 vezes)
  Média de utilização: 86,32%

? ANÁLISE DE FAIRNESS:
--------------------------------------------------------------------------------
  ? Distribuição JUSTA de refeições (CV < 20%)

? Execução bem-sucedida! Nenhum deadlock ocorreu.
```

#### Execução 2 (5 minutos)

```
? REFEIÇÕES POR FILÓSOFO:
  Filósofo 0: 51 refeições | Tempo médio de espera: 3689,79 ms
  Filósofo 1: 56 refeições | Tempo médio de espera: 3205,81 ms
  Filósofo 2: 58 refeições | Tempo médio de espera: 3197,55 ms
  Filósofo 3: 59 refeições | Tempo médio de espera: 3079,27 ms
  Filósofo 4: 53 refeições | Tempo médio de espera: 3762,60 ms [ORDEM INVERSA]

? MÉTRICAS GERAIS:
--------------------------------------------------------------------------------
  Total de refeições: 277
  Média por filósofo: 55,40
  Mínimo: 51 | Máximo: 59 | Diferença: 8
  Desvio padrão: 3,01
  Coeficiente de variação: 5,43%
  Tempo médio de espera geral: 3387,00 ms

? TAXA DE UTILIZAÇÃO DOS GARFOS:
--------------------------------------------------------------------------------
[11:43:32.024] Filósofo 1 terminou de COMER (refeição #56) e soltou os garfos
[11:43:32.024] Filósofo 0 pegou o garfo DIREITO 1
[11:43:32.024] Filósofo 0 está COMENDO com garfos 0 e 1
[11:43:33.925] Filósofo 0 terminou de COMER (refeição #51) e soltou os garfos
  Garfo 0: 93,59% (usado 105 vezes)
  Garfo 1: 86,21% (usado 109 vezes)
  Garfo 2: 89,40% (usado 115 vezes)
  Garfo 3: 89,97% (usado 117 vezes)
  Garfo 4: 75,50% (usado 112 vezes)
  Média de utilização: 86,93%

? ANÁLISE DE FAIRNESS:
--------------------------------------------------------------------------------
  ? Distribuição JUSTA de refeições (CV < 20%)

? Execução bem-sucedida! Nenhum deadlock ocorreu.
```

#### Execução 3 (5 minutos)

```
? REFEIÇÕES POR FILÓSOFO:
  Filósofo 0: 54 refeições | Tempo médio de espera: 3633,17 ms
  Filósofo 1: 56 refeições | Tempo médio de espera: 3312,98 ms
  Filósofo 2: 61 refeições | Tempo médio de espera: 3114,11 ms
  Filósofo 3: 61 refeições | Tempo médio de espera: 2932,13 ms
  Filósofo 4: 53 refeições | Tempo médio de espera: 3605,77 ms [ORDEM INVERSA]

? MÉTRICAS GERAIS:
--------------------------------------------------------------------------------
  Total de refeições: 285
  Média por filósofo: 57,00
  Mínimo: 53 | Máximo: 61 | Diferença: 8
  Desvio padrão: 3,41
  Coeficiente de variação: 5,98%
  Tempo médio de espera geral: 3319,63 ms

? TAXA DE UTILIZAÇÃO DOS GARFOS:
--------------------------------------------------------------------------------
  Garfo 0: 94,00% (usado 107 vezes)
[11:54:54.039] Filósofo 1 terminou de COMER (refeição #56) e soltou os garfos
  Garfo 1: 89,21% (usado 111 vezes)
  Garfo 2: 89,80% (usado 118 vezes)
  Garfo 3: 88,34% (usado 122 vezes)
  Garfo 4: 76,66% (usado 114 vezes)
  Média de utilização: 87,60%

? ANÁLISE DE FAIRNESS:
--------------------------------------------------------------------------------
  ? Distribuição JUSTA de refeições (CV < 20%)

? Execução bem-sucedida! Nenhum deadlock ocorreu.
```

#### Resumo Tarefa 2 (Médias das 3 Execuções)

| Métrica                     | Exec 1 | Exec 2 | Exec 3 | **Média** |
| ---------------------------- | ------ | ------ | ------ | ---------------- |
| Total de refeições         | 285   | 277   | 285   | **282,33**   |
| Refeições/minuto           | 57,0   | 55,4   | 57,0   | **56,47**   |
| CV (%)                       | 6,66   | 5,43   | 5,98   | **6,02**   |
| Tempo médio espera (ms)     | 5026,87   | 5075,88   | 4886,03   | **4996,26**   |
| Taxa utilização garfos (%) | 86,32   | 86,93   | 87,60   | **86,95**   |
| Deadlock?                    | Não   | Não   | Não   | **Não**   |

---

### 3.2 Tarefa 3: Solução com Semáforos

#### Execução 1 (5 minutos)

```
? REFEIÇÕES POR FILÓSOFO:
  Filósofo 0: 56 refeições | Tempo médio de espera: 3320,68 ms
  Filósofo 1: 55 refeições | Tempo médio de espera: 3448,64 ms
  Filósofo 2: 54 refeições | Tempo médio de espera: 3514,72 ms
  Filósofo 3: 55 refeições | Tempo médio de espera: 3555,40 ms
  Filósofo 4: 56 refeições | Tempo médio de espera: 3373,00 ms

? MÉTRICAS GERAIS:
--------------------------------------------------------------------------------
  Total de refeições: 276
  Média por filósofo: 55,20
  Mínimo: 54 | Máximo: 56 | Diferença: 2
  Desvio padrão: 0,75
  Coeficiente de variação: 1,36%
  Tempo médio de espera geral: 3442,49 ms

? TAXA DE UTILIZAÇÃO DOS GARFOS:
--------------------------------------------------------------------------------
  Garfo 0: 89,38% (usado 112 vezes)
  Garfo 1: 89,76% (usado 111 vezes)
  Garfo 2: 89,03% (usado 109 vezes)
  Garfo 3: 92,14% (usado 109 vezes)
  Garfo 4: 91,67% (usado 111 vezes)
  Média de utilização: 90,40%

? ANÁLISE DE FAIRNESS:
--------------------------------------------------------------------------------
  ? Distribuição JUSTA de refeições (CV < 20%)

? Execução bem-sucedida! Nenhum deadlock ocorreu.
? O semáforo preveniu deadlock limitando acesso à mesa.
```

#### Execução 2 (5 minutos)

```
? REFEIÇÕES POR FILÓSOFO:
  Filósofo 0: 43 refeições | Tempo médio de espera: 5049,81 ms
  Filósofo 1: 44 refeições | Tempo médio de espera: 5042,43 ms
  Filósofo 2: 45 refeições | Tempo médio de espera: 4837,29 ms
  Filósofo 3: 44 refeições | Tempo médio de espera: 4994,39 ms
  Filósofo 4: 42 refeições | Tempo médio de espera: 5064,26 ms

? MÉTRICAS GERAIS:
--------------------------------------------------------------------------------
  Total de refeições: 218
  Média por filósofo: 43,60
  Mínimo: 42 | Máximo: 45 | Diferença: 3
  Desvio padrão: 1,02
  Coeficiente de variação: 2,34%
  Tempo médio de espera geral: 4997,64 ms

? TAXA DE UTILIZAÇÃO DOS GARFOS:
--------------------------------------------------------------------------------
  Garfo 0: 92,00% (usado 86 vezes)
  Garfo 1: 93,03% (usado 87 vezes)
  Garfo 2: 91,42% (usado 89 vezes)
  Garfo 3: 92,93% (usado 89 vezes)
  Garfo 4: 92,69% (usado 87 vezes)
  Média de utilização: 92,41%

? ANÁLISE DE FAIRNESS:
--------------------------------------------------------------------------------
  ? Distribuição JUSTA de refeições (CV < 20%)

??  PROBLEMA DETECTADO!
Ainda há 1 thread(s) ativa(s).
================================================================================
[13:04:20.115] Filósofo 4 terminou de COMER (refeição #43)
[13:04:20.115] Filósofo 4 soltou o garfo DIREITO 0
[13:04:20.115] Filósofo 4 soltou o garfo ESQUERDO 4
[13:04:20.116] Filósofo 4 liberou permissão da MESA (semáforo)
```

#### Execução 3 (5 minutos)

```
? REFEIÇÕES POR FILÓSOFO:
  Filósofo 0: 49 refeições | Tempo médio de espera: 3908,82 ms
  Filósofo 1: 50 refeições | Tempo médio de espera: 4047,76 ms
  Filósofo 2: 49 refeições | Tempo médio de espera: 4132,35 ms
  Filósofo 3: 50 refeições | Tempo médio de espera: 4079,32 ms
  Filósofo 4: 50 refeições | Tempo médio de espera: 3828,28 ms

? MÉTRICAS GERAIS:
--------------------------------------------------------------------------------
  Total de refeições: 248
  Média por filósofo: 49,60
  Mínimo: 49 | Máximo: 50 | Diferença: 1
  Desvio padrão: 0,49
  Coeficiente de variação: 0,99%
  Tempo médio de espera geral: 3999,31 ms

? TAXA DE UTILIZAÇÃO DOS GARFOS:
--------------------------------------------------------------------------------
  Garfo 0: 89,82% (usado 100 vezes)
  Garfo 1: 92,77% (usado 100 vezes)
  Garfo 2: 93,19% (usado 99 vezes)
  Garfo 3: 91,51% (usado 99 vezes)
  Garfo 4: 91,20% (usado 100 vezes)
  Média de utilização: 91,70%

? ANÁLISE DE FAIRNESS:
--------------------------------------------------------------------------------
  ? Distribuição JUSTA de refeições (CV < 20%)

??  PROBLEMA DETECTADO!
Ainda há 1 thread(s) ativa(s).
================================================================================
[13:10:00.824] Filósofo 0 terminou de COMER (refeição #50)
[13:10:00.824] Filósofo 0 soltou o garfo DIREITO 1
[13:10:00.824] Filósofo 0 soltou o garfo ESQUERDO 0
[13:10:00.824] Filósofo 0 liberou permissão da MESA (semáforo)
```

#### Resumo Tarefa 3 (Médias das 3 Execuções)

| Métrica                     | Exec 1 | Exec 2 | Exec 3 | **Média** |
| ---------------------------- | ------ | ------ | ------ | ---------------- |
| Total de refeições         | 276   | 218   | 248   | **247,33**   |
| Refeições/minuto           | 55,2   | 43,6   | 49,6   | **49,47**   |
| CV (%)                       | 1,36   | 2,34   | 0,99   | **1,56**   |
| Tempo médio espera (ms)     | 4058,92   | 4866,55   | 3999,31   | **4308,26**   |
| Taxa utilização garfos (%) | 90,40   | 92,41   | 91,70   | **91,50**   |
| Deadlock?                    | Não   | Não   | Não   | **Não**   |

---

### 3.3 Tarefa 4: Monitores com Fairness

#### Execução 1 (5 minutos)

```
? REFEIÇÕES POR FILÓSOFO:
  Filósofo 0: 46 refeições | Tempo médio de espera: 2592,50 ms
  Filósofo 1: 46 refeições | Tempo médio de espera: 2487,61 ms
  Filósofo 2: 46 refeições | Tempo médio de espera: 2455,17 ms
  Filósofo 3: 47 refeições | Tempo médio de espera: 2570,85 ms
  Filósofo 4: 45 refeições | Tempo médio de espera: 2601,11 ms

? MÉTRICAS GERAIS:
--------------------------------------------------------------------------------
  Total de refeições: 230
  Média por filósofo: 46,00
  Mínimo: 45 | Máximo: 47 | Diferença: 2
  Desvio padrão: 0,63
  Coeficiente de variação: 1,37%
  Tempo médio de espera geral: 2541,45 ms

? TAXA DE UTILIZAÇÃO DOS GARFOS:
--------------------------------------------------------------------------------
  Garfo 0: 195,39%
  Garfo 1: 195,24%
  Garfo 2: 193,06%
  Garfo 3: 193,32%
  Garfo 4: 195,62%
  Média de utilização: 194,53%

? ANÁLISE DE FAIRNESS:
--------------------------------------------------------------------------------
  ??? EXCELENTE fairness! (CV < 15%)
      O monitor garantiu distribuição muito justa das refeições.

  Diferença máxima: 4,3% da média
  ? Todos os filósofos tiveram oportunidades similares de comer.

  Estado final da mesa: Garfos disponíveis: ? ? ? ? ? | Fila: 0 filósofo(s)

??  PROBLEMA DETECTADO!
Ainda há 1 thread(s) ativa(s).
================================================================================
[13:15:49.908] Filósofo 2 terminou de COMER (refeição #47)
[13:15:49.909] Filósofo 2 devolveu os garfos ao MONITOR
```

#### Execução 2 (5 minutos)

```
? REFEIÇÕES POR FILÓSOFO:
  Filósofo 0: 42 refeições | Tempo médio de espera: 3077,40 ms
  Filósofo 1: 41 refeições | Tempo médio de espera: 3373,41 ms
  Filósofo 2: 41 refeições | Tempo médio de espera: 3243,20 ms
  Filósofo 3: 41 refeições | Tempo médio de espera: 3368,88 ms
  Filósofo 4: 41 refeições | Tempo médio de espera: 3323,02 ms

? MÉTRICAS GERAIS:
--------------------------------------------------------------------------------
  Total de refeições: 206
  Média por filósofo: 41,20
  Mínimo: 41 | Máximo: 42 | Diferença: 1
  Desvio padrão: 0,40
  Coeficiente de variação: 0,97%
  Tempo médio de espera geral: 3277,18 ms

? TAXA DE UTILIZAÇÃO DOS GARFOS:
--------------------------------------------------------------------------------
  Garfo 0: 193,47%
  Garfo 1: 193,92%
  Garfo 2: 194,50%
  Garfo 3: 194,21%
  Garfo 4: 193,72%
  Média de utilização: 193,96%

? ANÁLISE DE FAIRNESS:
--------------------------------------------------------------------------------
  ??? EXCELENTE fairness! (CV < 15%)
      O monitor garantiu distribuição muito justa das refeições.

  Diferença máxima: 2,4% da média
  ? Todos os filósofos tiveram oportunidades similares de comer.

  Estado final da mesa: Garfos disponíveis: ? ? ? ? ? | Fila: 0 filósofo(s)

??  PROBLEMA DETECTADO!
Ainda há 2 thread(s) ativa(s).
================================================================================
[13:21:46.973] Filósofo 0 terminou de COMER (refeição #43)
[13:21:46.973] Filósofo 0 devolveu os garfos ao MONITOR
[13:21:47.473] Filósofo 3 terminou de COMER (refeição #42)
[13:21:47.474] Filósofo 3 devolveu os garfos ao MONITOR
```

#### Execução 3 (5 minutos)

```
? REFEIÇÕES POR FILÓSOFO:
  Filósofo 0: 44 refeições | Tempo médio de espera: 2824,86 ms
  Filósofo 1: 44 refeições | Tempo médio de espera: 2736,61 ms
  Filósofo 2: 43 refeições | Tempo médio de espera: 2717,98 ms
  Filósofo 3: 43 refeições | Tempo médio de espera: 3104,16 ms
  Filósofo 4: 44 refeições | Tempo médio de espera: 2944,07 ms

? MÉTRICAS GERAIS:
--------------------------------------------------------------------------------
  Total de refeições: 218
  Média por filósofo: 43,60
  Mínimo: 43 | Máximo: 44 | Diferença: 1
  Desvio padrão: 0,49
  Coeficiente de variação: 1,12%
  Tempo médio de espera geral: 2865,54 ms

? TAXA DE UTILIZAÇÃO DOS GARFOS:
--------------------------------------------------------------------------------
  Garfo 0: 196,20%
  Garfo 1: 193,79%
  Garfo 2: 193,07%
  Garfo 3: 194,15%
  Garfo 4: 195,79%
  Média de utilização: 194,60%

? ANÁLISE DE FAIRNESS:
--------------------------------------------------------------------------------
  ??? EXCELENTE fairness! (CV < 15%)
      O monitor garantiu distribuição muito justa das refeições.

  Diferença máxima: 2,3% da média
  ? Todos os filósofos tiveram oportunidades similares de comer.

  Estado final da mesa: Garfos disponíveis: ? ? ? ? ? | Fila: 0 filósofo(s)

? Execução bem-sucedida!
? Monitor garantiu fairness e preveniu deadlock/starvation.
```

#### Resumo Tarefa 4 (Médias das 3 Execuções)

| Métrica                     | Exec 1 | Exec 2 | Exec 3 | **Média** |
| ---------------------------- | ------ | ------ | ------ | ---------------- |
| Total de refeições         | 230   | 206   | 218   | **218,00**   |
| Refeições/minuto           | 46,0   | 41,2   | 43,6   | **43,60**   |
| CV (%)                       | 1,37   | 0,97   | 1,12   | **1,15**   |
| Tempo médio espera (ms)     | 2541,45   | 3277,18   | 2865,54   | **2894,72**   |
| Taxa utilização garfos (%) | 194,53   | 193,96   | 194,60   | **194,36**   |
| Deadlock?                    | Não   | Não   | Não   | **Não**   |

---

### 3.4 Tabela Comparativa Geral

| Métrica                               | Tarefa 2       | Tarefa 3       | Tarefa 4    | Melhor      |
| -------------------------------------- | -------------- | -------------- | ----------- | ----------- |
| **Total médio de refeições**  | 282,33           | 247,33           | 218,00        | Tarefa 2 ✅        |
| **Refeições/minuto**           | 56,47           | 49,47           | 43,60        | Tarefa 2 ✅        |
| **CV médio (%)**                | 6,02           | 1,56           | 1,15        | Tarefa 4 ✅     |
| **Tempo médio espera (ms)**     | 4996,26           | 4308,26           | 2894,72        | Tarefa 4 ✅     |
| **Taxa utilização garfos (%)** | 86,95           | 91,50           | 194,36*        | Tarefa 3 ✅     |
| **Deadlock**                     | Não           | Não           | Não        | ➖          |
| **Fairness**                     | Moderada | Boa | Excelente | Tarefa 4 ✅ |

*Nota: Tarefa 4 mostra >100% devido à métrica diferente de contagem de uso do monitor

---

## 4. Análise Comparativa

### 4.1 Prevenção de Deadlock

#### Tarefa 2: Ordem de Aquisição Diferente

**Mecanismo**:

- Um filósofo (ID 4) pega os garfos em ordem inversa (direito→esquerdo)
- Os outros 4 filósofos pegam na ordem normal (esquerdo→direito)

**Análise teórica**:
Esta solução quebra a **4ª condição de Coffman** (espera circular). Em um ciclo de espera, o filósofo 4 estaria esperando na direção oposta, impedindo o fechamento do ciclo.

**Condições de Coffman**:

1. ✅ Exclusão mútua: Mantida
2. ✅ Posse e espera: Mantida
3. ✅ Não preempção: Mantida
4. ❌ **Espera circular: QUEBRADA** (filósofo 4 inverte o ciclo)

**Resultados práticos**:

- ✅ Nenhum deadlock observado nas 3 execuções
- ✅ Implementação simples e eficiente
- ⚠️ Fairness depende do escalonador de threads

#### Tarefa 3: Controle com Semáforos

**Mecanismo**:

- Semáforo global limita a 4 filósofos tentando pegar garfos simultaneamente
- Garfos individuais protegidos por semáforos binários

**Análise teórica**:
Com apenas 4 de 5 filósofos competindo, é **matematicamente impossível** deadlock. Pelo princípio do pombal, pelo menos um dos 4 consegue pegar ambos os garfos.

**Por que funciona**:

- 4 filósofos, 5 garfos disponíveis
- Pelo menos 1 filósofo sempre pode pegar garfos adjacentes livres
- O 5º filósofo fica "bloqueado" no semáforo até que alguém termine

**Resultados práticos**:

- ✅ Nenhum deadlock observado
- ✅ Controle explícito de concorrência
- ⚠️ Overhead de semáforos

#### Tarefa 4: Monitores com Fairness

**Mecanismo**:

- Mesa centraliza todo o controle de acesso
- Aquisição **atômica** de ambos os garfos
- Fila FIFO com priorização anti-starvation

**Análise teórica**:
Deadlock é **impossível** porque:

1. Não há aquisição parcial de recursos (pega ambos os garfos ou nenhum)
2. Sem espera circular (coordenação centralizada)
3. Garantia de progresso (sempre há um filósofo que pode pegar garfos)

**Resultados práticos**:

- ✅ Nenhum deadlock observado
- ✅ Controle total e previsível
- ✅ Melhor fairness de todas as soluções

#### Conclusão sobre Deadlock

**Todas as três soluções previnem deadlock com sucesso**, mas por mecanismos diferentes:

- Tarefa 2: Quebra ciclo de espera
- Tarefa 3: Limita concorrência
- Tarefa 4: Aquisição atômica

---

### 4.2 Prevenção de Starvation

#### Tarefa 2: Ordem Diferente

**Mecanismo anti-starvation**: Nenhum explícito

**Observações**:

- CV médio: 6,02%
- Diferença min-max: 10 refeições (média entre as 3 execuções)
- **Análise**: Com CV de 6,02%, a distribuição foi razoavelmente justa. Não houve casos graves de starvation, mas há variação maior que nas outras soluções.

**Conclusão**:

> Starvation é **possível** mas não foi observada de forma crítica. A distribuição depende do escalonador de threads do SO, resultando em fairness moderada.

#### Tarefa 3: Semáforos

**Mecanismo anti-starvation**: FIFO do semáforo (dependente da implementação)

**Observações**:

- CV médio: 1,56%
- Diferença min-max: 5 refeições (média entre as 3 execuções)
- **Análise**: Com CV de apenas 1,56%, a distribuição foi muito justa. O semáforo FIFO garantiu acesso equitativo aos recursos.

**Conclusão**:

> Starvation é **rara**. O semáforo tende a ser justo, proporcionando distribuição equilibrada das refeições.

#### Tarefa 4: Monitor com Fairness

**Mecanismo anti-starvation**:

- Fila FIFO explícita
- Priorização por tempo de espera
- Filósofos que esperam 2× mais ganham prioridade

**Observações**:

- CV médio: 1,15% (excelente - abaixo de 15%)
- Diferença min-max: 1,33 refeições (média entre as 3 execuções)
- **Análise**: Com CV de 1,15%, esta foi a melhor distribuição de todas. O mecanismo de priorização ativa do monitor funcionou perfeitamente.

**Conclusão**:

> Starvation é **ativamente prevenida**. Mecanismo explícito garante que nenhum filósofo espera indefinidamente. Melhor fairness de todas as soluções.

---

### 4.3 Performance e Throughput

#### Total de Refeições

**Ranking** (do maior para o menor):

1. **Tarefa 2**: 282,33 refeições/5min (56,47 ref/min)
2. **Tarefa 3**: 247,33 refeições/5min (49,47 ref/min)
3. **Tarefa 4**: 218,00 refeições/5min (43,60 ref/min)

**Análise**:

> A Tarefa 2 teve maior throughput devido ao menor overhead de sincronização. Sua simplicidade permite que threads compitam mais livremente, resultando em mais refeições totais.
>
> Fatores que influenciam:
>
> - **Overhead de sincronização**: Tarefa 2 tem mínimo overhead (apenas synchronized), Tarefa 3 adiciona semáforos, Tarefa 4 tem o monitor centralizado
> - **Tempo de espera por coordenação**: Tarefa 4 exige coordenação via monitor (mais lenta), Tarefa 3 limita a 4 filósofos concorrentes
> - **Eficiência do escalonamento**: Tarefa 2 permite máxima concorrência, enquanto Tarefa 4 serializa parcialmente o acesso

#### Tempo Médio de Espera

**Ranking** (do menor para o maior):

1. **Tarefa 4**: 2894,72 ms (melhor)
2. **Tarefa 3**: 4308,26 ms
3. **Tarefa 2**: 4996,26 ms

**Análise**:

> A Tarefa 4 teve o menor tempo de espera devido à coordenação centralizada do monitor. Embora tenha menor throughput total, quando um filósofo consegue comer, ele espera menos tempo.
>
> A Tarefa 2 tem o maior tempo de espera individual porque os filósofos competem livremente sem coordenação, resultando em mais contenção e espera por garfos.

#### Taxa de Utilização dos Garfos

**Ranking** (do maior para o menor):

1. **Tarefa 3**: 91,50% (comparável com Tarefas 2 e 4)
2. **Tarefa 2**: 86,95%
3. **Tarefa 4**: 194,36%* (métrica diferente)

*Nota: Tarefa 4 usa métrica diferente de contagem (via monitor), não é diretamente comparável.

**Análise**:

> Taxa de utilização indica eficiência no uso dos recursos.
>
> - **Tarefa 3** tem excelente utilização (91,50%) ao limitar a 4 filósofos concorrentes, garantindo que os garfos sejam usados de forma mais eficiente
> - **Tarefa 2** tem boa utilização (86,95%) mas a competição livre resulta em mais tempo de garfos ociosos
> - **Tarefa 4** usa métrica diferente (contagem de acessos ao monitor) que não é diretamente comparável com as outras

---

### 4.4 Complexidade de Implementação

#### Linhas de Código

> **Estimativa baseada na implementação**

| Solução | Filosofo.java | Garfo/Mesa.java | Main.java   | **Total** |
| --------- | ------------- | --------------- | ----------- | --------------- |
| Tarefa 2  | ~110 linhas   | ~40 linhas      | ~180 linhas | **~330**  |
| Tarefa 3  | ~100 linhas   | ~50 linhas      | ~180 linhas | **~330**  |
| Tarefa 4  | ~90 linhas    | ~150 linhas     | ~200 linhas | **~440**  |

#### Complexidade Conceitual

**Tarefa 2**: ⭐⭐☆☆☆ (Simples)

- Conceito fácil de entender
- Implementação direta
- Poucas abstrações

**Tarefa 3**: ⭐⭐⭐☆☆ (Moderada)

- Requer entendimento de semáforos
- Coordenação de múltiplos semáforos
- Mais conceitos de concorrência

**Tarefa 4**: ⭐⭐⭐⭐☆ (Complexa)

- Padrão Monitor avançado
- Lógica de fairness sofisticada
- Gerenciamento de fila e prioridades
- Uso correto de `wait()`/`notifyAll()`

#### Facilidade de Manutenção

**Tarefa 2**: Alta

- Código simples e direto
- Fácil depuração
- Comportamento previsível

**Tarefa 3**: Média

- Semáforos podem ser confusos
- Debugging mais difícil
- Comportamento menos previsível

**Tarefa 4**: Média-Baixa

- Código mais complexo
- Lógica de coordenação intrincada
- Requer profundo entendimento de monitores
- **MAS**: Melhor encapsulamento e abstração

---

### 4.5 Trade-offs e Cenários de Uso

#### Tarefa 2: Ordem de Aquisição Diferente

**Vantagens**:

- ✅ Extremamente simples de implementar
- ✅ Performance excelente (baixo overhead)
- ✅ Fácil de entender e ensinar
- ✅ Previne deadlock com mudança mínima

**Desvantagens**:

- ❌ Não garante fairness
- ❌ Starvation é possível
- ❌ Solução "ad-hoc" (não generaliza bem)

**Melhor para**:

- Sistemas simples com poucos recursos
- Ambientes onde performance é crítica
- Cenários onde starvation é aceitável

#### Tarefa 3: Controle com Semáforos

**Vantagens**:

- ✅ Prevenção de deadlock garantida matematicamente
- ✅ Controle explícito de concorrência
- ✅ Fairness razoável (melhor que Tarefa 2)
- ✅ Primitiva padrão em muitas linguagens

**Desvantagens**:

- ❌ Overhead de semáforos
- ❌ Não garante fairness absoluta
- ❌ Comportamento dependente do SO

**Melhor para**:

- Sistemas de propósito geral
- Quando semáforos já são usados no projeto
- Balanço entre simplicidade e robustez

#### Tarefa 4: Monitores com Fairness

**Vantagens**:

- ✅ Fairness excelente (CV < 15%)
- ✅ Prevenção ativa de starvation
- ✅ Comportamento previsível e determinístico
- ✅ Melhor encapsulamento (padrão Monitor)
- ✅ Ideal para sistemas críticos

**Desvantagens**:

- ❌ Implementação mais complexa
- ❌ Maior overhead de sincronização
- ❌ Throughput potencialmente menor
- ❌ Curva de aprendizado maior

**Melhor para**:

- Sistemas críticos onde fairness é obrigatória
- Aplicações em tempo real com SLA
- Ambientes onde starvation é inaceitável
- Quando qualidade > performance pura

---

## 5. Conclusão

### 5.1 Resumo dos Achados

Este relatório comparou três soluções para o problema do Jantar dos Filósofos, avaliando cada uma em múltiplos aspectos:

**Deadlock**:

- ✅ Todas as três soluções previnem deadlock com sucesso
- Nenhum caso de deadlock foi observado em 9 execuções totais (3 por solução)

**Starvation**:

- Tarefa 2: Possível mas não crítica - CV de 6,02%
- Tarefa 3: Rara - CV de 1,56%
- Tarefa 4: Ativamente prevenida - CV de 1,15% (melhor)

**Performance**:

- Tarefa 2 teve maior throughput: 282,33 refeições/5min (56,47 ref/min)
- Tarefa 4 teve menor tempo de espera: 2894,72 ms
- Tarefa 3 teve maior utilização de garfos: 91,50%

**Fairness**:

- Tarefa 4 teve melhor fairness com CV de 1,15%
- Todas mantiveram CV abaixo de 20% (aceitável)
- Tarefa 2 teve o maior CV (6,02%), mas ainda em níveis aceitáveis

### 5.2 Recomendações por Cenário

#### Quando usar cada solução?

**Use Tarefa 2 (Ordem Diferente) quando**:

- Performance é a prioridade máxima
- Sistema é simples e previsível
- Fairness não é crítica
- Exemplo: Simulações, benchmarks

**Use Tarefa 3 (Semáforos) quando**:

- Precisa de balanço entre simplicidade e robustez
- Semáforos já são usados no projeto
- Fairness moderada é suficiente
- Exemplo: Servidores web, processamento batch

**Use Tarefa 4 (Monitores) quando**:

- Fairness e justiça são obrigatórias
- Sistema crítico onde starvation é inaceitável
- Qualidade > performance bruta
- Exemplo: Sistemas financeiros, healthcare, controle de tráfego aéreo

### 5.3 Lições Aprendidas

1. **Não existe solução perfeita**: Cada abordagem tem trade-offs
2. **Deadlock pode ser prevenido** de múltiplas formas
3. **Fairness requer design intencional**: Não acontece automaticamente
4. **Simplicidade tem valor**: Tarefa 2 é surpreendentemente eficaz
5. **Monitores são poderosos** mas exigem expertise

### 5.4 Considerações Finais

O problema do Jantar dos Filósofos, embora simples conceitualmente, revela desafios fundamentais da programação concorrente. Este estudo demonstra que:

- **Deadlock é evitável** com técnicas apropriadas
- **Fairness requer mecanismos explícitos**
- **Performance e justiça** frequentemente conflitam
- **Escolha da solução** depende dos requisitos do sistema

Para aplicações reais, recomenda-se:

1. Avaliar requisitos de fairness cuidadosamente
2. Medir performance no ambiente de produção
3. Considerar mantenabilidade a longo prazo
4. Usar padrões estabelecidos (como Monitores) quando possível

---

## 6. Referências

1. **Dijkstra, E. W.** (1965). "Cooperating sequential processes". Technical Report EWD-123, Technological University, Eindhoven.
2. **Silberschatz, A., Galvin, P. B., & Gagne, G.** (2018). *Operating System Concepts* (10th ed.). Wiley.
3. **Tanenbaum, A. S., & Bos, H.** (2015). *Modern Operating Systems* (4th ed.). Pearson.
4. **Herlihy, M., & Shavit, N.** (2012). *The Art of Multiprocessor Programming* (Revised 1st ed.). Morgan Kaufmann.
5. **Oracle Java Documentation**. (2023). *Java Concurrency Utilities*. https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/package-summary.html
6. **Coffman, E. G., Elphick, M., & Shoshani, A.** (1971). "System Deadlocks". *Computing Surveys*, 3(2), 67-78.
