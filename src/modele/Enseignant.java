package modele;

public class Enseignant extends Utilisateur{
    private String specialite;
    public Enseignant(String id, String nom, String prenom, String login, 
                      String motDePasse, String email, String specialite) {
        super(id, nom, prenom, login, motDePasse, "enseignant", email);
        this.specialite = specialite;
    }
    @Override
    public boolean seConnecter() {
        return true; 
    }

    @Override
    public boolean seDeconnecter() {
        return true; 
    }
    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}