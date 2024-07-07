package fr.actia.teledist.evol.models;

public class ArtifactData {
    private int id;
    private String nom;
    private String url;
    private String version;
    private String type;
    private String path;

    public ArtifactData(int id, String nom, String url, String version, String type, String path) {
        this.id = id;
        this.nom = nom;
        this.url = url;
        this.version = version;
        this.type = type;
        this.path = path;
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
    public String getVersion() {
        return version;
    }
    public String getType() {
        return type;
    }
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return id + ": " + nom;
    }
}
