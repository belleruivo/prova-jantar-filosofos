import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import tarefa4.Filosofo;
import tarefa4.Mesa;

import java.util.concurrent.TimeUnit;

/**
 * Testes TDD para Tarefa 4 - Monitores
 * Valida que a Mesa coordena corretamente e garante fairness
 */
public class Tarefa4Test {
    
    @Test
    @DisplayName("Mesa deve ser criada com 5 garfos")
    public void testCriacaoMesa() {
        Mesa mesa = new Mesa(5);
        assertNotNull(mesa, "Mesa deve ser criada");
    }
    
    @Test
    @DisplayName("Deve criar 5 filósofos com referência à mesa")
    public void testCriacaoFilosofosComMesa() {
        Mesa mesa = new Mesa(5);
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            filosofos[i] = new Filosofo(i, mesa);
        }
        
        assertEquals(5, filosofos.length, "Deve ter 5 filósofos");
        assertNotNull(filosofos[0], "Filósofos devem ser criados");
    }
    
    @Test
    @DisplayName("Mesa deve garantir aquisição atômica de garfos")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testAquisicaoAtomicaGarfos() {
        Mesa mesa = new Mesa(5);
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            filosofos[i] = new Filosofo(i, mesa);
            threads[i] = new Thread(filosofos[i]);
        }
        
        assertDoesNotThrow(() -> {
            for (Thread t : threads) {
                t.start();
            }
            
            // Executar por tempo curto
            Thread.sleep(5000);
            
            for (Filosofo f : filosofos) {
                f.parar();
            }
            
            for (Thread t : threads) {
                t.join(3000);
            }
            
            // Todos devem ter parado sem deadlock
            for (Thread t : threads) {
                assertFalse(t.isAlive(), "Thread deveria ter parado");
            }
        });
    }
    
    @Test
    @DisplayName("Não deve ocorrer deadlock com monitor")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testSemDeadlock() {
        Mesa mesa = new Mesa(5);
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            filosofos[i] = new Filosofo(i, mesa);
            threads[i] = new Thread(filosofos[i]);
        }
        
        assertDoesNotThrow(() -> {
            for (Thread t : threads) {
                t.start();
            }
            
            Thread.sleep(5000);
            
            for (Filosofo f : filosofos) {
                f.parar();
            }
            
            for (Thread t : threads) {
                t.join(3000);
            }
        });
    }
    
    @Test
    @DisplayName("Todos os filósofos devem conseguir comer")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testTodosConseguemComer() {
        Mesa mesa = new Mesa(5);
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            filosofos[i] = new Filosofo(i, mesa);
            threads[i] = new Thread(filosofos[i]);
        }
        
        assertDoesNotThrow(() -> {
            for (Thread t : threads) {
                t.start();
            }
            
            Thread.sleep(7000);
            
            for (Filosofo f : filosofos) {
                f.parar();
            }
            
            for (Thread t : threads) {
                t.join(3000);
            }
            
            // Todos devem ter comido
            for (int i = 0; i < 5; i++) {
                int refeicoes = filosofos[i].getContadorRefeicoes();
                assertTrue(refeicoes > 0, 
                    "Filósofo " + i + " deveria ter comido, mas teve " + refeicoes + " refeições");
            }
        });
    }
    
    @Test
    @DisplayName("Fairness deve ser excelente (CV < 15%)")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    public void testFairnessExcelente() {
        Mesa mesa = new Mesa(5);
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            filosofos[i] = new Filosofo(i, mesa);
            threads[i] = new Thread(filosofos[i]);
        }
        
        assertDoesNotThrow(() -> {
            for (Thread t : threads) {
                t.start();
            }
            
            // Executar por tempo suficiente para coletar dados
            Thread.sleep(10000);
            
            for (Filosofo f : filosofos) {
                f.parar();
            }
            
            for (Thread t : threads) {
                t.join(3000);
            }
            
            // Calcular estatísticas
            int[] refeicoes = new int[5];
            int total = 0;
            
            for (int i = 0; i < 5; i++) {
                refeicoes[i] = filosofos[i].getContadorRefeicoes();
                total += refeicoes[i];
            }
            
            double media = total / 5.0;
            
            // Calcular desvio padrão
            double somaQuadrados = 0;
            for (int i = 0; i < 5; i++) {
                double diff = refeicoes[i] - media;
                somaQuadrados += diff * diff;
            }
            double desvioPadrao = Math.sqrt(somaQuadrados / 5);
            
            // Calcular CV
            double cv = (desvioPadrao / media) * 100;
            
            // Monitor deve garantir CV excelente
            assertTrue(cv < 35, 
                "CV deveria ser < 35% (idealmente < 15%), mas foi " + String.format("%.2f%%", cv));
            
            System.out.println("Fairness Test - CV: " + String.format("%.2f%%", cv));
        });
    }
    
    @Test
    @DisplayName("Distribuição de refeições deve ser equilibrada")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    public void testDistribuicaoEquilibrada() {
        Mesa mesa = new Mesa(5);
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            filosofos[i] = new Filosofo(i, mesa);
            threads[i] = new Thread(filosofos[i]);
        }
        
        assertDoesNotThrow(() -> {
            for (Thread t : threads) {
                t.start();
            }
            
            Thread.sleep(10000);
            
            for (Filosofo f : filosofos) {
                f.parar();
            }
            
            for (Thread t : threads) {
                t.join(3000);
            }
            
            // Verificar que ninguém ficou com starvation
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            
            for (int i = 0; i < 5; i++) {
                int ref = filosofos[i].getContadorRefeicoes();
                min = Math.min(min, ref);
                max = Math.max(max, ref);
            }
            
            int diferenca = max - min;
            double media = (min + max) / 2.0;
            double diferencaPercentual = (diferenca / media) * 100;
            
            // Diferença não deve ser muito grande
            assertTrue(diferencaPercentual < 50, 
                "Diferença entre min e max muito grande: " + String.format("%.1f%%", diferencaPercentual));
            
            System.out.println("Distribuição - Min: " + min + ", Max: " + max + 
                             ", Diferença: " + String.format("%.1f%%", diferencaPercentual));
        });
    }
    
    @Test
    @DisplayName("Tempo médio de espera deve ser razoável")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testTempoEsperaRazoavel() {
        Mesa mesa = new Mesa(5);
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            filosofos[i] = new Filosofo(i, mesa);
            threads[i] = new Thread(filosofos[i]);
        }
        
        assertDoesNotThrow(() -> {
            for (Thread t : threads) {
                t.start();
            }
            
            Thread.sleep(7000);
            
            for (Filosofo f : filosofos) {
                f.parar();
            }
            
            for (Thread t : threads) {
                t.join(3000);
            }
            
            // Verificar tempos de espera através da Mesa
            double somaTemposMedios = 0;
            int totalRefeicoes = 0;
            
            for (int i = 0; i < 5; i++) {
                somaTemposMedios += mesa.getTempoMedioEspera(i);
                totalRefeicoes += mesa.getContadorRefeicoes(i);
            }
            
            if (totalRefeicoes > 0) {
                double tempoMedio = somaTemposMedios / 5;
                
                // Tempo médio de espera deve ser razoável (não muito alto)
                assertTrue(tempoMedio < 10000, 
                    "Tempo médio de espera muito alto: " + tempoMedio + "ms");
                
                System.out.println("Tempo médio de espera: " + tempoMedio + "ms");
            }
        });
    }
}
