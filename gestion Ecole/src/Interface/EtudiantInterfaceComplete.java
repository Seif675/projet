package Interface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class EtudiantInterfaceComplete extends JFrame {

    private JTable tableNotes, tableMatieres, tableEnseignants;
    private DefaultTableModel modelNotes, modelMatieres, modelEnseignants;
    private JTextField searchMatiereField, searchEnseignantField;

    public EtudiantInterfaceComplete(int utilisateurId) {
        setTitle("Espace Étudiant Complet");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Onglet 1 : Notes
        modelNotes = new DefaultTableModel(new String[]{"Matière", "Note TP", "Note DS", "Note Examen"}, 0);
        tableNotes = new JTable(modelNotes);
        tabbedPane.addTab("Mes Notes", new JScrollPane(tableNotes));

        // Onglet 2 : Matières
        JPanel matierePanel = new JPanel(new BorderLayout());
        searchMatiereField = new JTextField();
        modelMatieres = new DefaultTableModel(new String[]{"Code", "Nom", "Volume Horaire"}, 0);
        tableMatieres = new JTable(modelMatieres);

        searchMatiereField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterTable(searchMatiereField.getText(), tableMatieres, modelMatieres);
            }
        });

        matierePanel.add(new JLabel("Rechercher une matière:"), BorderLayout.NORTH);
        matierePanel.add(searchMatiereField, BorderLayout.NORTH);
        matierePanel.add(new JScrollPane(tableMatieres), BorderLayout.CENTER);
        tabbedPane.addTab("Matières", matierePanel);

        // Onglet 3 : Enseignants
        JPanel enseignantPanel = new JPanel(new BorderLayout());
        searchEnseignantField = new JTextField();
        modelEnseignants = new DefaultTableModel(new String[]{"Nom", "Prénom", "Email"}, 0);
        tableEnseignants = new JTable(modelEnseignants);

        searchEnseignantField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterTable(searchEnseignantField.getText(), tableEnseignants, modelEnseignants);
            }
        });

        enseignantPanel.add(new JLabel("Rechercher un enseignant:"), BorderLayout.NORTH);
        enseignantPanel.add(searchEnseignantField, BorderLayout.NORTH);
        enseignantPanel.add(new JScrollPane(tableEnseignants), BorderLayout.CENTER);
        tabbedPane.addTab("Enseignants", enseignantPanel);

        // Onglet 4 : Mon Profil
        JPanel profilPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setIcon(new ImageIcon(new ImageIcon("profil.jpg").getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));

        profilPanel.add(photoLabel, BorderLayout.NORTH);
        profilPanel.add(infoPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Mon Profil", profilPanel);

        add(tabbedPane);

        chargerNotes(utilisateurId);
        chargerMatieres();
        chargerEnseignants();
        chargerProfil(utilisateurId, infoPanel);

        setVisible(true);
    }

    private void filterTable(String searchText, JTable table, DefaultTableModel model) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
    }

    private void chargerNotes(int utilisateurId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ton_projet", "root", "");
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT m.nom, n.note_tp, n.note_ds, n.note_examen
                FROM Inscription i
                JOIN Matiere m ON i.matiere_id = m.id
                JOIN Note n ON n.inscription_id = i.id
                WHERE i.utilisateur_id = ?
            """)) {

            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelNotes.addRow(new Object[]{
                        rs.getString("nom"),
                        rs.getFloat("note_tp"),
                        rs.getFloat("note_ds"),
                        rs.getFloat("note_examen")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerMatieres() {
        // Exemples statiques à remplacer par une requête
        modelMatieres.addRow(new Object[]{"MAT101", "Mathématiques", "60h"});
        modelMatieres.addRow(new Object[]{"INF201", "Programmation", "45h"});
        modelMatieres.addRow(new Object[]{"PHY301", "Physique", "40h"});
    }

    private void chargerEnseignants() {
        // Exemples statiques à remplacer par une requête
        modelEnseignants.addRow(new Object[]{"Dupont", "Marc", "marc.dupont@univ.fr"});
        modelEnseignants.addRow(new Object[]{"Lemoine", "Sophie", "sophie.lemoine@univ.fr"});
    }

    private void chargerProfil(int utilisateurId, JPanel infoPanel) {
        String query = """
            SELECT u.nom, u.prenom, u.email, e.niveau, e.filiere, e.moyenne_generale, e.mention
            FROM Utilisateur u
            JOIN Etudiant e ON u.id = e.utilisateur_id
            WHERE u.id = ?
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ton_projet", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                infoPanel.add(new JLabel("Nom :"));
                infoPanel.add(new JLabel(rs.getString("nom")));

                infoPanel.add(new JLabel("Prénom :"));
                infoPanel.add(new JLabel(rs.getString("prenom")));

                infoPanel.add(new JLabel("Email :"));
                infoPanel.add(new JLabel(rs.getString("email")));

                infoPanel.add(new JLabel("Niveau :"));
                infoPanel.add(new JLabel(rs.getString("niveau")));

                infoPanel.add(new JLabel("Filière :"));
                infoPanel.add(new JLabel(rs.getString("filiere")));

                infoPanel.add(new JLabel("Moyenne Générale :"));
                infoPanel.add(new JLabel(String.valueOf(rs.getFloat("moyenne_generale"))));

                infoPanel.add(new JLabel("Mention :"));
                infoPanel.add(new JLabel(rs.getString("mention")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EtudiantInterfaceComplete(3)); // utilisateur_id de l'étudiant
    }
}
