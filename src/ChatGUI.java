import chat.ChatPanel;
import file_search.SearchPanel;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ChatGUI extends JFrame {
    // Tela inicial (Home)
    private JPanel homePanel;

    // Tela de chat
    private ChatPanel chatPanel;

    // Tela de pesquisa
    private SearchPanel searchPanel;

    public ChatGUI() {
        // Configuração da janela principal
        setTitle("ChatBot PCP - BETA");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Carrega a imagem do ícone
        try {
            Image iconImage = ImageIO.read(new File("./images/chatbot_normal.png"));
            setIconImage(iconImage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Cria a tela inicial (Home)
        createHomePanel();

        // Adiciona a tela inicial à janela
        add(homePanel);

        // Exibe a janela
        setVisible(true);
    }

    // Cria a tela de Home
    private void createHomePanel() {
        homePanel = new JPanel(new GridLayout(3, 1, 10, 10));
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Bem-vindo ao ChatBot PCP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton chatButton = new JButton("Chat");
        chatButton.setBackground(new Color(0x365D86));
        chatButton.setForeground(Color.WHITE);
        chatButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chatButton.setFont(new Font("Arial", Font.PLAIN, 30));

        JButton searchButton = new JButton("Pesquisa");
        searchButton.setBackground(new Color(0xD19300));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setFont(new Font("Arial", Font.PLAIN, 30));

        // Adiciona os componentes ao painel
        homePanel.add(titleLabel);
        homePanel.add(chatButton);
        homePanel.add(searchButton);

        // Adiciona os listeners para os botões
        chatButton.addActionListener(_ -> {
            showChatPanel();
            homePanel.setVisible(false);
        });

        searchButton.addActionListener(_ -> {
            showSearchPanel();
            homePanel.setVisible(false);
        });
    }

    // Mostra a tela de chat
    private void showChatPanel() {
        // Cria a tela de chat se ainda não existir
        if (chatPanel == null) {
            chatPanel = new ChatPanel(homePanel);
        }

        // Adiciona a tela de chat à janela
        add(chatPanel);
        chatPanel.setVisible(true);

        // Redesenha a janela para exibir a nova tela
        revalidate();
        repaint();
    }

    // Mostra a tela de pesquisa
    private void showSearchPanel() {
        // Cria a tela de pesquisa se ainda não existir
        if (searchPanel == null) {
            searchPanel = new SearchPanel(homePanel);
        }

        // Adiciona a tela de pesquisa à janela
        add(searchPanel);
        searchPanel.setVisible(true);

        // Redesenha a janela para exibir a nova tela
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}