import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;

public class PetApp {

    private final JFrame frame;
    private JLabel imageLabel;
    private Point startDragPoint;

    public PetApp() {
        frame = new JFrame("Pet App");
        frame.setUndecorated(true); // Remove a moldura da janela
        frame.setSize(200, 250); // Define as dimensões da imagem
        frame.setAlwaysOnTop(true); // Mantém o app em foco
        frame.setBackground(new Color(0, 0, 0, 0)); // Transparência total (0 = totalmente transparente)
        frame.setLocationRelativeTo(null); // Centraliza a janela

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
                    System.out.println("Clicou");
                }
            }
        });

        frame.setVisible(true);
    }

    private void showOptionsMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem openChatbot = new JMenuItem("Chatbot PCP");
        openChatbot.addActionListener(_ -> openChatbot());
        menu.add(openChatbot);
        JMenuItem openSAP = new JMenuItem("SAP - Acesso Interno");
        openSAP.addActionListener(_ -> openLink("https://www.myweg.net/irj/portal?NavigationTarget=pcd:portal_content/net.weg.folder.weg/net.weg.folder.core/net.weg.folder.roles/net.weg.role.ecc/net.weg.iview.ecc"));
        menu.add(openSAP);
        JMenuItem openIntranet = new JMenuItem("Intranet WEG");
        openIntranet.addActionListener(_ -> openLink("https://weg365.sharepoint.com/teams/br/default.aspx"));
        menu.add(openIntranet);
        JMenuItem openSharepoint = new JMenuItem("Sharepoint Departamento");
        openSharepoint.addActionListener(_ -> openLink("https://intranet.weg.net/br/energia-wm/pcp/SitePages/P%C3%A1gina%20Principal.aspx"));
        menu.add(openSharepoint);
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

    private void openLink(String link) {
        if (link != null && !link.isEmpty()) {
            try {
                Desktop.getDesktop().browse(new URI(link));
            } catch (URISyntaxException | IOException e) {
                System.out.println("Ocorreu o erro " + e);
            }
        }
    }

//    Função para abrir pasta = private void openFolder() {
//        try {
//            File folder = new File(Objects.requireNonNull(getClass().getResource(".")).toURI());
//            Desktop.getDesktop().open(folder);
//        } catch (URISyntaxException | IOException e) {
//            System.out.println("Ocorreu o erro " + e);
//        }
//    }

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