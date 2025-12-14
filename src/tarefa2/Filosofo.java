package tarefa2;

import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representa um filósofo no problema do Jantar dos Filósofos.
 * 
 * ✅ SOLUÇÃO COM PREVENÇÃO DE DEADLOCK: ORDEM DE AQUISIÇÃO DIFERENTE ✅
 * 
 * ESTRATÉGIA:
 * -----------
 * Um dos filósofos (ID 4) pega os garfos em ORDEM INVERSA:
 *   - Filósofos 0-3: pegam ESQUERDO → DIREITO
 *   - Filósofo 4: pega DIREITO → ESQUERDO
 * 
 * COMO PREVINE DEADLOCK:
 * ----------------------
 * Ao inverter a ordem de aquisição de UM filósofo, quebramos a ESPERA CIRCULAR.
 * 
 * Cenário que SERIA deadlock (mas NÃO É MAIS):
 *   - Filósofos 0-3 pegam seus garfos esquerdos
 *   - Filósofo 4 tenta pegar garfo DIREITO (garfo 0) primeiro
 *   - Como garfo 0 está com Filósofo 0, Filósofo 4 espera
 *   - Mas Filósofo 0 consegue pegar garfo 1 (não está com ninguém!)
 *   - Filósofo 0 come e libera garfos 0 e 1
 *   - Agora outros filósofos podem progredir
 * 
 * ANÁLISE DAS CONDIÇÕES DE COFFMAN:
 * 1. Exclusão Mútua: ✓ (ainda presente)
 * 2. Posse e Espera: ✓ (ainda presente)
 * 3. Não Preempção: ✓ (ainda presente)
 * 4. Espera Circular: ✗ (QUEBRADA! Filósofo 4 não participa do ciclo)
 * 
 * LIMITAÇÕES:
 * -----------
 * - Fairness NÃO é garantida (depende do escalonador de threads)
 * - Starvation ainda é POSSÍVEL (filósofo pode nunca conseguir ambos os garfos)
 * - Distribuição de refeições pode ser desigual
 */
public class Filosofo implements Runnable {
    private final int id;
    private final Garfo garfoEsquerdo;
    private final Garfo garfoDireito;
    private final Random random;
    private final SimpleDateFormat timeFormat;
    private volatile boolean executando = true;
    private final AtomicInteger contadorRefeicoes;
    private final boolean pegarOrdemInversa;
    private long totalTempoEspera = 0; // Tempo total esperando para comer (ms)
    private int tentativasTotal = 0; // Número de tentativas de comer

    public Filosofo(int id, Garfo garfoEsquerdo, Garfo garfoDireito, boolean pegarOrdemInversa) {
        this.id = id;
        this.garfoEsquerdo = garfoEsquerdo;
        this.garfoDireito = garfoDireito;
        this.random = new Random();
        this.timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        this.contadorRefeicoes = new AtomicInteger(0);
        this.pegarOrdemInversa = pegarOrdemInversa;
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
        
        if (pegarOrdemInversa) {
            // Filósofo especial: pega garfo DIREITO primeiro, depois ESQUERDO
            comerOrdemInversa();
        } else {
            // Filósofos normais: pegam garfo ESQUERDO primeiro, depois DIREITO
            comerOrdemNormal();
        }
        
        long fim = System.currentTimeMillis();
        totalTempoEspera += (fim - inicio);
        
        // Incrementa contador de refeições
        contadorRefeicoes.incrementAndGet();
    }

    private void comerOrdemNormal() throws InterruptedException {
        log("tentando pegar o garfo ESQUERDO " + garfoEsquerdo.getId());
        synchronized (garfoEsquerdo) {
            garfoEsquerdo.registrarPego();
            log("pegou o garfo ESQUERDO " + garfoEsquerdo.getId());
            
            log("tentando pegar o garfo DIREITO " + garfoDireito.getId());
            synchronized (garfoDireito) {
                garfoDireito.registrarPego();
                log("pegou o garfo DIREITO " + garfoDireito.getId());
                realizarRefeicao();
                garfoDireito.registrarSolto();
            }
            garfoEsquerdo.registrarSolto();
        }
    }

    private void comerOrdemInversa() throws InterruptedException {
        log("tentando pegar o garfo DIREITO " + garfoDireito.getId() + " [ORDEM INVERSA]");
        synchronized (garfoDireito) {
            garfoDireito.registrarPego();
            log("pegou o garfo DIREITO " + garfoDireito.getId() + " [ORDEM INVERSA]");
            
            log("tentando pegar o garfo ESQUERDO " + garfoEsquerdo.getId() + " [ORDEM INVERSA]");
            synchronized (garfoEsquerdo) {
                garfoEsquerdo.registrarPego();
                log("pegou o garfo ESQUERDO " + garfoEsquerdo.getId() + " [ORDEM INVERSA]");
                realizarRefeicao();
                garfoEsquerdo.registrarSolto();
            }
            garfoDireito.registrarSolto();
        }
    }

    private void realizarRefeicao() throws InterruptedException {
        log("está COMENDO com garfos " + garfoEsquerdo.getId() + " e " + garfoDireito.getId());
        Thread.sleep(random.nextInt(2000) + 1000); // 1-3 segundos
        log("terminou de COMER (refeição #" + contadorRefeicoes.get() + ") e soltou os garfos");
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
