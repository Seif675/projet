package vue;

import dao.EtudiantDao;
import dao.SingletonConnection;
import modele.Etudiant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EtudiantInterface extends JFrame {
    private int etudiantId;
    private EtudiantDao etudiantDao;
    private JTabbedPane tabbedPane;
    private JTable tableMatieres, tableNotes, tableEnseignants;
    private DefaultTableModel modelMatieres, modelNotes, modelEnseignants;

    public EtudiantInterface(int etudiantId) {
        this.etudiantId = etudiantId;
        this.etudiantDao = new EtudiantDao();
        initUI();
        chargerDonnees();
    }

    private void initUI() {
        setTitle("Espace Étudiant - " + etudiantId);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Onglet Matières
        modelMatieres = new DefaultTableModel(new String[]{"Code", "Matière", "Coefficient", "Volume Horaire"}, 0);
        tableMatieres = new JTable(modelMatieres);
        tabbedPane.addTab("Mes Matières", new JScrollPane(tableMatieres));

        // Onglet Notes
        modelNotes = new DefaultTableModel(new String[]{"Matière", "Type", "Note", "Coefficient"}, 0);
        tableNotes = new JTable(modelNotes);
        tabbedPane.addTab("Mes Notes", new JScrollPane(tableNotes));

        // Onglet Enseignants
        modelEnseignants = new DefaultTableModel(new String[]{"Nom", "Prénom", "Email", "Spécialité"}, 0);
        tableEnseignants = new JTable(modelEnseignants);
        tabbedPane.addTab("Mes Enseignants", new JScrollPane(tableEnseignants));

        // Onglet Profil
        JPanel profilPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        profilPanel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        tabbedPane.addTab("Mon Profil", profilPanel);

        add(tabbedPane);
    }

    private void chargerDonnees() {
        chargerMatieres();
        chargerNotes();
        chargerEnseignants();
        chargerProfil();
    }

    private void chargerMatieres() {
        modelMatieres.setRowCount(0);
        List<Object[]> matieres = etudiantDao.obtenirMatieresInscrites(etudiantId);

        for (Object[] matiere : matieres) {
            modelMatieres.addRow(matiere);
        }
    }

    private void chargerNotes() {
        modelNotes.setRowCount(0);
        List<Object[]> notes = etudiantDao.obtenirNotesEtMatieres(etudiantId);

        for (Object[] note : notes) {
            modelNotes.addRow(note);
        }
    }

    private void chargerEnseignants() {
        modelEnseignants.setRowCount(0);
        List<Object[]> enseignants = etudiantDao.obtenirEnseignants(etudiantId);

        for (Object[] enseignant : enseignants) {
            modelEnseignants.addRow(enseignant);
        }
    }

    private void chargerProfil() {
        Etudiant etudiant = etudiantDao.trouverParId(etudiantId);
        if (etudiant != null) {
            JPanel profilPanel = (JPanel) tabbedPane.getComponentAt(3);
            JScrollPane scrollPane = (JScrollPane) profilPanel.getComponent(0);
            JPanel infoPanel = (JPanel) scrollPane.getViewport().getView();

            infoPanel.removeAll();

            infoPanel.add(new JLabel("ID:"));
            infoPanel.add(new JLabel(String.valueOf(etudiant.getId())));

            infoPanel.add(new JLabel("Nom:"));
            infoPanel.add(new JLabel(etudiant.getNom()));

            infoPanel.add(new JLabel("Prénom:"));
            infoPanel.add(new JLabel(etudiant.getPrenom()));

            infoPanel.add(new JLabel("Email:"));
            infoPanel.add(new JLabel(etudiant.getEmail()));

            infoPanel.add(new JLabel("Niveau:"));
            infoPanel.add(new JLabel(etudiant.getNiveau()));

            infoPanel.add(new JLabel("Filière:"));
            infoPanel.add(new JLabel(etudiant.getFiliere()));

            infoPanel.add(new JLabel("Moyenne:"));
            infoPanel.add(new JLabel(String.valueOf(etudiant.getMoyenne())));

            infoPanel.add(new JLabel("Mention:"));
            infoPanel.add(new JLabel(etudiant.getMention()));

            infoPanel.revalidate();
            infoPanel.repaint();
        }
    }

    private void filtrerTableaux(String texte) {
        TableRowSorter<DefaultTableModel> sorterMatieres = new TableRowSorter<>(modelMatieres);
        tableMatieres.setRowSorter(sorterMatieres);
        sorterMatieres.setRowFilter(RowFilter.regexFilter("(?i)" + texte));

        TableRowSorter<DefaultTableModel> sorterNotes = new TableRowSorter<>(modelNotes);
        tableNotes.setRowSorter(sorterNotes);
        sorterNotes.setRowFilter(RowFilter.regexFilter("(?i)" + texte));

        TableRowSorter<DefaultTableModel> sorterEnseignants = new TableRowSorter<>(modelEnseignants);
        tableEnseignants.setRowSorter(sorterEnseignants);
        sorterEnseignants.setRowFilter(RowFilter.regexFilter("(?i)" + texte));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new EtudiantInterface(6).setVisible(true); // ID étudiant de test
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
