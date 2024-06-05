package chat;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatPanel extends JPanel {

    private static final String SERVER_URL = "http://10.1.43.63:5000";
    private static final Color CHAT_BG_COLOR = new Color(0xE8E7E7);
    private static final Color SEND_BUTTON_COLOR = new Color(0x0C2D48);
    private static final Color RESET_BUTTON_COLOR = new Color(0x60100B);

    private final JTextPane chatArea;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton resetButton;
    private final JLabel statusLabel;
    private final StringBuilder messageHistory = new StringBuilder();

    public ChatPanel() {
        // Configuração do painel
        setLayout(new BorderLayout());
        setBackground(CHAT_BG_COLOR);

        // Criação dos componentes
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(CHAT_BG_COLOR);
        chatArea.setForeground(Color.WHITE);
        chatArea.setBorder(createChatAreaBorder());

        messageField = new JTextField();
        messageField.setBackground(Color.WHITE);
        messageField.setForeground(Color.BLACK);
        messageField.setFont(new Font("Arial", Font.PLAIN, 30));

        sendButton = new JButton("Enviar");
        sendButton.setBackground(SEND_BUTTON_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.setFont(new Font("Arial", Font.PLAIN, 30));

        resetButton = new JButton("Resetar");
        resetButton.setBackground(RESET_BUTTON_COLOR);
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
        bottomPanel.setBackground(CHAT_BG_COLOR);
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(messageField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CHAT_BG_COLOR);
        buttonPanel.add(resetButton);
        buttonPanel.add(sendButton);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Ações dos botões e campos de texto
        messageField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        sendButton.addActionListener(_ -> sendMessage());
        resetButton.addActionListener(_ -> resetChat());
    }

    private CompoundBorder createChatAreaBorder() {
        EmptyBorder paddingBorder = new EmptyBorder(15, 15, 15, 15);
        LineBorder blackBorder = new LineBorder(CHAT_BG_COLOR);
        return new CompoundBorder(paddingBorder, blackBorder);
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            // Adiciona texto ao chatArea com estilo HTML
            chatArea.setContentType("text/html");

            // Formata a mensagem: negrito e links
            message = formatMessage(message);

            messageHistory.append("<div style=\"font-size:18px; color:white; padding:5px; border: 1px solid #000; background-color: #053B50; text-align: right;\">").append(message).append("</div> <br>");

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

    private String formatMessage(String message) {
        // Adiciona negrito
        message = message.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");

        // Adiciona links
        Pattern linkRegex = Pattern.compile("(https?://\\S+)");
        Matcher linkMatcher = linkRegex.matcher(message);
        message = linkMatcher.replaceAll("<a href=\"$1\" style=\"color:#3B8CED\" target=\"_blank\">$1</a>");

        return message;
    }

    private void resetChat() {
        try {
            messageHistory.setLength(0);
            chatArea.setText("");
            statusLabel.setText("");
            String userName = System.getProperty("user.name");
            URL url = new URI(SERVER_URL + "/quit").toURL();
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