package tarefa1;

import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representa um filósofo no problema do Jantar dos Filósofos.
 * Cada filósofo é uma thread que alterna entre pensar e comer.
 * 
 * ⚠️ ESTA IMPLEMENTAÇÃO PODE CAUSAR DEADLOCK! ⚠️
 * 
 * EXPLICAÇÃO DO DEADLOCK:
 * ---------------------
 * Todos os filósofos seguem a mesma estratégia:
 * 1. Pegar garfo ESQUERDO primeiro
 * 2. Pegar garfo DIREITO depois
 * 3. Comer
 * 4. Soltar ambos os garfos
 * 
 * CENÁRIO DE DEADLOCK:
 * Se todos os 5 filósofos pegarem seu garfo esquerdo SIMULTANEAMENTE:
 *   - Filósofo 0 pega garfo 0
 *   - Filósofo 1 pega garfo 1
 *   - Filósofo 2 pega garfo 2
 *   - Filósofo 3 pega garfo 3
 *   - Filósofo 4 pega garfo 4
 * 
 * Agora todos tentam pegar o garfo direito, mas:
 *   - Filósofo 0 quer garfo 1 (mas está com Filósofo 1)
 *   - Filósofo 1 quer garfo 2 (mas está com Filósofo 2)
 *   - Filósofo 2 quer garfo 3 (mas está com Filósofo 3)
 *   - Filósofo 3 quer garfo 4 (mas está com Filósofo 4)
 *   - Filósofo 4 quer garfo 0 (mas está com Filósofo 0)
 * 
 * CICLO DE ESPERA: F0 → F1 → F2 → F3 → F4 → F0 (DEADLOCK!)
 * 
 * CONDIÇÕES DE COFFMAN SATISFEITAS:
 * 1. Exclusão Mútua: ✓ (synchronized garante que apenas 1 filósofo usa cada garfo)
 * 2. Posse e Espera: ✓ (filósofo segura 1 garfo enquanto espera o outro)
 * 3. Não Preempção: ✓ (garfos não podem ser forcadamente retirados)
 * 4. Espera Circular: ✓ (ciclo F0→F1→...→F4→F0)
 */
public class Filosofo implements Runnable {
    private final int id;
    private final Garfo garfoEsquerdo;
    private final Garfo garfoDireito;
    private final Random random;
    private final SimpleDateFormat timeFormat;
    private volatile boolean executando = true;

    public Filosofo(int id, Garfo garfoEsquerdo, Garfo garfoDireito) {
        this.id = id;
        this.garfoEsquerdo = garfoEsquerdo;
        this.garfoDireito = garfoDireito;
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
        // Tenta pegar o garfo esquerdo primeiro
        log("tentando pegar o garfo ESQUERDO " + garfoEsquerdo.getId());
        synchronized (garfoEsquerdo) {
            log("pegou o garfo ESQUERDO " + garfoEsquerdo.getId());
            
            // Tenta pegar o garfo direito
            log("tentando pegar o garfo DIREITO " + garfoDireito.getId());
            synchronized (garfoDireito) {
                log("pegou o garfo DIREITO " + garfoDireito.getId());
                
                // Comendo
                log("está COMENDO com garfos " + garfoEsquerdo.getId() + " e " + garfoDireito.getId());
                Thread.sleep(random.nextInt(2000) + 1000); // 1-3 segundos
                
                log("terminou de COMER e soltou os garfos " + garfoEsquerdo.getId() + " e " + garfoDireito.getId());
            }
        }
    }

    public void parar() {
        executando = false;
    }

    private void log(String mensagem) {
        String timestamp = timeFormat.format(new Date());
        System.out.println("[" + timestamp + "] Filósofo " + id + " " + mensagem);
    }
}
