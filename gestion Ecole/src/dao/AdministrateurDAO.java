package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modele.Administrateur;

public non-sealed class AdministrateurDAO implements IDAO<Administrateur> {

    @Override
    public void add(Administrateur admin) throws SQLException {
        if (admin == null) {
            throw new IllegalArgumentException("L'administrateur ne peut pas être null");
        }
        
        Connection cx = SingletonConnection.getInstance();
        String req = "INSERT INTO Administrateur(utilisateur_id, telephone) VALUES (?, ?)";
        
        try (PreparedStatement ps = cx.prepareStatement(req)) {
            ps.setInt(1, Integer.parseInt(admin.getId()));
            ps.setString(2, admin.getTelephone());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Administrateur admin) throws SQLException {
        if (admin == null) {
            throw new IllegalArgumentException("L'administrateur ne peut pas être null");
        }
        
        Connection cx = SingletonConnection.getInstance();
        String req = "UPDATE Administrateur SET telephone = ? WHERE utilisateur_id = ?";
        
        try (PreparedStatement ps = cx.prepareStatement(req)) {
            ps.setString(1, admin.getTelephone());
            ps.setInt(2, Integer.parseInt(admin.getId()));
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection cx = SingletonConnection.getInstance();
        String req = "DELETE FROM Administrateur WHERE utilisateur_id = ?";
        
        try (PreparedStatement ps = cx.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Administrateur getById(int id) throws SQLException {
        Connection cx = SingletonConnection.getInstance();
        String req = "SELECT u.*, a.telephone FROM Administrateur a " +
                    "JOIN Utilisateur u ON a.utilisateur_id = u.id " +
                    "WHERE a.utilisateur_id = ?";
        Administrateur admin = null;
        
        try (PreparedStatement ps = cx.prepareStatement(req)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Administrateur(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("login"),
                        rs.getString("motDePasse"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("telephone")
                    );
                }
            }
        }
        
        return admin;
    }

    @Override
    public List<Administrateur> getAll() throws SQLException {
        Connection cx = SingletonConnection.getInstance();
        String req = "SELECT u.*, a.telephone FROM Administrateur a " +
                    "JOIN Utilisateur u ON a.utilisateur_id = u.id";
        List<Administrateur> admins = new ArrayList<>();
        
        try (PreparedStatement ps = cx.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Administrateur admin = new Administrateur(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("login"),
                    rs.getString("motDePasse"),
                    rs.getString("role"),
                    rs.getString("email"),
                    rs.getString("telephone")
                );
                admins.add(admin);
            }
        }
        
        return admins;
    }
}