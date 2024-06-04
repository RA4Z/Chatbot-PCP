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
        homePanel = new JPanel(new BorderLayout()); // Usa BorderLayout
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Boas vindas à Central de Sistemas PCP WEN!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Panel para os botões
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Adiciona um espaçamento na parte superior

        JButton chatButton = new JButton("Chatbot PCP WEN");
        chatButton.setBackground(new Color(0x365D86));
        chatButton.setForeground(Color.WHITE);
        chatButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chatButton.setFont(new Font("Arial", Font.PLAIN, 30));
        chatButton.setPreferredSize(new Dimension(400, 50));

        JButton searchButton = new JButton("Procurar Arquivo JGS");
        searchButton.setBackground(new Color(0xD19300));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setFont(new Font("Arial", Font.PLAIN, 30));
        searchButton.setPreferredSize(new Dimension(400, 50));

        // Adiciona os botões ao buttonPanel
        buttonPanel.add(chatButton);
        buttonPanel.add(searchButton);

        // Adiciona os componentes ao homePanel
        homePanel.add(titleLabel, BorderLayout.NORTH); // TitleLabel na parte superior
        homePanel.add(buttonPanel, BorderLayout.CENTER); // ButtonPanel no centro

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