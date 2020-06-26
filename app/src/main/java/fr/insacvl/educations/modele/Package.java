package fr.insacvl.educations.modele;

public class Package {

    long id;
    String nom;

    public Package() {}

    public Package(long id, String nom){
        this.id = id;
        this.nom = nom;
    }

    public String getNom() {return this.nom; }

    public long getId() {
        return id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setId(long id) {
        this.id = id;
    }
}
