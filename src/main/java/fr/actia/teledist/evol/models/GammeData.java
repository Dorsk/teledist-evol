package fr.actia.teledist.evol.models;

public class GammeData {
    private int id;
    private String nom;
    private String url;
    private String vehicule;
    private String version;

   
    public GammeData(int id, String nom, String vehicule, String version, String url) {
        this.id = id;
        this.nom = nom;
        this.vehicule = vehicule;
        this.version = version;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }
    public String getUrl() {
        return url;
    }

    public String getVehicule() {
        return vehicule;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return id + ": " + nom;
    }
}
