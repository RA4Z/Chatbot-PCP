import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PetApp {

    private final JFrame frame;
    private JLabel imageLabel;
    private Point startDragPoint;
    private final Map<String, String> linksFavoritos;

    public PetApp() {
        frame = new JFrame("Pet App");
        frame.setUndecorated(true); // Remove a moldura da janela
        frame.setSize(200, 250); // Define as dimensões da imagem
        frame.setAlwaysOnTop(true); // Mantém o app em foco
        frame.setBackground(new Color(0, 0, 0, 0)); // Transparência total (0 = totalmente transparente)
        frame.setLocationRelativeTo(null); // Centraliza a janela

        String LinksFile = "scripts/Links.txt";
        linksFavoritos = new HashMap<>();

        try (BufferedReader leitor = new BufferedReader(new FileReader(LinksFile))) {

            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 2) {
                    linksFavoritos.put(partes[0].trim(), partes[1].trim()); // Adiciona ao dicionário
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao abrir o arquivo: " + e.getMessage());
        }


        try {
            frame.setIconImage(ImageIO.read(new File("./images/chatbot_normal.png")));
        } catch (Exception e) {
            System.out.println("Erro ao carregar ícone: " + e.getMessage());
        }

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
                    while (true) {
                        String[] inputs = obterInputsDoUsuario(linksFavoritos);
                        if (inputs == null) {
                            break; // Usuário cancelou
                        }

                        String nome = inputs[0];
                        String caminho = inputs[1];

                        if (nome.isEmpty() || caminho.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Nome e caminho não podem estar vazios.");
                            continue; // Volta para o início do loop
                        }

                        linksFavoritos.put(nome, caminho);
                        if (salvarLinksNoArquivo(linksFavoritos)) { // Verifica o retorno da função
                            JOptionPane.showMessageDialog(null, "Link adicionado/editado com sucesso!");
                        }
                    }
                }
            }
        });

        frame.setVisible(true);
    }

    private void showOptionsMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();

        // Item "Chatbot PCP"
        JMenuItem openChatbot = new JMenuItem("Chatbot PCP");
        openChatbot.addActionListener(_ -> openChatbot());
        menu.add(openChatbot);

        // Item "SAP - Acesso Interno"
        JMenuItem openSAP = new JMenuItem("SAP - Acesso Interno");
        openSAP.addActionListener(_ -> openLink("https://www.myweg.net/irj/portal?NavigationTarget=pcd:portal_content/net.weg.folder.weg/net.weg.folder.core/net.weg.folder.roles/net.weg.role.ecc/net.weg.iview.ecc"));
        menu.add(openSAP);

        // Item "Intranet WEG"
        JMenuItem openIntranet = new JMenuItem("Intranet WEG");
        openIntranet.addActionListener(_ -> openLink("https://weg365.sharepoint.com/teams/br/default.aspx"));
        menu.add(openIntranet);

        // Item "Sharepoint Departamento"
        JMenuItem openSharepoint = new JMenuItem("Sharepoint Departamento");
        openSharepoint.addActionListener(_ -> openLink("https://intranet.weg.net/br/energia-wm/pcp/SitePages/P%C3%A1gina%20Principal.aspx"));
        menu.add(openSharepoint);

        if (!linksFavoritos.isEmpty()) { // Verifica se o dicionário não está vazio
            JMenu outrosRecursosMenu = new JMenu("Favoritos");

            for (Map.Entry<String, String> entry : linksFavoritos.entrySet()) {
                JMenuItem subItem = new JMenuItem(entry.getKey());
                subItem.addActionListener(_ -> openPathFavorites(entry.getValue()));
                outrosRecursosMenu.add(subItem);
            }

            // Adiciona o item "Outros Recursos" ao menu principal
            menu.add(outrosRecursosMenu);
        }

        // Separador
        menu.addSeparator();

        // Item "Fechar"
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

    // Função para obter os inputs do usuário
    public static String[] obterInputsDoUsuario(Map<String, String> links) {
        String[] inputs = new String[2];
        Object[] campos = {
                "Nome:", new JTextField(),
                "Caminho:", new JTextField()
        };
        int resultado = JOptionPane.showConfirmDialog(null, campos, "Inserir Favorito", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            inputs[0] = ((JTextField) campos[1]).getText();
            inputs[1] = ((JTextField) campos[3]).getText();
            // Salvar aqui após obter os inputs:
            salvarLinksNoArquivo(links);
            return inputs;
        } else {
            return null; // Usuário cancelou
        }
    }

    // Salva o dicionário de links no arquivo
    public static boolean salvarLinksNoArquivo(Map<String, String> links) {
        try (PrintWriter escritor = new PrintWriter("scripts/Links.txt")) {
            for (Map.Entry<String, String> entry : links.entrySet()) {
                escritor.println(entry.getKey() + ";" + entry.getValue());
            }
            return true; // Salvou com sucesso
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar os links: " + e.getMessage());
            return false; // Erro ao salvar
        }
    }

    private void openPathFavorites(String pathOrLink) {
        if (pathOrLink != null && !pathOrLink.isEmpty()) {
            try {
                if (pathOrLink.startsWith("http") || pathOrLink.startsWith("https")) {
                    // É um link
                    Desktop.getDesktop().browse(new URI(pathOrLink));
                } else {
                    // É um caminho de arquivo
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