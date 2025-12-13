package tarefa1;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal para executar a Tarefa 1 - Implementação com Deadlock.
 * 
 * Esta implementação demonstra o problema de deadlock no Jantar dos Filósofos.
 * Todos os filósofos tentam pegar primeiro o garfo esquerdo e depois o direito,
 * o que pode levar a uma situação onde todos pegam o garfo esquerdo e ficam
 * esperando indefinidamente pelo garfo direito.
 * 
 * Execute por pelo menos 30 segundos para observar o deadlock.
 */
public class Main {
    private static final int NUM_FILOSOFOS = 5;
    private static final int TEMPO_EXECUCAO_MS = 30000; // 30 segundos

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("TAREFA 1: JANTAR DOS FILÓSOFOS - IMPLEMENTAÇÃO COM DEADLOCK");
        System.out.println("=".repeat(80));
        System.out.println("Iniciando simulação com " + NUM_FILOSOFOS + " filósofos...");
        System.out.println("Tempo de execução: " + (TEMPO_EXECUCAO_MS / 1000) + " segundos");
        System.out.println("ATENÇÃO: Esta implementação PODE causar deadlock!\n");
        System.out.println("=".repeat(80));

        // Criar os garfos
        List<Garfo> garfos = new ArrayList<>();
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos.add(new Garfo(i));
        }

        // Criar os filósofos
        List<Filosofo> filosofos = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Garfo garfoEsquerdo = garfos.get(i);
            Garfo garfoDireito = garfos.get((i + 1) % NUM_FILOSOFOS);
            
            Filosofo filosofo = new Filosofo(i, garfoEsquerdo, garfoDireito);
            filosofos.add(filosofo);
            
            Thread thread = new Thread(filosofo);
            threads.add(thread);
            thread.start();
        }

        // Executar por um tempo determinado
        try {
            Thread.sleep(TEMPO_EXECUCAO_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Parar todos os filósofos
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Tempo de execução encerrado. Parando filósofos...");
        for (Filosofo filosofo : filosofos) {
            filosofo.parar();
        }

        // Aguardar finalização das threads (com timeout)
        for (Thread thread : threads) {
            try {
                thread.join(2000); // Aguarda no máximo 2 segundos por thread
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Simulação encerrada.");
        System.out.println("=".repeat(80));
        
        // Verificar se há threads ainda ativas (indicativo de deadlock)
        long threadsAtivas = threads.stream().filter(Thread::isAlive).count();
        if (threadsAtivas > 0) {
            System.out.println("\n⚠️  DEADLOCK DETECTADO!");
            System.out.println("Ainda há " + threadsAtivas + " thread(s) ativa(s) após tentativa de finalização.");
            System.out.println("Isso indica que os filósofos estão presos esperando por recursos.");
        } else {
            System.out.println("\n✓ Nenhum deadlock foi detectado durante esta execução.");
            System.out.println("Execute novamente para potencialmente observar um deadlock.");
        }
        System.out.println("=".repeat(80));
    }
}
