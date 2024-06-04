package file_search;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.IOException;

public class SearchPanel extends JPanel {

    private final JTextField inputField;
    private final JTextPane resultPane;
    private final JPanel optionsPanel;
    private String lastInputText;
    List<String> folders = new ArrayList<>();

    public SearchPanel(JPanel previousPanel) {
        // Adiciona a referência ao painel anterior

        // Configura o painel
        setLayout(new BorderLayout());
        setBackground(new Color(0xE8E7E7));

        // Criação dos componentes
        inputField = new JTextField();
        inputField.setBackground(Color.WHITE);
        inputField.setForeground(Color.BLACK);
        inputField.setFont(new Font("Arial", Font.PLAIN, 30));

        JButton searchButton = new JButton("Procurar Arquivo");
        searchButton.setBackground(new Color(0x0C2D48));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setFont(new Font("Arial", Font.PLAIN, 30));

        resultPane = new JTextPane();
        resultPane.setEditable(false);
        resultPane.setBackground(new Color(0xE8E7E7));
        resultPane.setForeground(Color.WHITE);
        resultPane.setContentType("text/html");

        // Cria um padding preto de 10 pixels
        EmptyBorder paddingBorder = new EmptyBorder(15, 15, 15, 15);
        LineBorder blackBorder = new LineBorder(new Color(0xE8E7E7));
        resultPane.setBorder(new CompoundBorder(paddingBorder, blackBorder));

        // Botão para voltar
        JButton backButton = new JButton("Voltar"); // Cria o botão Voltar
        backButton.setBackground(new Color(0x02A724));
        backButton.setForeground(Color.WHITE);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setFont(new Font("Arial", Font.PLAIN, 30));

        optionsPanel = new JPanel(new FlowLayout());
        optionsPanel.setBackground(new Color(0xE8E7E7));

        JLabel titleLabel = new JLabel("Opção Correta:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        optionsPanel.add(titleLabel);

        for(int i = 1; i<=5; i++) {
            final String textTemp= String.valueOf(i);
            JButton optionButton = new JButton(textTemp); // Cria o botão de opção
            optionButton.setBackground(new Color(0x1960d1));
            optionButton.setForeground(Color.WHITE);
            optionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            optionButton.setFont(new Font("Arial", Font.PLAIN, 30));

            optionButton.addActionListener(_ -> sendOpinion(textTemp));
            optionsPanel.add(optionButton);
        }

        JButton errorButton = new JButton("Nenhuma"); // Cria o botão de opção
        errorButton.setBackground(new Color(0x990000));
        errorButton.setForeground(Color.WHITE);
        errorButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        errorButton.setFont(new Font("Arial", Font.PLAIN, 30));
        errorButton.addActionListener(_ -> sendOpinion("Nenhuma"));
        optionsPanel.add(errorButton);
        optionsPanel.setVisible(false);

        // Cria um painel para o input e o botão
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(0xE8E7E7));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(backButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        // Cria um painel para o botão "Voltar"
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(0xE8E7E7));
        buttonPanel.add(inputField, BorderLayout.CENTER);
        buttonPanel.add(searchButton, BorderLayout.EAST);

        // Adiciona os componentes ao painel principal
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultPane), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(optionsPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Adiciona o listener ao botão de pesquisa
        searchButton.addActionListener(_ -> {
            String searchTerm = inputField.getText();
            if (!searchTerm.isEmpty()) {
                // Desabilita o inputField para evitar múltiplas pesquisas
                inputField.setEnabled(false);
                searchButton.setEnabled(false);
                // Cria uma nova thread para executar a pesquisa
                new Thread(() -> {
                    String searchResults = performSearch(searchTerm);
                    // Atualiza o resultPane na thread principal
                    SwingUtilities.invokeLater(() -> {
                        resultPane.setText(searchResults);
                        inputField.setText(""); // Limpa o inputField
                        // Habilita o inputField após a pesquisa
                        inputField.setEnabled(true);
                        searchButton.setEnabled(true);
                    });
                }).start();
            }
        });

        // Adiciona o listener ao botão Voltar
        backButton.addActionListener(_ -> {
            setVisible(false);
            previousPanel.setVisible(true);
            revalidate();
            repaint();
        });

        // Adiciona o listener para a tecla Enter no inputField
        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String searchTerm = inputField.getText();
                    if (!searchTerm.isEmpty()) {
                        inputField.setEnabled(false);
                        searchButton.setEnabled(false);
                        new Thread(() -> {
                            String searchResults = performSearch(searchTerm);
                            SwingUtilities.invokeLater(() -> {
                                resultPane.setText(searchResults);
                                inputField.setText("");
                                inputField.setEnabled(true);
                                searchButton.setEnabled(true);
                            });
                        }).start();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void sendOpinion(String option) {
        int desiredIndex = -1;
        switch (option) {
            case "1":
                desiredIndex = 0;
                break;

            case "2":
                desiredIndex = 1;
                break;

            case "3":
                desiredIndex = 2;
                break;

            case "4":
                desiredIndex = 3;
                break;

            case "5":
                desiredIndex = 4;
                break;

            default:
                break;
        }

        if(desiredIndex > -1) {
            try {
                String exePath = "./scripts/export_json_history.exe";
                ProcessBuilder pb = new ProcessBuilder(exePath, lastInputText, folders.get(desiredIndex));
                // Inicie o processo
                Process process = pb.start();
                // Aguarde a conclusão do processo
                process.waitFor();

            } catch (IOException | InterruptedException e) {
                System.err.println("Erro ao executar o arquivo EXE: " + e.getMessage());
            }
        }
        lastInputText = "";
        optionsPanel.setVisible(false);
    }

    private String performSearch(String message) {
        try {
            lastInputText = message;
            URL url = new URI("http://10.1.43.63:5000/search").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Enviar a mensagem como parâmetro
            String data = "message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
            OutputStream output = connection.getOutputStream();
            output.write(data.getBytes(StandardCharsets.UTF_8));
            output.flush();
            output.close();

            // Ler a resposta do servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            folders = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.contains("BR_SC_JGS_WM_LOGISTICA") && !line.contains("Observação")) {
                    folders.add(line.replace("**", ""));
                }
                response.append(line).append("<br>");
            }
            reader.close();
            String searchResults = response.toString();
            Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
            String formattedText = pattern.matcher(searchResults).replaceAll("<strong>$1</strong>");
            Pattern linkRegex = Pattern.compile("(https?://\\S+)");
            formattedText = linkRegex.matcher(formattedText)
                    .replaceAll("<a href=\"$1\" style=\"color:#3B8CED\" target=\"_blank\">$1</a>");

            optionsPanel.setVisible(true);

            return "<div style=\"font-size:18px; color:white; padding:5px; border: 1px solid #000; background-color: #176B87;\">" +
                    formattedText +
                    "</div> <br>";

        } catch (Exception ex) {
            throw new RuntimeException("Erro ao comunicar com o servidor Flask: " + ex.getMessage());
        }
    }
}