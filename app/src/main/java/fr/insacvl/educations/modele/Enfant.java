package fr.insacvl.educations.modele;

public class Enfant {

    long id;
    String nom;

    public Enfant() {};

    public Enfant(long id, String nom){
        this.id = id;
        this.nom = nom;
    }

    public long getId() { return this.id; }

    public String getNom() { return this.nom; }

    public void setId(long id ){
        this.id = id;
    }

    public void setNom(String nom){
        this.nom = nom;
    }
}
