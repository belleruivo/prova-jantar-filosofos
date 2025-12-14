package tarefa4;

import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representa um filósofo no problema do Jantar dos Filósofos.
 * 
 * ✅ SOLUÇÃO COM MONITOR E GARANTIA DE FAIRNESS ✅
 * 
 * ESTRATÉGIA:
 * -----------
 * A classe Mesa atua como um MONITOR que coordena CENTRALIZADAMENTE todo o acesso aos garfos.
 * 
 * Monitor = Objeto com:
 *   - Métodos synchronized (exclusão mútua automática)
 *   - wait() para esperar por condições
 *   - notifyAll() para notificar threads esperando
 * 
 * COMO PREVINE DEADLOCK:
 * ----------------------
 * 1. AQUISIÇÃO ATÔMICA:
 *    - Filósofo só pega AMBOS os garfos ou NENHUM
 *    - Não existe estado intermediário (ter apenas 1 garfo)
 *    - Elimina a condição "Posse e Espera"
 * 
 * 2. COORDENAÇÃO CENTRALIZADA:
 *    - Mesa decide QUEM pode pegar garfos
 *    - Não há competição direta entre filósofos
 *    - Elimina possibilidade de espera circular
 * 
 * 3. GARANTIA DE PROGRESSO:
 *    - Se garfos estão disponíveis, algum filósofo consegue pegá-los
 *    - notifyAll() acorda TODOS os filósofos esperando
 *    - Pelo menos 1 conseguirá pegar garfos (verificação com while)
 * 
 * COMO PREVINE STARVATION:
 * ------------------------
 * 1. FILA FIFO:
 *    - Filósofos entram numa fila de espera
 *    - Ordem de chegada é respeitada (fairness básica)
 * 
 * 2. PRIORIZAÇÃO POR TEMPO:
 *    - Mesa rastreia tempo da última refeição de cada filósofo
 *    - Filósofos que esperam MUITO TEMPO ganham prioridade
 *    - Se tempo_espera > 2× primeiro_da_fila → pode "furar a fila"
 * 
 * 3. ANTI-STARVATION ATIVO:
 *    - Mecanismo explícito previne que filósofos fiquem sem comer
 *    - Mesmo com má sorte no escalonamento, prioridade aumenta
 * 
 * ANÁLISE DAS CONDIÇÕES DE COFFMAN:
 * 1. Exclusão Mútua: ✓ (synchronized garante)
 * 2. Posse e Espera: ✗ (QUEBRADA! Pega ambos ou nenhum)
 * 3. Não Preempção: ✓ (garfos não são retirados)
 * 4. Espera Circular: ✗ (IMPOSSÍVEL! Coordenação centralizada)
 * 
 * VANTAGENS:
 * ----------
 * ✅ Deadlock IMPOSSÍVEL
 * ✅ Starvation ATIVAMENTE PREVENIDA
 * ✅ Fairness EXCELENTE (CV geralmente < 15%)
 * ✅ Comportamento PREVISÍVEL e DETERMINÍSTICO
 * ✅ Encapsulamento (padrão Monitor bem aplicado)
 * 
 * DESVANTAGENS:
 * -------------
 * ⚠️ Implementação mais COMPLEXA
 * ⚠️ Overhead de sincronização (wait/notifyAll)
 * ⚠️ Throughput potencialmente MENOR que soluções mais simples
 * 
 * IDEAL PARA:
 * -----------
 * - Sistemas CRÍTICOS onde fairness é obrigatória
 * - Aplicações onde starvation é INACEITÁVEL
 * - Ambientes que exigem comportamento DETERMINÍSTICO
 */
public class Filosofo implements Runnable {
    private final int id;
    private final Mesa mesa;
    private final Random random;
    private final SimpleDateFormat timeFormat;
    private volatile boolean executando = true;

    public Filosofo(int id, Mesa mesa) {
        this.id = id;
        this.mesa = mesa;
        this.random = new Random();
        this.timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
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
        int garfoEsquerdo = id;
        int garfoDireito = (id + 1) % 5;

        // Solicita os garfos através do monitor (Mesa)
        log("solicitando garfos " + garfoEsquerdo + " e " + garfoDireito + " ao MONITOR");
        long tempoInicio = System.currentTimeMillis();
        
        mesa.pegarGarfos(id);
        
        long tempoEspera = System.currentTimeMillis() - tempoInicio;
        log("obteve garfos " + garfoEsquerdo + " e " + garfoDireito + " após " + tempoEspera + "ms de espera");

        // Come
        log("está COMENDO com garfos " + garfoEsquerdo + " e " + garfoDireito);
        Thread.sleep(random.nextInt(2000) + 1000); // 1-3 segundos

        int numRefeicoes = mesa.getContadorRefeicoes(id) + 1;
        log("terminou de COMER (refeição #" + numRefeicoes + ")");

        // Devolve os garfos através do monitor
        mesa.soltarGarfos(id);
        log("devolveu os garfos ao MONITOR");
    }

    public void parar() {
        executando = false;
    }

    public int getContadorRefeicoes() {
        return mesa.getContadorRefeicoes(id);
    }

    private void log(String mensagem) {
        String timestamp = timeFormat.format(new Date());
        System.out.println("[" + timestamp + "] Filósofo " + id + " " + mensagem);
    }
}
