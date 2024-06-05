package chat;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.awt.Desktop;
import javax.swing.event.HyperlinkEvent;

public class PythonExecutor extends SwingWorker<Void, String> {

    private final String message;
    private final JTextPane chatArea; // Mudança: JTextPane
    private final JLabel statusLabel;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton resetButton;
    private final StringBuilder messageHistory;

    public PythonExecutor(String message, JTextPane chatArea, JLabel statusLabel, JTextField messageField,
                          JButton sendButton, StringBuilder messageHistory, JButton resetButton) {
        this.message = message;
        this.chatArea = chatArea;
        this.statusLabel = statusLabel;
        this.messageField = messageField;
        this.sendButton = sendButton;
        this.messageHistory = messageHistory;
        this.resetButton = resetButton;
    }

    @Override
    protected Void doInBackground() {
        try {
            this.messageField.setEnabled(false);
            this.sendButton.setEnabled(false);
            this.resetButton.setEnabled(false);

            String userName = System.getProperty("user.name");
            publish("Escrevendo...");

            // URL do servidor Flask
            URL url = new URI("http://10.1.43.63:5000/gemini").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Enviar a mensagem como parâmetro
            String data = "message=" + URLEncoder.encode(message, StandardCharsets.UTF_8) + "&username=" + URLEncoder.encode(userName, StandardCharsets.UTF_8);
            OutputStream output = connection.getOutputStream();
            output.write(data.getBytes(StandardCharsets.UTF_8));
            output.flush();
            output.close();

            // Ler a resposta do servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line).append("<br>");
            }
            reader.close();
            publish(response.toString()); // Publica a resposta para ser exibida

        } catch (Exception ex) {
            throw new RuntimeException("Erro ao comunicar com o servidor Flask: " + ex.getMessage());
        }

        this.messageField.setEnabled(true);
        this.sendButton.setEnabled(true);
        this.resetButton.setEnabled(true);
        return null;
    }

    @Override
    protected void process(java.util.List<String> chunks) {
        for (String chunk : chunks) {
            if (chunk.equals("Escrevendo...")) {
                statusLabel.setText(chunk);
            } else {
                // Adiciona texto ao JEditorPane com estilo HTML
                Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
                String formattedText = pattern.matcher(chunk).replaceAll("<strong>$1</strong>");

                Pattern linkRegex = Pattern.compile("(https?://\\S+)");
                formattedText = linkRegex.matcher(formattedText)
                        .replaceAll("<a href=\"$1\" style=\"color:#3B8CED;cursor:pointer\" target=\"_blank\">$1</a>");

                messageHistory.append("<div style=\"font-size:18px; color:white; padding:5px; background-color: #176B87; border: 1px solid #000;\">" + "🤖<br>").append(formattedText).append("</div> <br>");

                try {
                    // Define o tipo de conteúdo como HTML
                    chatArea.setContentType("text/html");
                    // Define o texto do JEditorPane
                    chatArea.setText(messageHistory.toString());

                    // Adiciona um HyperlinkListener para abrir links
                    chatArea.addHyperlinkListener(e -> {
                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            try {
                                String link = e.getURL().toString().substring(0,e.getURL().toString().indexOf("<br>"));
                                Desktop.getDesktop().browse(new URI(link));
                            } catch (Exception ex) {
                                System.err.println("Erro ao abrir o link: " + ex.getMessage());
                            }
                        }
                    });

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @Override
    protected void done() {
        statusLabel.setText("");
    }
}