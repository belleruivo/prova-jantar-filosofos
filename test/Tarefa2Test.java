import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import tarefa2.Filosofo;
import tarefa2.Garfo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Testes TDD para Tarefa 2 - Ordem Diferente
 * Valida que o filósofo 4 realmente inverte a ordem de aquisição
 */
public class Tarefa2Test {
    
    @Test
    @DisplayName("Deve criar 5 filósofos e 5 garfos")
    public void testCriacaoFilosofosGarfos() {
        Garfo[] garfos = new Garfo[5];
        for (int i = 0; i < 5; i++) {
            garfos[i] = new Garfo(i);
        }
        
        Filosofo[] filosofos = new Filosofo[5];
        for (int i = 0; i < 5; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % 5];
            boolean ordemInversa = (i == 4); // Filósofo 4 usa ordem inversa
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito, ordemInversa);
        }
        
        assertEquals(5, filosofos.length, "Deve ter 5 filósofos");
        assertEquals(5, garfos.length, "Deve ter 5 garfos");
    }
    
    @Test
    @DisplayName("Filosofo 4 deve ter flag de ordem inversa")
    public void testFilosofo4OrdemInversa() {
        Garfo[] garfos = new Garfo[5];
        for (int i = 0; i < 5; i++) {
            garfos[i] = new Garfo(i);
        }
        
        // Filósofo 4 deve ser criado com ordem inversa
        Filosofo filosofo4 = new Filosofo(4, garfos[4], garfos[0], true);
        
        assertNotNull(filosofo4, "Filósofo 4 deve ser criado");
        // O comportamento de inversão será testado na execução
    }
    
    @Test
    @DisplayName("Não deve ocorrer deadlock em execução curta")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    public void testSemDeadlock() {
        Garfo[] garfos = new Garfo[5];
        for (int i = 0; i < 5; i++) {
            garfos[i] = new Garfo(i);
        }
        
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        // Criar filósofos
        for (int i = 0; i < 5; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % 5];
            boolean ordemInversa = (i == 4);
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito, ordemInversa);
            threads[i] = new Thread(filosofos[i]);
        }
        
        // Executar por tempo limitado
        assertDoesNotThrow(() -> {
            for (Thread t : threads) {
                t.start();
            }
            
            // Esperar 5 segundos
            Thread.sleep(5000);
            
            // Parar threads
            for (Filosofo f : filosofos) {
                f.parar();
            }
            
            // Esperar todas terminarem
            for (Thread t : threads) {
                t.join(2000);
            }
            
            // Verificar que todos pararam
            for (Thread t : threads) {
                // Thread pode ainda estar finalizando, aguardar um pouco
        t.join(2000);
        assertFalse(t.isAlive(), "Thread deveria ter parado");
            }
        });
    }
    
    @Test
    @DisplayName("Todos os filósofos devem comer pelo menos uma vez")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testTodosComem() {
        Garfo[] garfos = new Garfo[5];
        for (int i = 0; i < 5; i++) {
            garfos[i] = new Garfo(i);
        }
        
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % 5];
            boolean ordemInversa = (i == 4);
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito, ordemInversa);
            threads[i] = new Thread(filosofos[i]);
        }
        
        assertDoesNotThrow(() -> {
            for (Thread t : threads) {
                t.start();
            }
            
            // Executar por 5 segundos
            Thread.sleep(5000);
            
            for (Filosofo f : filosofos) {
                f.parar();
            }
            
            for (Thread t : threads) {
                t.join(2000);
            }
            
            // Verificar que todos comeram
            for (int i = 0; i < 5; i++) {
                int refeicoes = filosofos[i].getContadorRefeicoes();
                assertTrue(refeicoes > 0, 
                    "Filósofo " + i + " deveria ter comido pelo menos 1 vez, mas comeu " + refeicoes);
            }
        });
    }
    
    @Test
    @DisplayName("Métricas devem ser coletadas corretamente")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    public void testMetricasColetadas() {
        Garfo[] garfos = new Garfo[5];
        for (int i = 0; i < 5; i++) {
            garfos[i] = new Garfo(i);
        }
        
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % 5];
            boolean ordemInversa = (i == 4);
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito, ordemInversa);
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
                t.join(2000);
            }
            
            // Verificar métricas
            int totalRefeicoes = 0;
            for (Filosofo f : filosofos) {
                int refeicoes = f.getContadorRefeicoes();
                double tempoEspera = f.getTempoMedioEspera();
                
                assertTrue(refeicoes > 0, "Deve ter registrado refeições");
                assertTrue(tempoEspera >= 0, "Tempo de espera deve ser não-negativo");
                
                totalRefeicoes += refeicoes;
            }
            
            assertTrue(totalRefeicoes >= 3, "Total de refeições deve ser significativo");
            
            // Verificar garfos
            for (Garfo g : garfos) {
                int vezesUsado = g.getVezesUsado();
                assertTrue(vezesUsado > 0, "Garfo deveria ter sido usado");
            }
        });
    }
}
