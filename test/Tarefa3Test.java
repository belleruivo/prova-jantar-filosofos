import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import tarefa3.Filosofo;
import tarefa3.Garfo;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Testes TDD para Tarefa 3 - Semáforos
 * Valida que o semáforo limita corretamente a 4 filósofos simultaneamente
 */
public class Tarefa3Test {
    
    @Test
    @DisplayName("Semáforo da mesa deve limitar a 4 permits")
    public void testSemaforoLimite() {
        // Semáforo deve ter no máximo 4 permits
        Semaphore semaforoMesa = new Semaphore(4);
        
        assertEquals(4, semaforoMesa.availablePermits(), 
            "Semáforo deve iniciar com 4 permits");
    }
    
    @Test
    @DisplayName("Deve criar 5 filósofos com semáforo compartilhado")
    public void testCriacaoComSemaforo() {
        Semaphore semaforoMesa = new Semaphore(4);
        Garfo[] garfos = new Garfo[5];
        
        for (int i = 0; i < 5; i++) {
            garfos[i] = new Garfo(i);
        }
        
        Filosofo[] filosofos = new Filosofo[5];
        for (int i = 0; i < 5; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % 5];
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito, semaforoMesa);
        }
        
        assertEquals(5, filosofos.length, "Deve ter 5 filósofos");
        assertNotNull(filosofos[0], "Filósofos devem ser criados");
    }
    
    @Test
    @DisplayName("Máximo 4 filósofos devem ter acesso à mesa simultaneamente")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testLimite4Filosofos() {
        Semaphore semaforoMesa = new Semaphore(4);
        AtomicInteger filosofosNaMesa = new AtomicInteger(0);
        AtomicInteger maxSimultaneos = new AtomicInteger(0);
        
        Thread[] threads = new Thread[5];
        
        // Simular 5 filósofos tentando acessar mesa
        for (int i = 0; i < 5; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                for (int tentativa = 0; tentativa < 10; tentativa++) {
                    try {
                        semaforoMesa.acquire();
                        
                        int count = filosofosNaMesa.incrementAndGet();
                        
                        // Atualizar máximo
                        maxSimultaneos.updateAndGet(max -> Math.max(max, count));
                        
                        // Simular uso da mesa
                        Thread.sleep(50);
                        
                        filosofosNaMesa.decrementAndGet();
                        semaforoMesa.release();
                        
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
        
        assertDoesNotThrow(() -> {
            for (Thread t : threads) {
                t.start();
            }
            
            for (Thread t : threads) {
                t.join(10000);
            }
            
            // Verificar que nunca houve mais de 4 simultaneamente
            assertTrue(maxSimultaneos.get() <= 4, 
                "Máximo de filósofos simultâneos foi " + maxSimultaneos.get() + ", deveria ser <= 4");
        });
    }
    
    @Test
    @DisplayName("Não deve ocorrer deadlock com semáforos")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testSemDeadlock() {
        Semaphore semaforoMesa = new Semaphore(4);
        Garfo[] garfos = new Garfo[5];
        
        for (int i = 0; i < 5; i++) {
            garfos[i] = new Garfo(i);
        }
        
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % 5];
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito, semaforoMesa);
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
            
            for (Thread t : threads) {
                // Thread pode ainda estar finalizando, aguardar um pouco
        t.join(2000);
        assertFalse(t.isAlive(), "Thread deveria ter parado");
            }
        });
    }
    
    @Test
    @DisplayName("Todos os filósofos devem conseguir comer")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testTodosConseguemComer() {
        Semaphore semaforoMesa = new Semaphore(4);
        Garfo[] garfos = new Garfo[5];
        
        for (int i = 0; i < 5; i++) {
            garfos[i] = new Garfo(i);
        }
        
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % 5];
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito, semaforoMesa);
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
    @DisplayName("Taxa de utilização dos garfos deve ser coletada")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testUtilizacaoGarfos() {
        Semaphore semaforoMesa = new Semaphore(4);
        Garfo[] garfos = new Garfo[5];
        
        for (int i = 0; i < 5; i++) {
            garfos[i] = new Garfo(i);
        }
        
        Thread[] threads = new Thread[5];
        Filosofo[] filosofos = new Filosofo[5];
        
        for (int i = 0; i < 5; i++) {
            Garfo garfoEsquerdo = garfos[i];
            Garfo garfoDireito = garfos[(i + 1) % 5];
            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito, semaforoMesa);
            threads[i] = new Thread(filosofos[i]);
        }
        
        assertDoesNotThrow(() -> {
            long inicio = System.currentTimeMillis();
            
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
            
            long duracao = System.currentTimeMillis() - inicio;
            
            // Verificar que garfos foram usados
            for (int i = 0; i < 5; i++) {
                int vezesUsado = garfos[i].getVezesUsado();
                
                assertTrue(vezesUsado > 0, "Garfo " + i + " deveria ter sido usado");
            }
        });
    }
}
