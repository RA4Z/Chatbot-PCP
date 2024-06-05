package automatismos;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class AutomatismosPanel extends JPanel {

    public AutomatismosPanel() {
        try {
            // Envia o comando para a classe PythonExecutor
            List<Dados> resposta = PythonExecutor.executarScript("semanais");

            // Exibe a resposta do script Python
            for (Dados dados : resposta) {
                System.out.println("Nome: " + dados.getNome());
                System.out.println("Path: " + dados.getPath());
                System.out.println("PathProcedure: " + dados.getPathProcedure());
                System.out.println("LastUpdate: " + dados.getLastUpdate());
                System.out.println("Status: " + dados.getStatus());
                System.out.println("-------------------");
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao executar o script: " + e.getMessage());
        }
    }
}