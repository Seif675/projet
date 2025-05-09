package modele;

public class Administrateur extends Utilisateur {
    private String telephone;

    public Administrateur(String id, String nom, String prenom, String login,
                         String motDePasse, String role, String email, String telephone) {
        super(id, nom, prenom, login, motDePasse, role, email);
        setTelephone(telephone);
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        if (telephone == null || !telephone.matches("\\d{8,15}")) {
            throw new IllegalArgumentException("Le numéro de téléphone doit contenir entre 8 et 15 chiffres");
        }
        this.telephone = telephone;
    }

    @Override
    public boolean seConnecter() {
        return true;
    }

    @Override
    public boolean seDeconnecter() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + ", telephone=" + telephone;
    }
}