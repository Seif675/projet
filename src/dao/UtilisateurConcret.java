package dao;

public class UtilisateurConcret {
    private String id;
    private String nom;
    private String prenom;
    private String login;
    private String role;
    private String email;

    public UtilisateurConcret(String id, String nom, String prenom, String login, 
                            String role, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.role = role;
        this.email = email;
    }

    // Getters
    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getLogin() { return login; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
}
