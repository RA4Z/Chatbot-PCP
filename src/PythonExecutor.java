import javax.swing.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class PythonExecutor extends SwingWorker<Void, String> {

    private final String message;
    private final JTextPane chatArea; // Mudança: JTextPane
    private final JLabel statusLabel;
    private final JTextField messageField;
    private final JButton sendButton;
    private StringBuilder messageHistory;

    public PythonExecutor(String message, JTextPane chatArea, JLabel statusLabel, JTextField messageField,
                          JButton sendButton, StringBuilder messageHistory) {
        this.message = message;
        this.chatArea = chatArea;
        this.statusLabel = statusLabel;
        this.messageField = messageField;
        this.sendButton = sendButton;
        this.messageHistory = messageHistory;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            this.messageField.setEnabled(false);
            this.sendButton.setEnabled(false);

            String userName = System.getProperty("user.name");
            publish("Enviando mensagem...");

            // URL do servidor Flask
            URL url = new URL("http://10.1.43.63:5000/gemini");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Enviar a mensagem como parâmetro
            String data = "message=" + URLEncoder.encode(message, "UTF-8") + "&username=" + URLEncoder.encode(userName, "UTF-8");
            OutputStream output = connection.getOutputStream();
            output.write(data.getBytes("UTF-8"));
            output.flush();
            output.close();

            // Ler a resposta do servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line + "<br>");
            }
            reader.close();
            publish(response.toString()); // Publica a resposta para ser exibida

        } catch (Exception ex) {
            throw new RuntimeException("Erro ao comunicar com o servidor Flask: " + ex.getMessage());
        }

        this.messageField.setEnabled(true);
        this.sendButton.setEnabled(true);
        return null;
    }

    @Override
    protected void process(java.util.List<String> chunks) {
        for (String chunk : chunks) {
            if (chunk.equals("Enviando mensagem...")) {
                statusLabel.setText(chunk);
            } else {
                // Adiciona texto ao JTextPane com estilo HTML
                chatArea.setContentType("text/html");

                Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
                String formattedText = pattern.matcher(chunk).replaceAll("<strong>$1</strong>");

                Pattern linkRegex = Pattern.compile("(https?:\\/\\/[^\\s]+)");
                formattedText = linkRegex.matcher(formattedText)
                        .replaceAll("<a href=\"$1\" style=\"color:#3B8CED;\" target=\"_blank\">$1</a>");

                messageHistory.append(
                        "<div style=\"font-size:18px; color:white; padding:5px; background-color: #176B87; border: 1px solid #000;\">"
                                + "🤖<br>" + formattedText + "</div> <br>");

                try {
                    chatArea.setText(messageHistory.toString());

                } catch (Error e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void done() {
        statusLabel.setText("");
    }
}