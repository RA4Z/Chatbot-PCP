package file_search;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SearchPanel extends JPanel {

    private final JTextField inputField;
    private final JTextPane resultPane;

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

        JButton searchButton = new JButton("Pesquisar");
        searchButton.setBackground(new Color(0x0C2D48));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setFont(new Font("Arial", Font.PLAIN, 30));

        resultPane = new JTextPane();
        resultPane.setEditable(false);
        resultPane.setBackground(new Color(0xE8E7E7));
        resultPane.setForeground(Color.WHITE);

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

        // Cria um painel para o input e o botão
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(0xE8E7E7));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(backButton);

        // Cria um painel para o botão "Voltar"
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(0xE8E7E7));
        buttonPanel.add(inputField, BorderLayout.CENTER);
        buttonPanel.add(searchButton, BorderLayout.EAST);

        // Adiciona os componentes ao painel principal
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultPane), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Adiciona o listener ao botão de pesquisa
        searchButton.addActionListener(_ -> {
            String searchTerm = inputField.getText();
            String searchResults = performSearch(searchTerm);
            resultPane.setText(searchResults);
            inputField.setText(""); // Limpa o inputField
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
                    String searchResults = performSearch(searchTerm);
                    resultPane.setText(searchResults);
                    inputField.setText(""); // Limpa o inputField
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private String performSearch(String searchTerm) {
        // Código para realizar a pesquisa usando o termo "searchTerm"
        String searchResults = "Resultados da pesquisa para '" + searchTerm + "'";
        System.out.println(searchResults);
        return searchResults;
    }
}