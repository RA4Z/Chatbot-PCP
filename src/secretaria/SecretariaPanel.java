package secretaria;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class SecretariaPanel extends JPanel {

    private final JPanel painelResposta; // Declaração do painel de resposta fora do método

    public SecretariaPanel() {
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

        // Cria o JPanel para os botões de resposta
        painelResposta = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    }

    // Método para executar o script Python
    private void executarScript(String argumento) {
        try {
            List<Dados> resposta = PythonExecutor.executarScript(argumento);

            // Limpa os botões de resposta anteriores
            painelResposta.removeAll();

            // Adiciona os novos botões de resposta ao painel
            for (Dados dados : resposta) {
                JButton botaoResposta = createButton(dados.getNome(),
                        dados.getStatus().equals("Pendente") ? new Color(0xF9EC9B) : new Color(0xBDECB6),  () -> {
                    // Exemplo: exibir informações do objeto Dados em um diálogo
                    JOptionPane.showMessageDialog(this, "Nome: " + dados.getNome() + "\n" +
                            "Path: " + dados.getPath() + "\n" +
                            "PathProcedure: " + dados.getPathProcedure() + "\n" +
                            "LastUpdate: " + dados.getLastUpdate() + "\n" +
                            "Status: " + dados.getStatus());
                });
                botaoResposta.setPreferredSize(new Dimension(250, 50));
                botaoResposta.setForeground(Color.BLACK);
                painelResposta.add(botaoResposta);
            }

            // Adiciona o painel de resposta ao centro do JPanel principal
            add(painelResposta, BorderLayout.CENTER);
            validate(); // Força a atualização do layout
            repaint(); // Redesenha o painel

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
        button.setFont(new Font("Arial", Font.PLAIN, 16)); // Fonte menor para os botões de resposta
        button.setPreferredSize(new Dimension(400, 75)); // Tamanho menor para os botões de resposta
        button.addActionListener(_ -> action.run());
        return button;
    }
}