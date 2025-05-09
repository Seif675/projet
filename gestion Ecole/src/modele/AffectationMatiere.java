package modele;
import java.time.LocalDate;
public class AffectationMatiere {
    private Etudiant etudiant;
    private Matiere matiere;
    private LocalDate dateInscription;
    private String statut;
    public static final String EN_ATTENTE = "EN_ATTENTE";
    public static final String VALIDEE = "VALIDEE";
    public static final String REFUSEE = "REFUSEE";
    public AffectationMatiere(Etudiant etudiant, Matiere matiere) {
        this.etudiant = etudiant;
        this.matiere = matiere;
        this.dateInscription = LocalDate.now();
        this.statut = EN_ATTENTE;
    }
    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }
    public LocalDate getDateInscription() {
        return dateInscription;
    }
}