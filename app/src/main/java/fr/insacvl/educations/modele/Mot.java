package fr.insacvl.educations.modele;

public class Mot {

    long id;
    String contenu;
    long id_enfant;
    String created_at;
    int score;

    public Mot() {}

    public Mot(String contenu, int score, long id, long id_enfant, String created_at ){
        this.contenu = contenu;
        this.score = score;
        this.id = id;
        this.id_enfant = id_enfant;
        this.created_at = created_at;
    }

    public String getContenu() { return this.contenu; }

    public String getCreated_at() { return this.created_at; }

    public int getScore() { return this.score; }

    public long getId() { return this.id; }

    public long getIdEnfant() { return this.id_enfant; }

    public void setId(long id) {
        this.id = id;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setId_enfant(long id_enfant) {
        this.id_enfant = id_enfant;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
