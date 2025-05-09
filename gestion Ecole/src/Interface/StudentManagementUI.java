package Interface;

import dao.EtudiantDAO;
import modele.Etudiant;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class StudentManagementUI extends JFrame {
    private JTextField tfId, tfNom, tfPrenom, tfLogin, tfPassword, tfSearch, tfEmail;
    private JComboBox<String> cbNiveau, cbFiliere;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnRemove, btnSearch;
    private EtudiantDAO etudiantDAO;

    public StudentManagementUI() {
        initializeDAO();
        setupUI();
        setupListeners();
        loadInitialData();
    }

    private void initializeDAO() {
        try {
            etudiantDAO = new EtudiantDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de connexion à la base de données: " + e.getMessage(), 
                "Erreur Critique", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void setupUI() {
        setTitle("Gestion des Étudiants");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Left Panel - Form
        JPanel leftPanel = createFormPanel();
        add(leftPanel, BorderLayout.WEST);

        // Right Panel - Table and Search
        JPanel rightPanel = createTablePanel();
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(400, 600));

        // Form fields
        panel.add(new JLabel("ID:"));
        tfId = new JTextField();
        tfId.setEditable(false);
        panel.add(tfId);

        panel.add(new JLabel("Nom:"));
        tfNom = new JTextField();
        panel.add(tfNom);

        panel.add(new JLabel("Prénom:"));
        tfPrenom = new JTextField();
        panel.add(tfPrenom);

        panel.add(new JLabel("Login:"));
        tfLogin = new JTextField();
        panel.add(tfLogin);

        panel.add(new JLabel("Password:"));
        tfPassword = new JTextField();
        panel.add(tfPassword);

        panel.add(new JLabel("Email:"));
        tfEmail = new JTextField();
        panel.add(tfEmail);

        panel.add(new JLabel("Niveau:"));
        cbNiveau = new JComboBox<>(new String[]{"L1", "L2", "L3", "M1", "M2", "cpi1", "cpi2"});
        panel.add(cbNiveau);

        panel.add(new JLabel("Filière:"));
        cbFiliere = new JComboBox<>(new String[]{"Informatique", "Mathématiques", "Physique", "Chimie"});
        panel.add(cbFiliere);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Ajouter");
        btnEdit = new JButton("Modifier");
        btnRemove = new JButton("Supprimer");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnRemove);
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        tfSearch = new JTextField();
        btnSearch = new JButton("Rechercher");
        searchPanel.add(new JLabel("Recherche:"), BorderLayout.WEST);
        searchPanel.add(tfSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Nom", "Prénom", "Login", "Email", "Niveau", "Filière", "Moyenne"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupListeners() {
        // Table selection listener
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    tfId.setText(tableModel.getValueAt(row, 0).toString());
                    tfNom.setText(tableModel.getValueAt(row, 1).toString());
                    tfPrenom.setText(tableModel.getValueAt(row, 2).toString());
                    tfLogin.setText(tableModel.getValueAt(row, 3).toString());
                    tfEmail.setText(tableModel.getValueAt(row, 4).toString());
                    cbNiveau.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                    cbFiliere.setSelectedItem(tableModel.getValueAt(row, 6).toString());
                }
            }
        });

        // Add button
        btnAdd.addActionListener(e -> addStudent());

        // Edit button
        btnEdit.addActionListener(e -> editStudent());

        // Remove button
        btnRemove.addActionListener(e -> removeStudent());

        // Search button
        btnSearch.addActionListener(e -> searchStudents());
    }

    private void loadInitialData() {
        try {
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement initial des données: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addStudent() {
        try {
            if (validateFields()) {
                Etudiant etudiant = createEtudiantFromFields();
                etudiantDAO.add(etudiant);
                refreshTable();
                clearFields();
                showSuccessMessage("Étudiant ajouté avec succès!");
            }
        } catch (SQLException ex) {
            showErrorMessage("Erreur lors de l'ajout: " + ex.getMessage());
        }
    }

    private void editStudent() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            try {
                if (validateFields()) {
                    Etudiant etudiant = createEtudiantFromFields();
                    etudiantDAO.update(etudiant);
                    refreshTable();
                    showSuccessMessage("Étudiant modifié avec succès!");
                }
            } catch (SQLException ex) {
                showErrorMessage("Erreur lors de la modification: " + ex.getMessage());
            }
        } else {
            showWarningMessage("Veuillez sélectionner un étudiant à modifier");
        }
    }

    private void removeStudent() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            try {
                int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                etudiantDAO.delete(id);
                refreshTable();
                clearFields();
                showSuccessMessage("Étudiant supprimé avec succès!");
            } catch (SQLException ex) {
                showErrorMessage("Erreur lors de la suppression: " + ex.getMessage());
            }
        } else {
            showWarningMessage("Veuillez sélectionner un étudiant à supprimer");
        }
    }

    private void searchStudents() {
        String searchText = tfSearch.getText();
        try {
            List<Etudiant> etudiants;
            if (searchText.isEmpty()) {
                etudiants = etudiantDAO.getAll();
            } else {
                etudiants = etudiantDAO.getAll().stream()
                        .filter(et -> containsIgnoreCase(et, searchText))
                        .toList();
            }
            updateTable(etudiants);
        } catch (SQLException ex) {
            showErrorMessage("Erreur lors de la recherche: " + ex.getMessage());
        }
    }

    private boolean containsIgnoreCase(Etudiant etudiant, String searchText) {
        return etudiant.getId().toLowerCase().contains(searchText.toLowerCase()) ||
               etudiant.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
               etudiant.getPrenom().toLowerCase().contains(searchText.toLowerCase()) ||
               etudiant.getLogin().toLowerCase().contains(searchText.toLowerCase()) ||
               etudiant.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
               String.valueOf(etudiant.getNiveau()).contains(searchText) ||
               etudiant.getFiliere().toLowerCase().contains(searchText.toLowerCase());
    }

    private void refreshTable() throws SQLException {
        List<Etudiant> etudiants = etudiantDAO.getAll();
        updateTable(etudiants);
    }

    private void updateTable(List<Etudiant> etudiants) {
        tableModel.setRowCount(0);
        for (Etudiant etudiant : etudiants) {
            tableModel.addRow(new Object[]{
                    etudiant.getId(),
                    etudiant.getNom(),
                    etudiant.getPrenom(),
                    etudiant.getLogin(),
                    etudiant.getEmail(),
                    "Niveau " + etudiant.getNiveau(),
                    etudiant.getFiliere(),
                    etudiant.getMoyenne() != null ? etudiant.getMoyenne() : "N/A"
            });
        }
    }

    private boolean validateFields() {
        if (tfNom.getText().trim().isEmpty() || 
            tfPrenom.getText().trim().isEmpty() || 
            tfLogin.getText().trim().isEmpty() || 
            tfPassword.getText().trim().isEmpty() || 
            tfEmail.getText().trim().isEmpty()) {
            
            showWarningMessage("Tous les champs obligatoires doivent être remplis");
            return false;
        }
        return true;
    }

    private Etudiant createEtudiantFromFields() {
        return new Etudiant(
            tfId.getText(),
            tfNom.getText(),
            tfPrenom.getText(),
            tfLogin.getText(),
            tfPassword.getText(),
            tfEmail.getText(),
            cbNiveau.getSelectedIndex() + 1,
            cbFiliere.getSelectedItem().toString(),
            null
        );
    }

    private void clearFields() {
        tfId.setText("");
        tfNom.setText("");
        tfPrenom.setText("");
        tfLogin.setText("");
        tfPassword.setText("");
        tfEmail.setText("");
        cbNiveau.setSelectedIndex(0);
        cbFiliere.setSelectedIndex(0);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new StudentManagementUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}