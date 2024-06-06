package secretaria;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SecretariaPanel extends JPanel {

    private final JTextPane chatArea;
    private static final Color CHAT_BG_COLOR = new Color(0xE8E7E7);
    private final JPanel painelResposta;
    private List<String> topics = new ArrayList<>();
    private JPanel painelTopicos; // Painel para os checkboxes dos tópicos
    private JPanel verticalBody = new JPanel(new BorderLayout());
    private JScrollPane scrollPane;

    public SecretariaPanel() {
        setLayout(new BorderLayout());
        this.painelResposta = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        this.painelResposta.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Cria os botões com estilização
        JButton botaoDiarios = createButton("Diários", new Color(0, 153, 255), () -> executarScript("diarios"));
        JButton botaoSemanais = createButton("Semanais", new Color(0, 102, 153), () -> executarScript("semanais"));
        JButton botaoMensais = createButton("Mensais", new Color(0, 51, 102), () -> executarScript("mensais"));

        // Cria um JPanel com GridLayout para os botões
        JPanel painelBotoes = new JPanel(new GridLayout(1, 0, 10, 10));
        painelBotoes.add(botaoDiarios);
        painelBotoes.add(botaoSemanais);
        painelBotoes.add(botaoMensais);

        add(painelBotoes,BorderLayout.NORTH);
        add(verticalBody);
        verticalBody.add(painelResposta);

        // Cria o painel de chat com JScrollPane para scroll
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(CHAT_BG_COLOR);
        chatArea.setForeground(Color.WHITE);
        chatArea.setBorder(createChatAreaBorder());

        scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        verticalBody.add(scrollPane,BorderLayout.SOUTH);
    }

    private CompoundBorder createChatAreaBorder() {
        EmptyBorder paddingBorder = new EmptyBorder(15, 15, 15, 15);
        LineBorder blackBorder = new LineBorder(CHAT_BG_COLOR);
        return new CompoundBorder(paddingBorder, blackBorder);
    }

    // Método para executar o script Python
    private void executarScript(String argumento) {
        verticalBody.remove(scrollPane);
        verticalBody.add(scrollPane,BorderLayout.SOUTH);
        try {
            List<Dados> resposta = PythonExecutor.executarScript(argumento); // Assuma que PythonExecutor está definido
            atualizarPainelResposta(resposta);
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao executar o script: " + e.getMessage());
        }
    }

    // Método para atualizar o painel de resposta com os novos botões
    private void atualizarPainelResposta(List<Dados> resposta) {
        painelResposta.removeAll();

        for (Dados dados : resposta) {
            JButton botaoResposta = createButton(dados.getNome(),
                    dados.getStatus().equals("Pendente") ? new Color(0xF9EC9B) : new Color(0xBDECB6),
                    () -> exibirInformacoes(dados));
            botaoResposta.setPreferredSize(new Dimension(250, 50));
            botaoResposta.setForeground(Color.BLACK);
            painelResposta.add(botaoResposta);
        }

        validate();
        repaint();
    }

    // Método para exibir as informações do objeto Dados em um diálogo
    private void exibirInformacoes(Dados dados) {
        // Cria uma nova thread para executar get_procedure
        new Thread(() -> get_procedure(dados.getPathProcedure(), dados.getNome())).start();
    }

    // Método para obter a lista de tópicos
    private void get_procedure(String path, String filename) {
        try {
            URL url = new URI("http://10.1.43.63:5000/secretary").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Enviar a mensagem como parâmetro
            String data = "path=" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "&filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8);
            OutputStream output = connection.getOutputStream();
            output.write(data.getBytes(StandardCharsets.UTF_8));
            output.flush();
            output.close();

            // Ler a resposta do servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;

            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line.replace("<topico>", "")).append(" <br>");
            }
            reader.close();

            // Atualiza a área de chat na thread de execução (Swing não é thread-safe)
            SwingUtilities.invokeLater(() -> updateChatArea(response.toString()));

            // Limpa o painel de resposta
            SwingUtilities.invokeLater(() -> {
                painelResposta.removeAll();
                verticalBody.remove(scrollPane);
                verticalBody.add(scrollPane);
                validate();
                repaint();
            });

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatText(String text) {
        // Formata o texto com negrito
        text = Pattern.compile("\\*\\*(.*?)\\*\\*").matcher(text).replaceAll("<strong>$1</strong>");

        // Formata os links com atributos HTML
        text = Pattern.compile("(https?://\\S+)").matcher(text)
                .replaceAll("<a href=\"$1\" style=\"color:#3B8CED;cursor:pointer\" target=\"_blank\">$1</a>");

        return text;
    }

    private void updateChatArea(String textoRecebido) {
        try {
            chatArea.setContentType("text/html");
            chatArea.setText(formatText(textoRecebido));
            chatArea.addHyperlinkListener(e -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        String link = e.getURL().toString();
                        Desktop.getDesktop().browse(new URI(link));
                    } catch (Exception ex) {
                        System.err.println("Erro ao abrir o link: " + ex.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Erro ao atualizar chat: " + e.getMessage());
        }
    }

    // Método auxiliar para criar botões com estilização
    private JButton createButton(String text, Color backgroundColor, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(400, 75));
        button.addActionListener(_ -> action.run());
        return button;
    }
}