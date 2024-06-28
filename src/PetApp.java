import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class PetApp {

    private final JFrame frame;
    private JLabel imageLabel;
    private Point startDragPoint;
    private final Map<String, String> linksFavoritos;

    public PetApp() {
        frame = new JFrame("Pet App");
        frame.setUndecorated(true);
        frame.setSize(200,250);
        frame.setAlwaysOnTop(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setLocationRelativeTo(null);

        linksFavoritos = carregarLinksDoArquivo();

        try {
            frame.setIconImage(ImageIO.read(new File("./images/chatbot_normal.png")));
        } catch (Exception e) {
            System.out.println("Erro ao carregar ícone: " + e.getMessage());
        }

        try {
            BufferedImage image = ImageIO.read(new File("images/chat.png"));
            image = resizeImage(image);
            imageLabel = new JLabel(new ImageIcon(image));
        } catch (IOException e) {
            System.out.println("Ocorreu o erro " + e);
        }

        imageLabel.setBorder(BorderFactory.createEmptyBorder());
        frame.add(imageLabel);

        // Arrastar a janela
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startDragPoint = e.getPoint();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                // Carrega a imagem de hover (chatbot_normal.png)
                try {
                    BufferedImage hoverImage = ImageIO.read(new File("images/chatbot_normal.png"));
                    hoverImage = resizeImage(hoverImage);
                    imageLabel.setIcon(new ImageIcon(hoverImage));
                } catch (IOException ex) {
                    System.err.println("Erro ao carregar a imagem de hover: " + ex);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // Volta para a imagem inicial (chat.png)
                try {
                    BufferedImage initialImage = ImageIO.read(new File("images/chat.png"));
                    initialImage = resizeImage(initialImage);
                    imageLabel.setIcon(new ImageIcon(initialImage));
                } catch (IOException ex) {
                    System.err.println("Erro ao carregar a imagem inicial: " + ex);
                }
            }
        });
        imageLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - startDragPoint.x;
                int dy = e.getY() - startDragPoint.y;
                frame.setLocation(frame.getX() + dx, frame.getY() + dy);
            }
        });

        // Clicks do mouse
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showOptionsMenu(e);
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    showDeleteMenu(e);
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getClickCount() == 2) {
                    adicionarOuEditarFavorito();
                    }
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

        if (!linksFavoritos.isEmpty()) {
            JMenu favoritosMenu = new JMenu("Favoritos");
            for (Map.Entry<String, String> entry : linksFavoritos.entrySet()) {
                JMenuItem subItem = new JMenuItem(entry.getKey());
                subItem.addActionListener(_ -> openPathFavorites(entry.getValue()));
                favoritosMenu.add(subItem);
            }
            menu.add(favoritosMenu);
        }

        menu.addSeparator();

        JMenuItem closeItem = new JMenuItem("Fechar");
        closeItem.addActionListener(_ -> frame.dispose());
        menu.add(closeItem);

        menu.show(imageLabel, e.getX(), e.getY());
    }

    private void openChatbot() {
        ChatGUI chatGUI = new ChatGUI();
        chatGUI.setVisible(true);
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

    // Adiciona ou edita um favorito
    private void adicionarOuEditarFavorito() {
        while (true) {
            String[] inputs = obterInputsDoUsuario();
            if (inputs == null) {
                break; // Usuário cancelou
            }

            String nome = inputs[0];
            String caminho = inputs[1];

            if (nome.isEmpty() || caminho.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nome e caminho não podem estar vazios.");
                continue;
            }

            linksFavoritos.put(nome, caminho);
            if (salvarLinksNoArquivo(linksFavoritos)) {
                JOptionPane.showMessageDialog(null, "Link adicionado/editado com sucesso!");
            }
        }
    }

    // Obter inputs do usuário
    private String[] obterInputsDoUsuario() {
        String[] inputs = new String[2];
        Object[] campos = {
                "Nome:", new JTextField(),
                "Caminho:", new JTextField()
        };
        int resultado = JOptionPane.showConfirmDialog(null, campos, "Inserir Favorito", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            inputs[0] = ((JTextField) campos[1]).getText();
            inputs[1] = ((JTextField) campos[3]).getText();
            return inputs;
        } else {
            return null;
        }
    }

    // Carregar links do arquivo
    private Map<String, String> carregarLinksDoArquivo() {
        Map<String, String> links = new HashMap<>();
        try (BufferedReader leitor = new BufferedReader(new FileReader("scripts/Links.txt"))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 2) {
                    links.put(partes[0].trim(), partes[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao abrir o arquivo: " + e.getMessage());
        }
        return links;
    }

    // Salvar links no arquivo
    private boolean salvarLinksNoArquivo(Map<String, String> links) {
        try (PrintWriter escritor = new PrintWriter("scripts/Links.txt")) {
            for (Map.Entry<String, String> entry : links.entrySet()) {
                escritor.println(entry.getKey() + ";" + entry.getValue());
            }
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar os links: " + e.getMessage());
            return false;
        }
    }

    private void openPathFavorites(String pathOrLink) {
        if (pathOrLink != null && !pathOrLink.isEmpty()) {
            try {
                if (pathOrLink.startsWith("http") || pathOrLink.startsWith("https")) {
                    Desktop.getDesktop().browse(new URI(pathOrLink));
                } else {
                    File fileOrFolder = new File(pathOrLink);
                    if (fileOrFolder.exists()) {
                        Desktop.getDesktop().open(fileOrFolder);
                    } else {
                        System.out.println("Arquivo ou pasta não encontrado: " + pathOrLink);
                    }
                }
            } catch (URISyntaxException | IOException e) {
                System.out.println("Ocorreu o erro: " + e.getMessage());
            }
        }
    }

    // Menu de exclusão de favorito
    private void showDeleteMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();

        if (!linksFavoritos.isEmpty()) {
            JMenu favoritosMenu = new JMenu("Deletar Favoritos");
            for (Map.Entry<String, String> entry : linksFavoritos.entrySet()) {
                JMenuItem subItem = getjMenuItem(entry);
                favoritosMenu.add(subItem);
            }
            menu.add(favoritosMenu);
        }
        menu.show(imageLabel, e.getX(), e.getY());
    }

    private JMenuItem getjMenuItem(Map.Entry<String, String> entry) {
        JMenuItem subItem = new JMenuItem(entry.getKey());
        subItem.addActionListener(event -> {
            String nome = ((JMenuItem) event.getSource()).getText();
            if (JOptionPane.showConfirmDialog(null, "Deseja realmente deletar o favorito " + nome + "?",
                    "Confirmar Exclusão", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                linksFavoritos.remove(nome);
                salvarLinksNoArquivo(linksFavoritos);
            }
        });
        return subItem;
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        Image resultingImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);

        try (BufferedReader leitor = new BufferedReader(new FileReader("scripts/tamanho.txt"))) {
            String linha = leitor.readLine();
            String[] partes = linha.split(";");
            resultingImage = originalImage.getScaledInstance(Integer.parseInt(partes[0].trim()), Integer.parseInt(partes[1].trim()), Image.SCALE_SMOOTH);
            outputImage = new BufferedImage(Integer.parseInt(partes[0].trim()), Integer.parseInt(partes[1].trim()), BufferedImage.TYPE_INT_ARGB);
        } catch(IOException e) {
            System.out.println("Ocorreu o erro: " + e);
        }
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PetApp::new);
    }
}