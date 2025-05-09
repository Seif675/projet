package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modele.Etudiant;

public non-sealed class EtudiantDAO implements IDAO<Etudiant> {
    
    private static final String TABLE_NAME = "Etudiant";
    private static final String JOIN_QUERY = 
        "SELECT u.*, e.niveau, e.filiere, e.moyenne_generale " + // Changé 'moyenne' en 'moyenne_generale'
        "FROM " + TABLE_NAME + " e JOIN Utilisateur u ON e.utilisateur_id = u.id";

    @Override
    public void add(Etudiant etudiant) throws SQLException {
        validateEtudiant(etudiant);
        
        String sql = "INSERT INTO " + TABLE_NAME + 
                   " (utilisateur_id, niveau, filiere, moyenne_generale) " + // Changé ici
                   "VALUES (?, ?, ?, ?)";
        
        try (Connection cx = SingletonConnection.getInstance();
             PreparedStatement ps = cx.prepareStatement(sql)) {
            
            ps.setInt(1, Integer.parseInt(etudiant.getId()));
            ps.setInt(2, etudiant.getNiveau());
            ps.setString(3, etudiant.getFiliere());
            ps.setObject(4, etudiant.getMoyenne(), java.sql.Types.FLOAT);
            
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Etudiant etudiant) throws SQLException {
        validateEtudiant(etudiant);
        
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                    "niveau = ?, filiere = ?, moyenne_generale = ? " + // Changé ici
                    "WHERE utilisateur_id = ?";
        
        try (Connection cx = SingletonConnection.getInstance();
             PreparedStatement ps = cx.prepareStatement(sql)) {
            
            ps.setInt(1, etudiant.getNiveau());
            ps.setString(2, etudiant.getFiliere());
            ps.setObject(3, etudiant.getMoyenne(), java.sql.Types.FLOAT);
            ps.setInt(4, Integer.parseInt(etudiant.getId()));
            
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE utilisateur_id = ?";
        
        try (Connection cx = SingletonConnection.getInstance();
             PreparedStatement ps = cx.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Etudiant getById(int id) throws SQLException {
        String sql = JOIN_QUERY + " WHERE e.utilisateur_id = ?";
        Etudiant etudiant = null;
        
        try (Connection cx = SingletonConnection.getInstance();
             PreparedStatement ps = cx.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    etudiant = mapResultSetToEtudiant(rs);
                }
            }
        }
        return etudiant;
    }

    @Override
    public List<Etudiant> getAll() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        
        try (Connection cx = SingletonConnection.getInstance();
             PreparedStatement ps = cx.prepareStatement(JOIN_QUERY);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                etudiants.add(mapResultSetToEtudiant(rs));
            }
        }
        return etudiants;
    }

    private void validateEtudiant(Etudiant etudiant) {
        if (etudiant == null) {
            throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        }
        if (etudiant.getId() == null) {
            throw new IllegalArgumentException("L'ID de l'étudiant ne peut pas être null");
        }
        if (etudiant.getFiliere() == null || etudiant.getFiliere().isEmpty()) {
            throw new IllegalArgumentException("La filière ne peut pas être vide");
        }
        if (etudiant.getNiveau() < 1 || etudiant.getNiveau() > 7) {
            throw new IllegalArgumentException("Niveau invalide (doit être entre 1 et 7)");
        }
    }

    private Etudiant mapResultSetToEtudiant(ResultSet rs) throws SQLException {
        Float moyenne = rs.getFloat("moyenne_generale");
        if (rs.wasNull()) {
            moyenne = null;
        }
        
        return new Etudiant(
            String.valueOf(rs.getInt("id")),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("login"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getInt("niveau"),
            rs.getString("filiere"),
            moyenne
        );
    }
}