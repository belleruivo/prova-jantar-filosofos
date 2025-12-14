package tarefa2;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal para executar a Tarefa 2 - Prevenção de Deadlock com Ordem Diferente.
 * 
 * Nesta implementação, o filósofo ID 4 pega os garfos em ordem inversa
 * (direito primeiro, depois esquerdo), quebrando o ciclo de espera circular
 * e prevenindo deadlock.
 * 
 * Execute por pelo menos 2 minutos para verificar que não ocorre deadlock.
 */
public class Main {
    private static final int NUM_FILOSOFOS = 5;
    private static final int TEMPO_EXECUCAO_MS = 300000; // 5 minutos (Tarefa 5)
    private static long tempoInicio; // Para calcular taxa de utilização dos garfos

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("TAREFA 2: JANTAR DOS FILÓSOFOS - PREVENÇÃO DE DEADLOCK (ORDEM DIFERENTE)");
        System.out.println("=".repeat(80));
        System.out.println("Iniciando simulação com " + NUM_FILOSOFOS + " filósofos...");
        System.out.println("Tempo de execução: " + (TEMPO_EXECUCAO_MS / 1000) + " segundos");
        System.out.println("Filósofo ID 4 pegará os garfos em ordem INVERSA para prevenir deadlock.\n");
        System.out.println("=".repeat(80));

        tempoInicio = System.currentTimeMillis();

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
            
            // Filósofo ID 4 pega garfos em ordem inversa
            boolean ordemInversa = (i == 4);
            
            Filosofo filosofo = new Filosofo(i, garfoEsquerdo, garfoDireito, ordemInversa);
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

        // Aguardar finalização das threads
        for (Thread thread : threads) {
            try {
                thread.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Simulação encerrada.");
        System.out.println("=".repeat(80));
        
        long tempoTotal = System.currentTimeMillis() - tempoInicio;
        
        // Exibir estatísticas
        exibirEstatisticas(filosofos, garfos, tempoTotal);
        
        // Verificar se há threads ainda ativas
        long threadsAtivas = threads.stream().filter(Thread::isAlive).count();
        if (threadsAtivas > 0) {
            System.out.println("\n⚠️  PROBLEMA DETECTADO!");
            System.out.println("Ainda há " + threadsAtivas + " thread(s) ativa(s).");
        } else {
            System.out.println("\n✓ Execução bem-sucedida! Nenhum deadlock ocorreu.");
        }
        System.out.println("=".repeat(80));
    }

    private static void exibirEstatisticas(List<Filosofo> filosofos, List<Garfo> garfos, long tempoTotal) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ESTATÍSTICAS DE EXECUÇÃO");
        System.out.println("=".repeat(80));
        
        int totalRefeicoes = 0;
        int minRefeicoes = Integer.MAX_VALUE;
        int maxRefeicoes = 0;
        double somaTempoEspera = 0;
        
        System.out.println("\n📊 REFEIÇÕES POR FILÓSOFO:");
        for (int i = 0; i < filosofos.size(); i++) {
            int refeicoes = filosofos.get(i).getContadorRefeicoes();
            double tempoMedioEspera = filosofos.get(i).getTempoMedioEspera();
            totalRefeicoes += refeicoes;
            somaTempoEspera += tempoMedioEspera;
            minRefeicoes = Math.min(minRefeicoes, refeicoes);
            maxRefeicoes = Math.max(maxRefeicoes, refeicoes);
            
            String marcador = (i == 4) ? " [ORDEM INVERSA]" : "";
            System.out.printf("  Filósofo %d: %d refeições | Tempo médio de espera: %.2f ms%s%n", 
                             i, refeicoes, tempoMedioEspera, marcador);
        }
        
        double media = (double) totalRefeicoes / filosofos.size();
        double desvioPadrao = calcularDesvioPadrao(filosofos, media);
        double coeficienteVariacao = (desvioPadrao / media) * 100;
        double tempoMedioEsperaGeral = somaTempoEspera / filosofos.size();
        
        System.out.println("\n📈 MÉTRICAS GERAIS:");
        System.out.println("-".repeat(80));
        System.out.printf("  Total de refeições: %d%n", totalRefeicoes);
        System.out.printf("  Média por filósofo: %.2f%n", media);
        System.out.printf("  Mínimo: %d | Máximo: %d | Diferença: %d%n", 
                         minRefeicoes, maxRefeicoes, maxRefeicoes - minRefeicoes);
        System.out.printf("  Desvio padrão: %.2f%n", desvioPadrao);
        System.out.printf("  Coeficiente de variação: %.2f%%%n", coeficienteVariacao);
        System.out.printf("  Tempo médio de espera geral: %.2f ms%n", tempoMedioEsperaGeral);
        
        System.out.println("\n🍴 TAXA DE UTILIZAÇÃO DOS GARFOS:");
        System.out.println("-".repeat(80));
        double somaUtilizacao = 0;
        for (Garfo garfo : garfos) {
            double taxa = garfo.getTaxaUtilizacao(tempoTotal);
            somaUtilizacao += taxa;
            System.out.printf("  Garfo %d: %.2f%% (usado %d vezes)%n", 
                             garfo.getId(), taxa, garfo.getVezesUsado());
        }
        double mediaUtilizacao = somaUtilizacao / garfos.size();
        System.out.printf("  Média de utilização: %.2f%%%n", mediaUtilizacao);
        
        System.out.println("\n🎯 ANÁLISE DE FAIRNESS:");
        System.out.println("-".repeat(80));
        if (coeficienteVariacao < 20) {
            System.out.println("  ✓ Distribuição JUSTA de refeições (CV < 20%)");
        } else if (coeficienteVariacao < 40) {
            System.out.println("  ⚠ Distribuição MODERADAMENTE justa (CV entre 20-40%)");
        } else {
            System.out.println("  ⚠ Distribuição DESIGUAL - possível starvation (CV > 40%)");
        }
    }

    private static double calcularDesvioPadrao(List<Filosofo> filosofos, double media) {
        double somaDiferencasQuadrado = 0;
        for (Filosofo filosofo : filosofos) {
            double diferenca = filosofo.getContadorRefeicoes() - media;
            somaDiferencasQuadrado += diferenca * diferenca;
        }
        return Math.sqrt(somaDiferencasQuadrado / filosofos.size());
    }
}
