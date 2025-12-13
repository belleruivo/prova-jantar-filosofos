# Arquitetura do Projeto - Jantar dos Filósofos

## Visão Geral

Este projeto implementa diferentes soluções para o clássico problema de sincronização conhecido como "Jantar dos Filósofos", proposto por Dijkstra.

---

## Estrutura do Código

### Tarefa 1 - Implementação com Deadlock

**Classes**:
- `Filosofo.java`: Thread que representa um filósofo
- `Garfo.java`: Recurso compartilhado (monitor)
- `Main.java`: Inicialização do sistema

**Implementação**:
- Todos os filósofos pegam garfo esquerdo primeiro, depois direito
- Uso de `synchronized` para exclusão mútua
- **Resultado**: Demonstra deadlock por espera circular

---

### Tarefa 2 - Solução com Ordem Diferente

**Classes**:
- `Filosofo.java`: Lógica com detecção de ID para ordem inversa
- `Garfo.java`: Recurso compartilhado
- `Main.java`: Coleta de métricas (refeições, tempo de espera, CV)

**Implementação**:
- Filósofos 0-3: `synchronized(garfoEsquerdo)` → `synchronized(garfoDireito)`
- Filósofo 4: `synchronized(garfoDireito)` → `synchronized(garfoEsquerdo)`
- Métricas: `AtomicInteger`, timestamps, cálculo de CV

---

### Tarefa 3 - Solução com Semáforos

**Classes**:
- `Filosofo.java`: Lógica com acquire/release de semáforos
- `Garfo.java`: Encapsula `Semaphore(1)` para cada garfo
- `Main.java`: Coleta de métricas avançadas

**Implementação**:
- Semáforo global: `Semaphore(4)` - limita filósofos na mesa
- Semáforos dos garfos: `Semaphore(1)` - exclusão mútua por garfo
- Ordem: `semaforoMesa.acquire()` → garfos → comer → release

---

### Tarefa 4 - Solução com Monitores

**Classes**:
- `Filosofo.java`: Coordena via Mesa (sem acesso direto a garfos)
- `Mesa.java`: Monitor centralizado com fila FIFO e priorização
- `Main.java`: Estatísticas completas

**Implementação**:
- `Mesa` controla estado de cada filósofo: `PENSANDO`, `FAMINTO`, `COMENDO`
- Método `pegarGarfos()`: `synchronized` com `wait()` até ambos disponíveis
- Método `soltarGarfos()`: `synchronized` com `notifyAll()`
- Priorização: filósofos esperando 2× média ganham prioridade

---

## Conceitos de Concorrência Aplicados

### 1. Exclusão Mútua
- **Objetivo**: Garantir que apenas um filósofo use cada garfo por vez
- **Implementação**: 
  - Tarefas 1-2: `synchronized`
  - Tarefa 3: `Semaphore(1)`
  - Tarefa 4: Monitor centralizado

### 2. Sincronização
- **Objetivo**: Coordenar acesso entre threads
- **Mecanismos**:
  - Locks implícitos (`synchronized`)
  - Semáforos (`java.util.concurrent.Semaphore`)
  - Monitores (`wait()`, `notifyAll()`)

### 3. Deadlock - Condições Necessárias (Coffman)

Para deadlock ocorrer, todas as 4 condições devem estar presentes:

1. **Exclusão mútua**: Recurso não pode ser compartilhado
2. **Posse e espera**: Processo segura recurso enquanto espera outro
3. **Não preempção**: Recurso não pode ser retirado à força
4. **Espera circular**: Ciclo de processos esperando uns pelos outros

### 4. Estratégias de Prevenção

Cada solução quebra uma condição diferente:

- **Tarefa 2**: Quebra **espera circular** (filósofo 4 inverte ordem)
- **Tarefa 3**: Previne **posse e espera** (limita filósofos simultâneos)
- **Tarefa 4**: Quebra **posse e espera** (aquisição atômica de ambos os garfos)

---

## Referências

- Dijkstra, E. W. (1965). "Cooperating sequential processes"
- Tanenbaum, A. S. "Modern Operating Systems"
- Silberschatz, A. "Operating System Concepts"
