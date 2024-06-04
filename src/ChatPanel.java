import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class ChatPanel extends JPanel {

    private final JTextPane chatArea; // Substituindo JTextArea por JTextPane
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton resetButton; // Botão Resetar
    private final JLabel statusLabel;
    private final StringBuilder messageHistory = new StringBuilder();

    public ChatPanel() {
        // Configuração do painel
        setLayout(new BorderLayout());
        setBackground(new Color(0xE8E7E7)); // Cor do chat: #e8e7e7

        // Criação dos componentes
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(0xE8E7E7)); // Cor de fundo do output: #176b87
        chatArea.setForeground(Color.WHITE);

        // Cria um padding preto de 10 pixels
        EmptyBorder paddingBorder = new EmptyBorder(15, 15, 15, 15);
        LineBorder blackBorder = new LineBorder(new Color(0xe8e7e7)); // Cria a borda preta

        // Cria uma borda composta com o padding e a borda preta
        chatArea.setBorder(new CompoundBorder(paddingBorder, blackBorder));

        messageField = new JTextField();
        messageField.setBackground(Color.WHITE); // Cor de fundo do input: #053b50
        messageField.setForeground(Color.BLACK);
        messageField.setFont(new Font("Arial", Font.PLAIN, 30));

        sendButton = new JButton("Enviar");
        sendButton.setBackground(new Color(0x0C2D48));
        sendButton.setForeground(Color.WHITE);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.setFont(new Font("Arial", Font.PLAIN, 30));

        resetButton = new JButton("Resetar"); // Criação do botão Resetar
        resetButton.setBackground(new Color(0x60100B));
        resetButton.setForeground(Color.WHITE);
        resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetButton.setFont(new Font("Arial", Font.PLAIN, 30));

        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        statusLabel.setForeground(Color.GRAY);

        // Adiciona componentes ao painel
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(0xE8E7E7)); // Cor do chat: #e8e7e7
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(messageField, BorderLayout.CENTER);

        // Cria um painel para o botão "Enviar" e "Resetar"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(0xE8E7E7)); // Cor do chat: #e8e7e7
        buttonPanel.add(resetButton); // Adiciona o botão "Resetar" ao painel
        buttonPanel.add(sendButton);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Ações dos botões e campos de texto
        messageField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        sendButton.addActionListener(_ -> sendMessage());
        // Ação do botão "Resetar"
        resetButton.addActionListener(_ -> resetChat());
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            // Adiciona texto ao chatArea com estilo HTML
            chatArea.setContentType("text/html");

            Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
            String formattedText = pattern.matcher(message).replaceAll("<strong>$1</strong>");

            Pattern linkRegex = Pattern.compile("(https?://\\S+)");
            formattedText = linkRegex.matcher(formattedText)
                    .replaceAll("<a href=\"$1\" style=\"color:#3B8CED\" target=\"_blank\">$1</a>");

            messageHistory.append("<div style=\"font-size:18px; color:white; padding:5px; border: 1px solid #000; background-color: #053B50; text-align: right;\">").append(formattedText).append("</div> <br>");

            try {
                chatArea.setText(messageHistory.toString());

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            messageField.setText("");

            // Executa o script Python em uma thread separada
            new PythonExecutor(message, chatArea, statusLabel, messageField, sendButton, messageHistory, resetButton).execute();
        }
    }

    private void resetChat() { // Função para resetar o chat
        try {
            messageHistory.setLength(0); // Limpa o histórico de mensagens
            chatArea.setText(""); // Limpa a área de chat
            statusLabel.setText(""); // Limpa o status
            String userName = System.getProperty("user.name");
            URL url = new URI("http://10.1.43.63:5000/quit").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String data = "username=" + URLEncoder.encode(userName, StandardCharsets.UTF_8);
            OutputStream output = connection.getOutputStream();
            output.write(data.getBytes(StandardCharsets.UTF_8));
            output.flush();
            output.close();
            new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

        } catch (Exception ex) {
            throw new RuntimeException("Erro ao comunicar com o servidor Flask: " + ex.getMessage());
        }
    }
}