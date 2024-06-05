package automatismos;

public class Dados {
    private String nome;
    private String path;
    private String pathProcedure;
    private String lastUpdate;
    private String status;

    public Dados() {
        // Construtor vazio
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathProcedure() {
        return pathProcedure;
    }

    public void setPathProcedure(String pathProcedure) {
        this.pathProcedure = pathProcedure;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
