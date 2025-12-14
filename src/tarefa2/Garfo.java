package tarefa2;

/**
 * Representa um garfo na mesa do Jantar dos Filósofos.
 * Cada garfo é um recurso compartilhado que deve ser adquirido exclusivamente.
 * 
 * Rastreia métricas de utilização para análise de performance.
 */
public class Garfo {
    private final int id;
    private long tempoTotalEmUso = 0; // Tempo total que o garfo foi usado (ms)
    private long ultimoPegado = 0; // Timestamp da última vez que foi pego
    private int vezesUsado = 0; // Número de vezes que foi usado

    public Garfo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public synchronized void registrarPego() {
        ultimoPegado = System.currentTimeMillis();
        vezesUsado++;
    }

    public synchronized void registrarSolto() {
        if (ultimoPegado > 0) {
            tempoTotalEmUso += (System.currentTimeMillis() - ultimoPegado);
            ultimoPegado = 0;
        }
    }

    public synchronized double getTaxaUtilizacao(long tempoTotal) {
        return tempoTotal > 0 ? (double) tempoTotalEmUso / tempoTotal * 100 : 0;
    }

    public synchronized int getVezesUsado() {
        return vezesUsado;
    }
}
