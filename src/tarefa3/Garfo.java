package tarefa3;

import java.util.concurrent.Semaphore;

/**
 * Representa um garfo na mesa do Jantar dos Filósofos usando Semáforo.
 * O semáforo garante exclusão mútua (apenas 1 permit disponível).
 * 
 * Rastreia métricas de utilização para análise de performance.
 */
public class Garfo {
    private final int id;
    private final Semaphore semaforo;
    private long tempoTotalEmUso = 0;
    private long ultimoPegado = 0;
    private int vezesUsado = 0;

    public Garfo(int id) {
        this.id = id;
        this.semaforo = new Semaphore(1); // Apenas 1 filósofo pode usar o garfo por vez
    }

    public int getId() {
        return id;
    }

    public void pegar() throws InterruptedException {
        semaforo.acquire();
        synchronized(this) {
            ultimoPegado = System.currentTimeMillis();
            vezesUsado++;
        }
    }

    public void soltar() {
        synchronized(this) {
            if (ultimoPegado > 0) {
                tempoTotalEmUso += (System.currentTimeMillis() - ultimoPegado);
                ultimoPegado = 0;
            }
        }
        semaforo.release();
    }

    public synchronized double getTaxaUtilizacao(long tempoTotal) {
        return tempoTotal > 0 ? (double) tempoTotalEmUso / tempoTotal * 100 : 0;
    }

    public synchronized int getVezesUsado() {
        return vezesUsado;
    }
}
