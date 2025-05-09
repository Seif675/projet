package modele;

import java.util.Date;

public class Note {

    public static final String EXAMEN = "Examen";

    public static final String TP = "TP";

    public  final String DS = "DS";

    private final Date date;

    private final String typeEvaluation; // Utilise les constantes

    private final float note;

    private final float coefficient;

    public Note(String typeEvaluation, float note, float coefficient, Date date) {

        if (!typeEvaluation.equals(EXAMEN) &&

                !typeEvaluation.equals(TP) &&

                !typeEvaluation.equals(DS)) {

            throw new IllegalArgumentException("Type d'évaluation invalide");

        }

        this.typeEvaluation = typeEvaluation;

        this.note = (note >= 0 && note <= 20) ? note : 0;

        this.coefficient = (coefficient > 0) ? coefficient : 1;

        this.date = (date != null) ? date : new Date();

    }

    public String getTypeEvaluation() { return typeEvaluation; }

    public float getNote() { return note; }

    public float getCoefficient() { return coefficient; }

    public Date getDate() { return date; }

