package modele;


public class Etudiant extends Utilisateur {

    public static final String LICENSE = "license";

    public static final String PREPA = "prepa";

    public static final String MASTER = "master";



    private String niveau;

    private String filiere;

    private Float moyenne;

    private String mention;



    public Etudiant(int id, String nom, String prenom, String login,

                    String motDePasse, String email, String niveau,

                    String filiere, Float moyenne) {

        super(id, nom, prenom, login, motDePasse, "etudiant", email);

        this.niveau = niveau;

        this.filiere = filiere;

        this.moyenne = moyenne;

        this.mention = calculerMention(moyenne);

    }





    private String calculerMention(Float moyenne) {

        if (moyenne == null) return "Non noté";

        if (moyenne >= 16) return "Très bien";

        if (moyenne >= 14) return "Bien";

        if (moyenne >= 12) return "Assez bien";

        return "Passable";

    }

    public String getNiveau() { return niveau; }

    public String getFiliere() { return filiere; }

    public Float getMoyenne() { return moyenne; }

    public String getMention() { return mention; }

    public void setNiveau(String niveau) {

        if (niveau == "1" || niveau == "3" ||niveau == "2" ) {

            this.niveau = niveau;

        }

    }



    public void setFiliere(String filiere) {

        if (filiere.equals(LICENSE) || filiere.equals(PREPA) || filiere.equals(MASTER)) {

            this.filiere = filiere;

        }

    }



    public void setMoyenne(Float moyenne) {

        if (moyenne == null || (moyenne >= 0 && moyenne <= 20)) {

            this.moyenne = moyenne;

            this.mention = calculerMention(moyenne);

        }

    }



    @Override

    public String toString() {

        String moy = (moyenne != null) ? String.valueOf(moyenne) : "N/A";

        return getPrenom() + " " + getNom() +

                " - Niveau " + niveau +

                " (" + filiere + ")" +



                " - Moyenne: " + moy;

    }



    @Override

    public boolean seConnecter() {

        System.out.println("Connexion étudiante: " + getLogin());

        return true;

    }



    @Override

    public boolean seDeconnecter() {

        System.out.println("Déconnexion étudiante: " + getLogin());

        return true;

    }

}
