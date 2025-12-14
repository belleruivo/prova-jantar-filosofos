package tarefa4;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal para executar a Tarefa 4 - Solução com Monitores e Fairness.
 * 
 * Esta implementação usa a classe Mesa como monitor para coordenar
 * centralizadamente o acesso aos garfos, garantindo fairness e prevenindo
 * tanto deadlock quanto starvation.
 * 
 * Execute por pelo menos 2 minutos para coletar estatísticas.
 */
public class Main {
    private static final int NUM_FILOSOFOS = 5;
    private static final int TEMPO_EXECUCAO_MS = 300000; // 5 minutos (Tarefa 5)

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("TAREFA 4: JANTAR DOS FILÓSOFOS - MONITORES COM FAIRNESS");
        System.out.println("=".repeat(80));
        System.out.println("Iniciando simulação com " + NUM_FILOSOFOS + " filósofos...");
        System.out.println("Monitor (Mesa) coordena acesso aos garfos com garantia de fairness");
        System.out.println("Tempo de execução: " + (TEMPO_EXECUCAO_MS / 1000) + " segundos\n");
        System.out.println("=".repeat(80));

        // Criar a mesa (monitor)
        Mesa mesa = new Mesa(NUM_FILOSOFOS);

        // Criar os filósofos
        List<Filosofo> filosofos = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Filosofo filosofo = new Filosofo(i, mesa);
            filosofos.add(filosofo);
            
            Thread thread = new Thread(filosofo);
            threads.add(thread);
            thread.start();
        }

        // Thread para monitorar estado da mesa periodicamente
        Thread monitorThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(10000); // A cada 10 segundos
                    System.out.println("\n[MONITOR] " + mesa.getEstadoMesa() + "\n");
                }
            } catch (InterruptedException e) {
                // Thread de monitoramento encerrada
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();

        // Executar por um tempo determinado
        try {
            Thread.sleep(TEMPO_EXECUCAO_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Parar todos os filósofos
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Tempo de execução encerrado. Parando filósofos...");
        monitorThread.interrupt();
        
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
        
        // Exibir estatísticas
        exibirEstatisticas(filosofos, mesa);
        
        // Verificar se há threads ainda ativas
        long threadsAtivas = threads.stream().filter(Thread::isAlive).count();
        if (threadsAtivas > 0) {
            System.out.println("\n⚠️  PROBLEMA DETECTADO!");
            System.out.println("Ainda há " + threadsAtivas + " thread(s) ativa(s).");
        } else {
            System.out.println("\n✓ Execução bem-sucedida!");
            System.out.println("✓ Monitor garantiu fairness e preveniu deadlock/starvation.");
        }
        System.out.println("=".repeat(80));
    }

    private static void exibirEstatisticas(List<Filosofo> filosofos, Mesa mesa) {
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
            double tempoMedioEspera = mesa.getTempoMedioEspera(i);
            totalRefeicoes += refeicoes;
            somaTempoEspera += tempoMedioEspera;
            minRefeicoes = Math.min(minRefeicoes, refeicoes);
            maxRefeicoes = Math.max(maxRefeicoes, refeicoes);
            
            System.out.printf("  Filósofo %d: %d refeições | Tempo médio de espera: %.2f ms%n", 
                             i, refeicoes, tempoMedioEspera);
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
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            double taxa = mesa.getTaxaUtilizacaoGarfo(i);
            somaUtilizacao += taxa;
            System.out.printf("  Garfo %d: %.2f%%%n", i, taxa);
        }
        double mediaUtilizacao = somaUtilizacao / NUM_FILOSOFOS;
        System.out.printf("  Média de utilização: %.2f%%%n", mediaUtilizacao);
        
        // Análise de fairness
        System.out.println("\n🎯 ANÁLISE DE FAIRNESS:");
        System.out.println("-".repeat(80));
        
        if (coeficienteVariacao < 15) {
            System.out.println("  ✓✓✓ EXCELENTE fairness! (CV < 15%)");
            System.out.println("      O monitor garantiu distribuição muito justa das refeições.");
        } else if (coeficienteVariacao < 25) {
            System.out.println("  ✓✓ BOA fairness (CV entre 15-25%)");
            System.out.println("     Distribuição justa com pequenas variações aceitáveis.");
        } else if (coeficienteVariacao < 40) {
            System.out.println("  ✓ Fairness MODERADA (CV entre 25-40%)");
            System.out.println("    Alguma desigualdade, mas sem starvation crítica.");
        } else {
            System.out.println("  ⚠ Fairness BAIXA (CV > 40%)");
            System.out.println("    Distribuição desigual detectada.");
        }
        
        // Análise de uniformidade
        double diferencaPercentual = ((double)(maxRefeicoes - minRefeicoes) / media) * 100;
        System.out.printf("\n  Diferença máxima: %.1f%% da média%n", diferencaPercentual);
        
        if (diferencaPercentual < 20) {
            System.out.println("  ✓ Todos os filósofos tiveram oportunidades similares de comer.");
        } else if (diferencaPercentual < 40) {
            System.out.println("  ⚠ Houve alguma desigualdade nas oportunidades.");
        } else {
            System.out.println("  ⚠ Desigualdade significativa detectada.");
        }
        
        System.out.println("\n  Estado final da mesa: " + mesa.getEstadoMesa());
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
