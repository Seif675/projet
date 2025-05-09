package Interface;

import dao.EnseignantDAO;
import modele.Enseignant;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class TeacherManagementUI extends JFrame {
    private JTextField tfId, tfNom, tfPrenom, tfEmail, tfSearch, tfSpecialite, tfPassword;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnRemove, btnSearch;
    private EnseignantDAO enseignantDAO;

    public TeacherManagementUI() {
        try {
            enseignantDAO = new EnseignantDAO();
            initUI();
            setupListeners();
            loadInitialData();
            setVisible(true);
        } catch (Exception e) {
            showError("Erreur d'initialisation: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initUI() {
        setTitle("Gestion des Enseignants");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel gauche - Formulaire
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.WEST);

        // Panel droit - Tableau et recherche
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(230, 240, 250));

        // Champs de formulaire
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

        panel.add(new JLabel("Email:"));
        tfEmail = new JTextField();
        panel.add(tfEmail);

        panel.add(new JLabel("Mot de passe:"));
        tfPassword = new JTextField();
        panel.add(tfPassword);

        panel.add(new JLabel("Spécialité:"));
        tfSpecialite = new JTextField();
        panel.add(tfSpecialite);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Ajouter");
        btnEdit = new JButton("Modifier");
        btnRemove = new JButton("Supprimer");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnRemove);
        
        panel.add(new JLabel());
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Barre de recherche
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        tfSearch = new JTextField();
        btnSearch = new JButton("Rechercher");
        searchPanel.add(new JLabel("Recherche:"), BorderLayout.WEST);
        searchPanel.add(tfSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Tableau
        String[] cols = {"ID", "Nom", "Prénom", "Email", "Spécialité"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupListeners() {
        // Sélection dans le tableau
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    tfId.setText(tableModel.getValueAt(row, 0).toString());
                    tfNom.setText(tableModel.getValueAt(row, 1).toString());
                    tfPrenom.setText(tableModel.getValueAt(row, 2).toString());
                    tfEmail.setText(tableModel.getValueAt(row, 3).toString());
                    tfSpecialite.setText(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        // Bouton Ajouter
        btnAdd.addActionListener(e -> {
            if (validateFields()) {
                addTeacher();
            } else {
                showError("Veuillez remplir tous les champs obligatoires");
            }
        });

        // Bouton Modifier
        btnEdit.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                showWarning("Veuillez sélectionner un enseignant");
            } else if (validateFields()) {
                editTeacher();
            }
        });

        // Bouton Supprimer
        btnRemove.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                showWarning("Veuillez sélectionner un enseignant");
            } else {
                removeTeacher();
            }
        });

        // Bouton Rechercher
        btnSearch.addActionListener(e -> searchTeachers());
    }

    private void loadInitialData() {
        try {
            refreshTable();
        } catch (SQLException e) {
            showError("Erreur de chargement des données: " + e.getMessage());
        }
    }

    private void addTeacher() {
        try {
            Enseignant enseignant = new Enseignant(
                null,
                tfNom.getText(),
                tfPrenom.getText(),
                tfEmail.getText().split("@")[0], // Login = partie avant @ de l'email
                tfPassword.getText(),
                tfEmail.getText(),
                tfSpecialite.getText()
            );

            enseignantDAO.add(enseignant);
            refreshTable();
            clearFields();
            showSuccess("Enseignant ajouté avec succès");
        } catch (SQLException e) {
            showError("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    private void editTeacher() {
        try {
            Enseignant enseignant = new Enseignant(
                tfId.getText(),
                tfNom.getText(),
                tfPrenom.getText(),
                tfEmail.getText().split("@")[0],
                tfPassword.getText(),
                tfEmail.getText(),
                tfSpecialite.getText()
            );

            enseignantDAO.update(enseignant);
            refreshTable();
            showSuccess("Enseignant modifié avec succès");
        } catch (SQLException e) {
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void removeTeacher() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Êtes-vous sûr de vouloir supprimer cet enseignant?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(tfId.getText());
                enseignantDAO.delete(id);
                refreshTable();
                clearFields();
                showSuccess("Enseignant supprimé avec succès");
            } catch (SQLException e) {
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void searchTeachers() {
        String searchText = tfSearch.getText().trim().toLowerCase();
        try {
            List<Enseignant> enseignants = enseignantDAO.getAll();
            tableModel.setRowCount(0);
            
            for (Enseignant e : enseignants) {
                if (searchText.isEmpty() ||
                    e.getNom().toLowerCase().contains(searchText) ||
                    e.getPrenom().toLowerCase().contains(searchText) ||
                    e.getEmail().toLowerCase().contains(searchText) ||
                    e.getSpecialite().toLowerCase().contains(searchText)) {
                    
                    tableModel.addRow(new Object[]{
                        e.getId(),
                        e.getNom(),
                        e.getPrenom(),
                        e.getEmail(),
                        e.getSpecialite()
                    });
                }
            }
        } catch (SQLException e) {
            showError("Erreur lors de la recherche: " + e.getMessage());
        }
    }

    private void refreshTable() throws SQLException {
        tableModel.setRowCount(0);
        List<Enseignant> enseignants = enseignantDAO.getAll();
        
        for (Enseignant e : enseignants) {
            tableModel.addRow(new Object[]{
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getEmail(),
                e.getSpecialite()
            });
        }
    }

    private boolean validateFields() {
        return !tfNom.getText().trim().isEmpty() &&
               !tfPrenom.getText().trim().isEmpty() &&
               !tfEmail.getText().trim().isEmpty() &&
               !tfSpecialite.getText().trim().isEmpty() &&
               !tfPassword.getText().trim().isEmpty();
    }

    private void clearFields() {
        tfId.setText("");
        tfNom.setText("");
        tfPrenom.setText("");
        tfEmail.setText("");
        tfPassword.setText("");
        tfSpecialite.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new TeacherManagementUI();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors du démarrage: " + e.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}