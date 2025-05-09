package dao;

import java.sql.*;
import modele.Utilisateur;

public class UtilisateurDAO {
    private static final String TABLE_NAME = "Utilisateur";

    public Utilisateur authenticate(String login, String password, String role) throws SQLException {
        String sql = "SELECT id, nom, prenom, email, role FROM " + TABLE_NAME + 
                   " WHERE login = ? AND password = ? AND role = ?";
        
        try (Connection cx = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/project", "root", "Seifoun123@");
             PreparedStatement ps = cx.prepareStatement(sql)) {
            
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, role.toLowerCase());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        login, // Utilise le login fourni plutôt que de le récupérer
                        "", // Ne pas stocker le mot de passe en mémoire
                        rs.getString("role"),
                        rs.getString("email")
                    ) {
                        @Override
                        public boolean seConnecter() {
                            return true;
                        }

                        @Override
                        public boolean seDeconnecter() {
                            return true;
                        }
                    };
                }
            }
        }
        return null;
    }
}