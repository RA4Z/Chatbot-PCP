import chat.ChatPanel;
import file_search.SearchPanel;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ChatGUI extends JFrame {

    private final JPanel homePanel;
    private final CardLayout cardLayout;

    private ChatPanel chatPanel;
    private SearchPanel searchPanel;

    public ChatGUI() {
        setTitle("ChatBot PCP - BETA");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            setIconImage(ImageIO.read(new File("./images/chatbot_normal.png")));
        } catch (Exception e) {
            System.out.println("Erro ao carregar ícone: " + e.getMessage());
        }

        homePanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) homePanel.getLayout();
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        createHomePanel();
        add(homePanel);
        createFooter();

        setVisible(true);
    }

    private void createHomePanel() {
        JLabel titleLabel = new JLabel("Boas vindas à Central de Sistemas PCP WEN!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton chatButton = createButton("Chatbot PCP WEN", new Color(0x365D86), this::showChatPanel);
        JButton searchButton = createButton("Procurar Arquivo JGS", new Color(0xD19300), this::showSearchPanel);

        buttonPanel.add(chatButton);
        buttonPanel.add(searchButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        homePanel.add(contentPanel, "home");
    }

    private JButton createButton(String text, Color backgroundColor, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.PLAIN, 30));
        button.setPreferredSize(new Dimension(400, 50));
        button.addActionListener(_ -> action.run());
        return button;
    }

    private void showChatPanel() {
        if (chatPanel == null) {
            chatPanel = new ChatPanel(homePanel);
        }
        homePanel.add(chatPanel, "chat");
        cardLayout.show(homePanel, "chat");
    }

    private void showSearchPanel() {
        if (searchPanel == null) {
            searchPanel = new SearchPanel(homePanel);
        }
        homePanel.add(searchPanel, "search");
        cardLayout.show(homePanel, "search");
    }

    private void createFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        JLabel footerText = new JLabel("© 2024 PCP WEN - Desenvolvido e Prototipado por Robert Aron Zimmermann.");
        footerText.setHorizontalAlignment(SwingConstants.CENTER);
        footerText.setFont(new Font("Arial", Font.PLAIN, 12));

        try {
            Image footerImage = ImageIO.read(new File("./images/logo.png"));
            JLabel imageLabel = new JLabel(new ImageIcon(footerImage));
            imageLabel.setHorizontalAlignment(SwingConstants.LEFT);

            footerPanel.add(imageLabel, BorderLayout.WEST);
            footerPanel.add(footerText, BorderLayout.CENTER);
            add(footerPanel, BorderLayout.SOUTH);
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem do rodapé: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}