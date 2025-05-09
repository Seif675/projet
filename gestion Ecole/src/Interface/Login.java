package Interface;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;
import dao.UtilisateurConcret;

public class Login extends JFrame {
    private JPanel loginPanel;
    private JLabel titleLabel;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeCombo;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel typeLabel;
    private JTextField usernameField;
    private JButton loginButton;
    private JButton logoutButton;
    private JLabel imageLabel;
    private Image backgroundImage;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/project";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Seifoun123@";

    public Login() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Système de Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            backgroundImage = ImageIO.read(new File("C:/Users/seifo/Downloads/UfPy6ejOF8wzcaX-ui_P6.png"));
            Image robotImage = ImageIO.read(new File("C:/Users/seifo/Downloads/ChatGPT_Image_6_mai_2025__20_35_25-removebg-preview.png"));
            robotImage = robotImage.getScaledInstance(280, 280, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(robotImage));
        } catch (Exception e) {
            backgroundImage = null;
            imageLabel = new JLabel("Image non chargée");
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(240, 240, 240));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        titleLabel = new JLabel("CONNEXION");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
        titleLabel.setForeground(new Color(12, 69, 107));

        usernameLabel = new JLabel("Nom d'utilisateur:");
        usernameLabel.setFont(labelFont);

        usernameField = new JTextField();
        usernameField.setFont(fieldFont);
        usernameField.setPreferredSize(new Dimension(200, 30));

        passwordLabel = new JLabel("Mot de passe:");
        passwordLabel.setFont(labelFont);

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setPreferredSize(new Dimension(200, 30));

        typeLabel = new JLabel("Type:");
        typeLabel.setFont(labelFont);

        userTypeCombo = new JComboBox<>(new String[]{"Enseignant", "Administrateur", "Étudiant"});
        userTypeCombo.setFont(fieldFont);
        userTypeCombo.setPreferredSize(new Dimension(200, 30));

        loginButton = new JButton("SE CONNECTER");
        loginButton.setBackground(new Color(12, 69, 107));
        loginButton.setForeground(Color.black);
        loginButton.setFont(buttonFont);
        loginButton.setPreferredSize(new Dimension(150, 35));
        loginButton.addActionListener(e -> connect());

        logoutButton = new JButton("SE DÉCONNECTER");
        logoutButton.setBackground(new Color(150, 40, 40));
        logoutButton.setForeground(Color.black);
        logoutButton.setFont(buttonFont);
        logoutButton.setPreferredSize(new Dimension(150, 35));
        logoutButton.addActionListener(e -> System.exit(0));

        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        loginPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(userTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(logoutButton);
        loginPanel.add(buttonPanel, gbc);

        JPanel contentPanel = new JPanel(new BorderLayout(30, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(imageLabel, BorderLayout.WEST);
        contentPanel.add(loginPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private void connect() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) userTypeCombo.getSelectedItem();

        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez remplir tous les champs", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM Utilisateur WHERE login = ? AND password = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.toLowerCase());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UtilisateurConcret utilisateur = new UtilisateurConcret(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("login"),
                    "",
                    rs.getString("role")
                );
                
                JOptionPane.showMessageDialog(this,
                    "Connexion réussie!\nBienvenue " + utilisateur.getPrenom() + " " + utilisateur.getNom(),
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                openDashboard(utilisateur);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Identifiants incorrects ou rôle non autorisé",
                    "Échec de connexion",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur de connexion à la base de données: " + ex.getMessage(),
                "Erreur technique",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openDashboard(UtilisateurConcret utilisateur) {
        switch (utilisateur.getRole().toLowerCase()) {
            case "administrateur":
                new AdminMangement().setVisible(true);
                break;
            case "enseignant":
                new TeacherManagementUI().setVisible(true);
                break;
            case "étudiant":
                new StudentManagementUI().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                    "Rôle non reconnu",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
        }
        this.dispose();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}