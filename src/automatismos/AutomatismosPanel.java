package automatismos;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AutomatismosPanel extends JPanel {

    public AutomatismosPanel() {
        setLayout(new BorderLayout());

        // Cria os botões com estilização
        JButton botaoDiarios = createButton("Diários", new Color(0, 153, 255), () -> executarScript("diarios"));
        JButton botaoSemanais = createButton("Semanais", new Color(0, 102, 153), () -> executarScript("semanais"));
        JButton botaoMensais = createButton("Mensais", new Color(0, 51, 102), () -> executarScript("mensais"));

        // Cria um JPanel com GridLayout para os botões
        JPanel painelBotoes = new JPanel(new GridLayout(1, 0, 10, 10)); // 1 linha, colunas automáticas, espaçamento 10
        painelBotoes.add(botaoDiarios);
        painelBotoes.add(botaoSemanais);
        painelBotoes.add(botaoMensais);

        // Adiciona o painel de botões ao topo do JPanel principal
        add(painelBotoes, BorderLayout.NORTH);
    }

    // Método para executar o script Python
    private void executarScript(String argumento) {
        try {
            List<Dados> resposta = PythonExecutor.executarScript(argumento);

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

    // Método auxiliar para criar botões com estilização
    private JButton createButton(String text, Color backgroundColor, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.PLAIN, 30));
        button.setPreferredSize(new Dimension(400, 50));
        button.addActionListener(_ -> action.run());
        return button;
    }
}