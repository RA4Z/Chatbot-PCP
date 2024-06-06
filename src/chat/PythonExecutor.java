package chat;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class PythonExecutor extends SwingWorker<Void, String> {

    private final String message;
    private final JTextPane chatArea;
    private final JLabel statusLabel;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton resetButton;
    private final StringBuilder messageHistory;
    private final JProgressBar loadingBar;

    public PythonExecutor(String message, JTextPane chatArea, JLabel statusLabel, JTextField messageField,
                          JButton sendButton, StringBuilder messageHistory, JButton resetButton, JProgressBar loadingBar) {
        this.message = message;
        this.chatArea = chatArea;
        this.statusLabel = statusLabel;
        this.messageField = messageField;
        this.sendButton = sendButton;
        this.messageHistory = messageHistory;
        this.resetButton = resetButton;
        this.loadingBar = loadingBar;
    }

    @Override
    protected Void doInBackground() throws Exception {
        disableInput();

        publish("Escrevendo...");

        String userName = System.getProperty("user.name");
        URL url = new URI("http://10.1.43.63:5000/gemini").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String data = "message=" + URLEncoder.encode(message, StandardCharsets.UTF_8) + "&username=" + URLEncoder.encode(userName, StandardCharsets.UTF_8);
        try (OutputStream output = connection.getOutputStream()) {
            output.write(data.getBytes(StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append(" <br>");
            }
        }
        publish(response.toString());

        return null;
    }

    @Override
    protected void process(java.util.List<String> chunks) {
        loadingBar.setVisible(true);
        for (String chunk : chunks) {
            if (chunk.equals("Escrevendo...")) {
                statusLabel.setText(chunk);
            } else {
                String formattedText = formatText(chunk);
                messageHistory.append("<div style=\"font-size:18px; color:white; padding:5px; background-color: #176B87; border: 1px solid #000;\">" + "🤖<br>").append(formattedText).append("</div> <br>");
                updateChatArea();
                loadingBar.setVisible(false);
            }
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

    private void updateChatArea() {
        try {
            chatArea.setContentType("text/html");
            chatArea.setText(messageHistory.toString());
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
            loadingBar.setVisible(false);
        }
    }

    @Override
    protected void done() {
        enableInput();
        statusLabel.setText("");
    }

    private void disableInput() {
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
        resetButton.setEnabled(false);
    }

    private void enableInput() {
        messageField.setEnabled(true);
        sendButton.setEnabled(true);
        resetButton.setEnabled(true);
    }
}