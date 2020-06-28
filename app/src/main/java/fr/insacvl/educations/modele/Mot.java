package fr.insacvl.educations.modele;

public class Mot {

    long id;
    String contenu;
    long id_enfant;
    String created_at;
    int score;
    long id_package;

    public Mot() {}

    public Mot(String contenu, int score, long id, long id_enfant, String created_at, long id_package){
        this.contenu = contenu;
        this.score = score;
        this.id = id;
        this.id_enfant = id_enfant;
        this.created_at = created_at;
        this.id_package = id_package;
    }

    public String getContenu() { return this.contenu; }

    public String getCreated_at() { return this.created_at; }

    public int getScore() { return this.score; }

    public String getLevelOfScore () {
        if (score == 0){
            return "Débutant";
        }
        if (score == 1){
            return "Initié";
        }
        if (score == 2){
            return "Intermédiaire";
        }
        if (score == 3){
            return "Avancé";
        }
        if (score == 4){
            return "Maitrisé";
        }
        else return "Score invalide: " + score;
    }

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

    public long getId_enfant() {
        return id_enfant;
    }

    public long getId_package() {
        return id_package;
    }

    public void setId_package(long id_package) {
        this.id_package = id_package;
    }
}
