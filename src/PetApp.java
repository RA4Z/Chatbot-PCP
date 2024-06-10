import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PetApp {

    private final JFrame frame;
    private JLabel imageLabel;
    private final JTextArea messageArea; // Novo JLabel para mostrar a mensagem
    private Point startDragPoint;

    public PetApp() {
        frame = new JFrame("Pet App");
        frame.setUndecorated(true); // Remove a moldura da janela
        frame.setSize(200, 250); // Define as dimensões da imagem
        frame.setAlwaysOnTop(true); // Mantém o app em foco
        frame.setBackground(new Color(0, 0, 0, 0)); // Transparência total (0 = totalmente transparente)
        frame.setLocationRelativeTo(null); // Centraliza a janela

        List<String> messages = getStrings();

        // Carrega a imagem
        try {
            BufferedImage image = ImageIO.read(new File("images/chat.png")); // Substitua "pet.png" pelo nome da sua imagem
            image = resizeImage(image);
            imageLabel = new JLabel(new ImageIcon(image));
        } catch (IOException e) {
            System.out.println("Ocorreu o erro " + e);
        }

        // Cria um label para a imagem
        imageLabel.setBorder(BorderFactory.createEmptyBorder());
        frame.add(imageLabel);

        // Cria o JLabel para a mensagem
        messageArea = new JTextArea("", 1, 1); // Cria JTextArea com 1 linha e 1 coluna
        messageArea.setFont(new Font("Arial", Font.BOLD, 12));
        messageArea.setForeground(Color.WHITE);
        messageArea.setEditable(false); // Desabilita edição
        messageArea.setLineWrap(true); // Ativa quebra de linha
        messageArea.setWrapStyleWord(true); // Ativa quebra de palavras
        messageArea.setOpaque(false); // Transparente
        frame.add(messageArea, BorderLayout.NORTH); // Adiciona o JTextArea no topo da janela

        // Permite arrastar a janela
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Obtem a posição do mouse em relação ao label (dentro da janela)
                startDragPoint = new Point(e.getX(), e.getY()); // Use getX() e getY()
            }
        });
        imageLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - startDragPoint.x;
                int dy = e.getY() - startDragPoint.y;

                // Move a janela
                frame.setLocation(frame.getX() + dx, frame.getY() + dy);
            }
        });

        // Clique com o botão direito
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showOptionsMenu(e);
                }
            }
        });

        // Clique com o botão esquerdo
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(messages.size());
                    String message = messages.get(randomIndex);
                    messageArea.setText(message); // Define o texto no JLabel
                    new Timer(5000, _ -> messageArea.setText("")).start();
                }
            }
        });

        frame.setVisible(true);
    }

    private static List<String> getStrings() {
        List<String> messages = new ArrayList<>();
        messages.add("Olá! Sou o chatbot do PCP da WEG Energia, estou aqui para auxiliar você com informações e procedimentos da área.");
        messages.add("E aí! Sou o seu amigo chatbot do PCP da WEG Energia. Posso te ajudar com qualquer dúvida sobre planejamento, programação e controle da produção.");
        messages.add("Precisa de informações sobre o PCP da WEG Energia? Pode contar comigo! Sou o chatbot da área, pronto para te ajudar.");
        messages.add("Sou o chatbot do PCP da WEG Energia, desenvolvido para fornecer informações e auxiliar na resolução de dúvidas sobre os processos da área.");
        messages.add("Olá, sou o chatbot do PCP da WEG Energia. Estou à disposição.");
        return messages;
    }

    private void showOptionsMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem openChatbot = new JMenuItem("Abrir Chatbot");
        openChatbot.addActionListener(_ -> openChatbot());
        menu.add(openChatbot);
        JMenuItem openLinkItem = new JMenuItem("Abrir Link");
        openLinkItem.addActionListener(_ -> openLink());
        menu.add(openLinkItem);
        JMenuItem openFolderItem = new JMenuItem("Abrir Pasta");
        openFolderItem.addActionListener(_ -> openFolder());
        menu.add(openFolderItem);
        menu.addSeparator();
        JMenuItem closeItem = new JMenuItem("Fechar");
        closeItem.addActionListener(_ -> frame.dispose());
        menu.add(closeItem);
        menu.show(imageLabel, e.getX(), e.getY());
    }

    private void openChatbot() {
        ChatGUI chatGUI = new ChatGUI(); // Cria uma nova instância de ChatGUI
        chatGUI.setVisible(true); // Torne a janela visível
    }

    private void openLink() {
        String link = JOptionPane.showInputDialog(frame, "Insira o link:");
        if (link != null && !link.isEmpty()) {
            try {
                Desktop.getDesktop().browse(new URI(link));
            } catch (URISyntaxException | IOException e) {
                System.out.println("Ocorreu o erro " + e);
            }
        }
    }

    private void openFolder() {
        try {
            File folder = new File(Objects.requireNonNull(getClass().getResource(".")).toURI());
            Desktop.getDesktop().open(folder);
        } catch (URISyntaxException | IOException e) {
            System.out.println("Ocorreu o erro " + e);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        Image resultingImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PetApp::new);
    }
}