package tarefa4;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Classe Mesa que atua como Monitor para coordenar o acesso aos garfos.
 * 
 * Esta implementação garante:
 * - Prevenção de deadlock
 * - Prevenção de starvation através de fila FIFO
 * - Fairness: todos os filósofos têm oportunidade justa de comer
 */
public class Mesa {
    private final int numFilosofos;
    private final boolean[] garfosDisponiveis;
    private final Queue<Integer> filaEspera;
    private final int[] contadorRefeicoes;
    private final long[] tempoUltimaRefeicao;
    private final long[] totalTempoEspera; // Tempo total de espera por filósofo
    private final int[] tentativasTotal; // Número de tentativas por filósofo
    private long tempoInicioSimulacao;
    private final long[] tempoTotalGarfoEmUso; // Tempo que cada garfo foi usado

    public Mesa(int numFilosofos) {
        this.numFilosofos = numFilosofos;
        this.garfosDisponiveis = new boolean[numFilosofos];
        this.filaEspera = new LinkedList<>();
        this.contadorRefeicoes = new int[numFilosofos];
        this.tempoUltimaRefeicao = new long[numFilosofos];
        this.totalTempoEspera = new long[numFilosofos];
        this.tentativasTotal = new int[numFilosofos];
        this.tempoTotalGarfoEmUso = new long[numFilosofos];
        this.tempoInicioSimulacao = System.currentTimeMillis();
        
        // Inicialmente todos os garfos estão disponíveis
        for (int i = 0; i < numFilosofos; i++) {
            garfosDisponiveis[i] = true;
            tempoUltimaRefeicao[i] = System.currentTimeMillis();
        }
    }

    /**
     * Tenta pegar os garfos para um filósofo.
     * Usa fila de espera para garantir fairness.
     * Prioriza filósofos que comeram há mais tempo.
     */
    public synchronized void pegarGarfos(int idFilosofo) throws InterruptedException {
        long inicio = System.currentTimeMillis();
        tentativasTotal[idFilosofo]++;
        
        int garfoEsquerdo = idFilosofo;
        int garfoDireito = (idFilosofo + 1) % numFilosofos;

        // Adiciona à fila de espera
        filaEspera.add(idFilosofo);

        // Aguarda até que:
        // 1. Este filósofo seja o primeiro da fila (fairness)
        // 2. Ambos os garfos estejam disponíveis
        while (!podepegarGarfos(idFilosofo, garfoEsquerdo, garfoDireito)) {
            wait();
        }

        // Remove da fila e pega os garfos
        filaEspera.remove(idFilosofo);
        garfosDisponiveis[garfoEsquerdo] = false;
        garfosDisponiveis[garfoDireito] = false;
        
        // Registra tempo de espera
        long fim = System.currentTimeMillis();
        totalTempoEspera[idFilosofo] += (fim - inicio);
    }

    /**
     * Verifica se um filósofo pode pegar os garfos.
     * Considera:
     * - Se é o primeiro da fila (fairness)
     * - Se ambos os garfos estão disponíveis
     * - Prioridade para quem está há mais tempo sem comer
     */
    private boolean podepegarGarfos(int idFilosofo, int garfoEsquerdo, int garfoDireito) {
        // Verifica se os garfos estão disponíveis
        if (!garfosDisponiveis[garfoEsquerdo] || !garfosDisponiveis[garfoDireito]) {
            return false;
        }

        // Política de fairness: respeita ordem da fila
        // MAS dá prioridade a filósofos que estão há muito tempo sem comer
        Integer primeiro = filaEspera.peek();
        if (primeiro == null) {
            return false;
        }

        // Se é o primeiro da fila, pode pegar
        if (primeiro == idFilosofo) {
            return true;
        }

        // Verifica se há algum filósofo com prioridade maior (starvation prevention)
        long tempoEsperaAtual = System.currentTimeMillis() - tempoUltimaRefeicao[idFilosofo];
        long tempoEsperaPrimeiro = System.currentTimeMillis() - tempoUltimaRefeicao[primeiro];
        
        // Se este filósofo está esperando 2x mais que o primeiro, dá prioridade
        // Isso previne starvation extrema
        if (tempoEsperaAtual > tempoEsperaPrimeiro * 2) {
            return true;
        }

        return false;
    }

    /**
     * Solta os garfos após comer e notifica outros filósofos.
     */
    public synchronized void soltarGarfos(int idFilosofo) {
        int garfoEsquerdo = idFilosofo;
        int garfoDireito = (idFilosofo + 1) % numFilosofos;

        // Calcula tempo que os garfos foram usados (aproximação baseada na refeição)
        long tempoDesdeUltimaRefeicao = System.currentTimeMillis() - tempoUltimaRefeicao[idFilosofo];
        if (contadorRefeicoes[idFilosofo] > 0) { // Não conta a primeira vez
            tempoTotalGarfoEmUso[garfoEsquerdo] += tempoDesdeUltimaRefeicao;
            tempoTotalGarfoEmUso[garfoDireito] += tempoDesdeUltimaRefeicao;
        }

        // Libera os garfos
        garfosDisponiveis[garfoEsquerdo] = true;
        garfosDisponiveis[garfoDireito] = true;

        // Atualiza estatísticas
        contadorRefeicoes[idFilosofo]++;
        tempoUltimaRefeicao[idFilosofo] = System.currentTimeMillis();

        // Notifica TODOS os filósofos esperando
        notifyAll();
    }

    /**
     * Retorna o número de refeições de um filósofo.
     */
    public synchronized int getContadorRefeicoes(int idFilosofo) {
        return contadorRefeicoes[idFilosofo];
    }

    public synchronized double getTempoMedioEspera(int idFilosofo) {
        return tentativasTotal[idFilosofo] > 0 ? 
               (double) totalTempoEspera[idFilosofo] / tentativasTotal[idFilosofo] : 0;
    }

    public synchronized double getTaxaUtilizacaoGarfo(int idGarfo) {
        long tempoTotal = System.currentTimeMillis() - tempoInicioSimulacao;
        return tempoTotal > 0 ? (double) tempoTotalGarfoEmUso[idGarfo] / tempoTotal * 100 : 0;
    }

    /**
     * Retorna informações de estado da mesa (para debugging).
     */
    public synchronized String getEstadoMesa() {
        StringBuilder sb = new StringBuilder();
        sb.append("Garfos disponíveis: ");
        for (int i = 0; i < numFilosofos; i++) {
            sb.append(garfosDisponiveis[i] ? "✓" : "✗");
            if (i < numFilosofos - 1) sb.append(" ");
        }
        sb.append(" | Fila: ").append(filaEspera.size()).append(" filósofo(s)");
        return sb.toString();
    }
}
