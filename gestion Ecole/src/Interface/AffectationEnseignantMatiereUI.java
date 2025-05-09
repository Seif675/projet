package Interface;

import dao.EnseignantDAO;
import dao.MatiereDAO;
import dao.EnseignantMatiereDAO;
import model.Enseignant;
import model.Matiere;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AffectationEnseignantMatiereUI extends JFrame {

    private JComboBox<String> comboEnseignants, comboMatieres;
    private JButton btnAffecter, btnDesaffecter, btnFermer;
    private JTable tableAffectations;
    private DefaultTableModel tableModelAffectations;

    private EnseignantDAO enseignantDAO;
    private MatiereDAO matiereDAO;
    private EnseignantMatiereDAO enseignantMatiereDAO;

    private List<Enseignant> enseignantsList;
    private List<Matiere> matieresList;

    public AffectationEnseignantMatiereUI() {
        this.enseignantDAO = new EnseignantDAO();
        this.matiereDAO = new MatiereDAO();
        this.enseignantMatiereDAO = new EnseignantMatiereDAO();

        setTitle("Affectation des Matières aux Enseignants");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadEnseignantsAndMatieres();
        loadAffectations();
    }

    private void initComponents() {
        // === PANEL DE SELECTION (Haut) ===
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboEnseignants = new JComboBox<>();
        comboMatieres = new JComboBox<>();
        btnAffecter = new JButton("Affecter Matière");
        btnDesaffecter = new JButton("Désaffecter Matière");
        btnFermer = new JButton("Fermer");

        gbc.gridx = 0; gbc.gridy = 0; selectionPanel.add(new JLabel("Enseignant:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4; selectionPanel.add(comboEnseignants, gbc);
        gbc.gridx = 0; gbc.gridy = 1; selectionPanel.add(new JLabel("Matière:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; selectionPanel.add(comboMatieres, gbc);
        
        JPanel buttonsCrudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsCrudPanel.add(btnAffecter);
        buttonsCrudPanel.add(btnDesaffecter);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; selectionPanel.add(buttonsCrudPanel, gbc);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(selectionPanel, BorderLayout.CENTER);
        
        JPanel closeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closeButtonPanel.add(btnFermer);
        topPanel.add(closeButtonPanel, BorderLayout.SOUTH);

        // === PANEL TABLE DES AFFECTATIONS (Centre) ===
        String[] cols = {"ID Enseignant", "Nom Enseignant", "ID Matière", "Nom Matière"};
        tableModelAffectations = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableAffectations = new JTable(tableModelAffectations);
        JScrollPane scrollPane = new JScrollPane(tableAffectations);

        // Listeners
        btnAffecter.addActionListener(e -> affecterMatiere());
        btnDesaffecter.addActionListener(e -> desaffecterMatiere());
        btnFermer.addActionListener(e -> dispose());

        comboEnseignants.addActionListener(e -> loadAffectationsForSelectedEnseignant());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadEnseignantsAndMatieres() {
        enseignantsList = enseignantDAO.getAllEnseignants();
        matieresList = matiereDAO.getAllMatieres();

        comboEnseignants.removeAllItems();
        for (Enseignant ens : enseignantsList) {
            comboEnseignants.addItem(ens.getId() + ": " + ens.getPrenom() + " " + ens.getNom());
        }

        comboMatieres.removeAllItems();
        for (Matiere mat : matieresList) {
            comboMatieres.addItem(mat.getId() + ": " + mat.getNom() + " (" + mat.getCode() + ")");
        }
    }

    private void loadAffectations() {
        tableModelAffectations.setRowCount(0);
        List<model.EnseignantMatiere> affectations = enseignantMatiereDAO.getAllEnseignantMatieres();
        
        for (model.EnseignantMatiere aff : affectations) {
            Enseignant enseignant = findEnseignantById(aff.getEnseignantId());
            Matiere matiere = findMatiereById(aff.getMatiereId());
            if (enseignant != null && matiere != null) {
                tableModelAffectations.addRow(new Object[]{
                        enseignant.getId(),
                        enseignant.getPrenom() + " " + enseignant.getNom(),
                        matiere.getId(),
                        matiere.getNom()
                });
            }
        }
    }
    
    private void loadAffectationsForSelectedEnseignant() {
        int selectedEnseignantIndex = comboEnseignants.getSelectedIndex();
        if (selectedEnseignantIndex < 0 || enseignantsList == null || enseignantsList.isEmpty()) {
            loadAffectations(); // Load all if no specific teacher or list is empty
            return;
        }

        Enseignant selectedEnseignant = enseignantsList.get(selectedEnseignantIndex);
        tableModelAffectations.setRowCount(0);
        List<Matiere> matieresAffectees = enseignantMatiereDAO.getMatieresByEnseignantId(selectedEnseignant.getId());

        for (Matiere mat : matieresAffectees) {
            tableModelAffectations.addRow(new Object[]{
                    selectedEnseignant.getId(),
                    selectedEnseignant.getPrenom() + " " + selectedEnseignant.getNom(),
                    mat.getId(),
                    mat.getNom()
            });
        }
    }

    private Enseignant findEnseignantById(int id) {
        return enseignantsList.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    private Matiere findMatiereById(int id) {
        return matieresList.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }

    private void affecterMatiere() {
        int selectedEnseignantIndex = comboEnseignants.getSelectedIndex();
        int selectedMatiereIndex = comboMatieres.getSelectedIndex();

        if (selectedEnseignantIndex < 0 || selectedMatiereIndex < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enseignant et une matière.", "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Enseignant enseignant = enseignantsList.get(selectedEnseignantIndex);
        Matiere matiere = matieresList.get(selectedMatiereIndex);

        if (enseignantMatiereDAO.affecterMatiereAEnseignant(enseignant.getId(), matiere.getId())) {
            JOptionPane.showMessageDialog(this, "Matière affectée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            loadAffectationsForSelectedEnseignant(); // Refresh table for current teacher or all
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l\_affectation ou l\_affectation existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desaffecterMatiere() {
        int selectedRow = tableAffectations.getSelectedRow();
        if (selectedRow < 0) {
             int selectedEnseignantIndex = comboEnseignants.getSelectedIndex();
             int selectedMatiereIndex = comboMatieres.getSelectedIndex();
             if (selectedEnseignantIndex < 0 || selectedMatiereIndex < 0) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une affectation dans le tableau ou un enseignant et une matière à désaffecter.", "Sélection requise", JOptionPane.WARNING_MESSAGE);
                return;
             }
             Enseignant enseignant = enseignantsList.get(selectedEnseignantIndex);
             Matiere matiere = matieresList.get(selectedMatiereIndex);
             
             int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir désaffecter la matière '" + matiere.getNom() + "' de l'enseignant '" + enseignant.getPrenom() + " " + enseignant.getNom() + "'?",
                    "Confirmation de désaffectation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (enseignantMatiereDAO.desaffecterMatiereDeEnseignant(enseignant.getId(), matiere.getId())) {
                    JOptionPane.showMessageDialog(this, "Matière désaffectée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadAffectationsForSelectedEnseignant();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la désaffectation.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }

        } else {
            int enseignantId = (int) tableModelAffectations.getValueAt(selectedRow, 0);
            String enseignantNom = tableModelAffectations.getValueAt(selectedRow, 1).toString();
            int matiereId = (int) tableModelAffectations.getValueAt(selectedRow, 2);
            String matiereNom = tableModelAffectations.getValueAt(selectedRow, 3).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir désaffecter la matière '" + matiereNom + "' de l'enseignant '" + enseignantNom + "'?",
                    "Confirmation de désaffectation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (enseignantMatiereDAO.desaffecterMatiereDeEnseignant(enseignantId, matiereId)) {
                    JOptionPane.showMessageDialog(this, "Matière désaffectée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadAffectationsForSelectedEnseignant(); // Refresh table for current teacher or all
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la désaffectation.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Important: Pour que cette interface fonctionne, la table Enseignant_Matiere doit exister dans la base de données.
            // CREATE TABLE Enseignant_Matiere (enseignant_id INT, matiere_id INT, PRIMARY KEY (enseignant_id, matiere_id), FOREIGN KEY (enseignant_id) REFERENCES Utilisateur(id) ON DELETE CASCADE, FOREIGN KEY (matiere_id) REFERENCES Matiere(id) ON DELETE CASCADE);
            // Note: La FK enseignant_id devrait pointer vers Utilisateur(id) car Enseignant.utilisateur_id est la PK de Enseignant.
            new AffectationEnseignantMatiereUI().setVisible(true);
        });
    }
}


