import chat.ChatPanel;
import file_search.SearchPanel;
import secretaria.SecretariaPanel;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ChatGUI extends JFrame {

    private final JPanel homePanel;
    private final CardLayout cardLayout;

    private SecretariaPanel secretariaPanel;
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

        createHeader();
        createHomePanel();
        add(homePanel, BorderLayout.CENTER);
        createFooter();

        setVisible(true);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setBackground(new Color(0x365D86));

        JButton backButton = new JButton("HOME");
        backButton.setBackground(Color.WHITE);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setForeground(new Color(0x365D86));
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(_ -> showHomePanel());

        headerPanel.add(backButton, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void createHomePanel() {
        JLabel titleLabel = new JLabel("Boas vindas à Central de Sistemas PCP WEN!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        buttonPanel.add(createButton("Chatbot PCP WEN", new Color(0x365D86), this::showChatPanel));
        buttonPanel.add(createButton("Procurar Arquivo JGS", new Color(0xD19300), this::showSearchPanel));
        buttonPanel.add(createButton("Indicadores da Secretária", new Color(0x00A65A), this::showAutomatismosPanel));

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

    private void showPanel(String panelName) {
        switch (panelName) {
            case "chat":
                if (chatPanel == null) {
                    chatPanel = new ChatPanel();
                }
                homePanel.add(chatPanel, "chat");
                break;
            case "search":
                if (searchPanel == null) {
                    searchPanel = new SearchPanel();
                }
                homePanel.add(searchPanel, "search");
                break;
            case "secretaria":
                if (secretariaPanel == null) {
                    secretariaPanel = new SecretariaPanel();
                }
                homePanel.add(secretariaPanel, "secretaria");
                break;
        }
        cardLayout.show(homePanel, panelName);
    }

    private void showChatPanel() {
        showPanel("chat");
    }

    private void showSearchPanel() {
        showPanel("search");
    }

    private void showAutomatismosPanel() {
        showPanel("secretaria");
    }

    private void showHomePanel() {
        cardLayout.show(homePanel, "home");
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
            JLabel imageLabel = getjLabel(footerImage);

            footerPanel.add(imageLabel, BorderLayout.WEST);
            footerPanel.add(footerText, BorderLayout.CENTER);
            add(footerPanel, BorderLayout.SOUTH);
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem do rodapé: " + e.getMessage());
        }
    }

    private static JLabel getjLabel(Image footerImage) {
        JLabel imageLabel = new JLabel(new ImageIcon(footerImage));
        imageLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Adiciona um MouseListener para o JLabel da imagem
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Abre o link no navegador
                try {
                    Desktop.getDesktop().browse(new URI("https://automations-database.vercel.app/"));
                } catch (URISyntaxException | IOException ex) {
                    System.err.println("Erro ao abrir o link: " + ex.getMessage());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                imageLabel.setToolTipText("Navegar para Automation's Database");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                imageLabel.setCursor(Cursor.getDefaultCursor());
                imageLabel.setToolTipText(null);
            }
        });
        return imageLabel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}