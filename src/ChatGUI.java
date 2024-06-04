import chat.ChatPanel;
import file_search.SearchPanel;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

public class ChatGUI extends JFrame {

    private final JPanel homePanel;
    private final CardLayout cardLayout;

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

        // Cria o homePanel com CardLayout
        homePanel = new JPanel();
        cardLayout = new CardLayout();
        homePanel.setLayout(cardLayout);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cria a tela inicial (Home)
        createHomePanel();

        // Adiciona a tela inicial à janela
        add(homePanel);

        // Cria o rodapé
        createFooter();

        // Exibe a janela
        setVisible(true);
    }

    // Cria a tela de Home
    private void createHomePanel() {
        // TitleLabel
        JLabel titleLabel = new JLabel("Boas vindas à Central de Sistemas PCP WEN!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // ButtonPanel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Botões
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

        // Cria um JPanel para conter titleLabel e buttonPanel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        // Adiciona o contentPanel ao homePanel
        homePanel.add(contentPanel, "home"); // Utiliza o nome "home" para identificar o painel

        // Adiciona os listeners para os botões
        chatButton.addActionListener(_ -> showChatPanel());

        searchButton.addActionListener(_ -> showSearchPanel());
    }

    // Mostra a tela de chat
    private void showChatPanel() {
        // Cria a tela de chat se ainda não existir
        if (chatPanel == null) {
            chatPanel = new ChatPanel(homePanel);
        }

        // Adiciona a tela de chat ao homePanel
        homePanel.add(chatPanel, "chat"); // Utiliza o nome "chat" para identificar o painel

        // Mostra a tela de chat
        cardLayout.show(homePanel, "chat");
    }

    // Mostra a tela de pesquisa
    private void showSearchPanel() {
        // Cria a tela de pesquisa se ainda não existir
        if (searchPanel == null) {
            searchPanel = new SearchPanel(homePanel);
        }

        // Adiciona a tela de pesquisa ao homePanel
        homePanel.add(searchPanel, "search"); // Utiliza o nome "search" para identificar o painel

        // Mostra a tela de pesquisa
        cardLayout.show(homePanel, "search");
    }

    // Cria o rodapé com imagem e texto
    private void createFooter() {
        // Cria o JPanel para o rodapé
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        Border topBorder = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(topBorder, footerPanel.getBorder()));

        // Cria o JLabel para o texto do rodapé
        JLabel footerText = new JLabel("© 2024 PCP WEN - Desenvolvido e Prototipado por Robert Aron Zimmermann.");
        footerText.setHorizontalAlignment(SwingConstants.CENTER);
        footerText.setFont(new Font("Arial", Font.PLAIN, 12));

        // Carrega a imagem do rodapé
        try {
            Image footerImage = ImageIO.read(new File("./images/logo.png"));
            JLabel imageLabel = new JLabel(new ImageIcon(footerImage));
            imageLabel.setHorizontalAlignment(SwingConstants.LEFT);

            // Adiciona a imagem e o texto ao footerPanel
            footerPanel.add(imageLabel, BorderLayout.WEST);
            footerPanel.add(footerText, BorderLayout.CENTER);

            // Adiciona o footerPanel à janela principal
            add(footerPanel, BorderLayout.SOUTH);
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem do rodapé: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}