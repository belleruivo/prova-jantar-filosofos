package tarefa3;

import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Semaphore;

/**
 * Representa um filósofo no problema do Jantar dos Filósofos.
 * 
 * ✅ SOLUÇÃO COM SEMÁFOROS ✅
 * 
 * ESTRATÉGIA:
 * -----------
 * Usa um semáforo global que limita a MÁXIMO 4 FILÓSOFOS tentando pegar garfos simultaneamente.
 * 
 * Semáforo da Mesa: Semaphore(4)
 *   - Apenas 4 dos 5 filósofos podem estar "sentados à mesa" tentando pegar garfos
 *   - O 5º filósofo fica bloqueado esperando uma vaga
 * 
 * Semáforos dos Garfos: Semaphore(1) para cada garfo
 *   - Garante exclusão mútua (apenas 1 filósofo por garfo)
 * 
 * COMO PREVINE DEADLOCK:
 * ----------------------
 * IMPOSSÍVEL ter deadlock porque:
 * 
 * 1. PRINCÍPIO DO POMBAL:
 *    - 4 filósofos competindo por 5 garfos
 *    - Pelo menos 1 garfo SEMPRE estará livre
 *    - Logo, pelo menos 1 filósofo consegue pegar AMBOS os garfos adjacentes
 * 
 * 2. GARANTIA DE PROGRESSO:
 *    - Se 4 filósofos estão tentando, pelo menos 1 terá sucesso
 *    - Quando termina, libera garfos e vaga no semáforo
 *    - Outro filósofo pode entrar e tentar
 * 
 * EXEMPLO:
 *    Garfos: G0 G1 G2 G3 G4 (5 garfos)
 *    Na mesa: F0 F1 F2 F3 (4 filósofos)
 *    Esperando: F4 (bloqueado no semáforo)
 * 
 *    Pior caso: F0 pega G0, F1 pega G1, F2 pega G2, F3 pega G3
 *    Sobra: G4 (livre!)
 *    F3 pode pegar G4 (tem G3 e G4 → COME!)
 * 
 * ANÁLISE DAS CONDIÇÕES DE COFFMAN:
 * 1. Exclusão Mútua: ✓ (cada garfo é Semaphore(1))
 * 2. Posse e Espera: ✓ (filósofo pode segurar 1 garfo esperando outro)
 * 3. Não Preempção: ✓ (garfos não são retirados à força)
 * 4. Espera Circular: ✗ (IMPOSSÍVEL com apenas 4 competindo!)
 * 
 * LIMITAÇÕES:
 * -----------
 * - Fairness NÃO é garantida (mas geralmente melhor que Tarefa 2)
 * - Starvation é RARA mas ainda possível
 * - Overhead de semáforos (acquire/release)
 */
public class Filosofo implements Runnable {
    private final int id;
    private final Garfo garfoEsquerdo;
    private final Garfo garfoDireito;
    private final Random random;
    private final SimpleDateFormat timeFormat;
    private volatile boolean executando = true;
    private final AtomicInteger contadorRefeicoes;
    private final Semaphore semaforoMesa; // Limita filósofos na mesa
    private long totalTempoEspera = 0; // Tempo total esperando para comer (ms)
    private int tentativasTotal = 0; // Número de tentativas de comer

    public Filosofo(int id, Garfo garfoEsquerdo, Garfo garfoDireito, Semaphore semaforoMesa) {
        this.id = id;
        this.garfoEsquerdo = garfoEsquerdo;
        this.garfoDireito = garfoDireito;
        this.random = new Random();
        this.timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        this.contadorRefeicoes = new AtomicInteger(0);
        this.semaforoMesa = semaforoMesa;
    }

    @Override
    public void run() {
        try {
            while (executando) {
                pensar();
                comer();
            }
        } catch (InterruptedException e) {
            log("Foi interrompido");
            Thread.currentThread().interrupt();
        }
    }

    private void pensar() throws InterruptedException {
        log("começou a PENSAR");
        Thread.sleep(random.nextInt(2000) + 1000); // 1-3 segundos
    }

    private void comer() throws InterruptedException {
        long inicio = System.currentTimeMillis();
        tentativasTotal++;
        
        // Adquire permissão do semáforo da mesa (máximo 4 filósofos tentando pegar garfos)
        log("solicitando permissão para SENTAR À MESA (semáforo)");
        semaforoMesa.acquire();
        log("obteve permissão para SENTAR À MESA");

        try {
            // Tenta pegar os garfos
            log("tentando pegar o garfo ESQUERDO " + garfoEsquerdo.getId());
            garfoEsquerdo.pegar();
            log("pegou o garfo ESQUERDO " + garfoEsquerdo.getId());

            log("tentando pegar o garfo DIREITO " + garfoDireito.getId());
            garfoDireito.pegar();
            log("pegou o garfo DIREITO " + garfoDireito.getId());

            // Come
            log("está COMENDO com garfos " + garfoEsquerdo.getId() + " e " + garfoDireito.getId());
            Thread.sleep(random.nextInt(2000) + 1000); // 1-3 segundos

            contadorRefeicoes.incrementAndGet();
            log("terminou de COMER (refeição #" + contadorRefeicoes.get() + ")");

        } finally {
            // Solta os garfos
            garfoDireito.soltar();
            log("soltou o garfo DIREITO " + garfoDireito.getId());
            
            garfoEsquerdo.soltar();
            log("soltou o garfo ESQUERDO " + garfoEsquerdo.getId());

            // Libera a permissão do semáforo da mesa
            semaforoMesa.release();
            log("liberou permissão da MESA (semáforo)");
            
            long fim = System.currentTimeMillis();
            totalTempoEspera += (fim - inicio);
        }
    }

    public void parar() {
        executando = false;
    }

    public int getContadorRefeicoes() {
        return contadorRefeicoes.get();
    }

    public double getTempoMedioEspera() {
        return tentativasTotal > 0 ? (double) totalTempoEspera / tentativasTotal : 0;
    }

    private void log(String mensagem) {
        String timestamp = timeFormat.format(new Date());
        System.out.println("[" + timestamp + "] Filósofo " + id + " " + mensagem);
    }
}
