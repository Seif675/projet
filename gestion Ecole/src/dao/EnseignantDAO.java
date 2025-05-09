package dao;

import java.sql.*;
import java.util.*;
import modele.Enseignant;

public non-sealed class EnseignantDAO implements IDAO<Enseignant> {
    
    private static final String TABLE_NAME = "Enseignant";
    private static final String JOIN_QUERY = 
        "SELECT u.id, u.nom, u.prenom, u.email, u.login, e.specialite " +
        "FROM " + TABLE_NAME + " e JOIN Utilisateur u ON e.utilisateur_id = u.id";

    @Override
    public void add(Enseignant enseignant) throws SQLException {
        Connection cx = null;
        PreparedStatement psUser = null;
        PreparedStatement psEns = null;
        ResultSet rs = null;
        
        try {
            cx = SingletonConnection.getInstance();
            cx.setAutoCommit(false); // Désactive l'autocommit pour la transaction

            // 1. Insertion dans Utilisateur
            String sqlUser = "INSERT INTO Utilisateur(nom, prenom, email, login, password, role) VALUES(?,?,?,?,?,'enseignant')";
            psUser = cx.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            
            psUser.setString(1, enseignant.getNom());
            psUser.setString(2, enseignant.getPrenom());
            psUser.setString(3, enseignant.getEmail());
            psUser.setString(4, enseignant.getLogin());
            psUser.setString(5, enseignant.getMotDePasse());
            
            int affectedRows = psUser.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Échec de l'insertion dans Utilisateur");
            }

            // 2. Récupération de l'ID généré
            rs = psUser.getGeneratedKeys();
            int userId;
            
            if (rs.next()) {
                userId = rs.getInt(1);
            } else {
                throw new SQLException("Échec de récupération de l'ID utilisateur");
            }

            // 3. Insertion dans Enseignant
            String sqlEns = "INSERT INTO " + TABLE_NAME + "(utilisateur_id, specialite) VALUES(?,?)";
            psEns = cx.prepareStatement(sqlEns);
            
            psEns.setInt(1, userId);
            psEns.setString(2, enseignant.getSpecialite());
            psEns.executeUpdate();
            
            cx.commit(); // Valide la transaction
            
        } catch (SQLException e) {
            if (cx != null) {
                try {
                    cx.rollback(); // Annule la transaction en cas d'erreur
                } catch (SQLException ex) {
                    throw new SQLException("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            throw new SQLException("Erreur DAO: " + e.getMessage());
        } finally {
            // Fermeture sécurisée des ressources
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* Ignored */ }
            if (psUser != null) try { psUser.close(); } catch (SQLException e) { /* Ignored */ }
            if (psEns != null) try { psEns.close(); } catch (SQLException e) { /* Ignored */ }
            if (cx != null) try { cx.setAutoCommit(true); cx.close(); } catch (SQLException e) { /* Ignored */ }
        }
    }

    @Override
    public void update(Enseignant enseignant) throws SQLException {
        if (enseignant.getId() == null) {
            throw new SQLException("ID enseignant null");
        }

        Connection cx = null;
        PreparedStatement psUser = null;
        PreparedStatement psEns = null;
        
        try {
            cx = SingletonConnection.getInstance();
            cx.setAutoCommit(false);

            // 1. Mise à jour Utilisateur
            String sqlUser = "UPDATE Utilisateur SET nom=?, prenom=?, email=?, login=? WHERE id=?";
            psUser = cx.prepareStatement(sqlUser);
            
            psUser.setString(1, enseignant.getNom());
            psUser.setString(2, enseignant.getPrenom());
            psUser.setString(3, enseignant.getEmail());
            psUser.setString(4, enseignant.getLogin());
            psUser.setInt(5, Integer.parseInt(enseignant.getId()));
            
            psUser.executeUpdate();

            // 2. Mise à jour Enseignant
            String sqlEns = "UPDATE " + TABLE_NAME + " SET specialite=? WHERE utilisateur_id=?";
            psEns = cx.prepareStatement(sqlEns);
            
            psEns.setString(1, enseignant.getSpecialite());
            psEns.setInt(2, Integer.parseInt(enseignant.getId()));
            
            psEns.executeUpdate();
            
            cx.commit();
            
        } catch (SQLException e) {
            if (cx != null) {
                try {
                    cx.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Erreur rollback: " + ex.getMessage());
                }
            }
            throw new SQLException("Erreur mise à jour: " + e.getMessage());
        } finally {
            if (psUser != null) try { psUser.close(); } catch (SQLException e) { /* Ignored */ }
            if (psEns != null) try { psEns.close(); } catch (SQLException e) { /* Ignored */ }
            if (cx != null) try { cx.setAutoCommit(true); cx.close(); } catch (SQLException e) { /* Ignored */ }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // La suppression cascade grâce à ON DELETE CASCADE dans la table Enseignant
        String sql = "DELETE FROM Utilisateur WHERE id=?";
        
        try (Connection cx = SingletonConnection.getInstance();
             PreparedStatement ps = cx.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Aucun enseignant trouvé avec l'ID: " + id);
            }
        }
    }

    @Override
    public Enseignant getById(int id) throws SQLException {
        String sql = JOIN_QUERY + " WHERE u.id=?";
        
        try (Connection cx = SingletonConnection.getInstance();
             PreparedStatement ps = cx.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Enseignant(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("login"),
                        "", // Mot de passe non récupéré pour sécurité
                        rs.getString("email"),
                        rs.getString("specialite")
                    );
                } else {
                    throw new SQLException("Enseignant non trouvé avec l'ID: " + id);
                }
            }
        }
    }

    @Override
    public List<Enseignant> getAll() throws SQLException {
        List<Enseignant> enseignants = new ArrayList<>();
        
        try (Connection cx = SingletonConnection.getInstance();
             PreparedStatement ps = cx.prepareStatement(JOIN_QUERY);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                enseignants.add(new Enseignant(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("login"),
                    "", // Mot de passe non récupéré
                    rs.getString("email"),
                    rs.getString("specialite")
                ));
            }
        }
        
        return enseignants;
    }

    public List<Enseignant> search(String keyword) throws SQLException {
        String sql = JOIN_QUERY + " WHERE LOWER(u.nom) LIKE ? OR LOWER(u.prenom) LIKE ? OR LOWER(e.specialite) LIKE ?";
        List<Enseignant> result = new ArrayList<>();
        
        try (Connection cx = SingletonConnection.getInstance();
             PreparedStatement ps = cx.prepareStatement(sql)) {
            
            String searchTerm = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ps.setString(3, searchTerm);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Enseignant(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("login"),
                        "",
                        rs.getString("email"),
                        rs.getString("specialite")
                    ));
                }
            }
        }
        
        return result;
    }
}