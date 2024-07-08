package fr.actia.teledist.evol.models;

public class UsineData {
    private int id;
    private String nom;
    private String pays;

    public UsineData(int id, String nom, String pays) {
        this.id = id;
        this.nom = nom;
        this.pays = pays;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }
   
    public String getPays() {
        return pays;
    }

    @Override
    public String toString() {
        return id + ": " + nom + " | " + pays;
    }
}
