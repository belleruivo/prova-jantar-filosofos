package tarefa1;

/**
 * Representa um garfo na mesa do Jantar dos Filósofos.
 * Cada garfo é um recurso compartilhado que deve ser adquirido exclusivamente.
 */
public class Garfo {
    private final int id;

    public Garfo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
