package fr.insacvl.educations.modele;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomScoreWord {

    public static Mot getWord(List<Mot> dbWordCount) {
        Mot dbWord = null;
        // Tri des mots par difficulté
        List<Mot> dbWordCount0 = new ArrayList<>();
        List<Mot> dbWordCount1 = new ArrayList<>();
        List<Mot> dbWordCount2 = new ArrayList<>();
        List<Mot> dbWordCount3 = new ArrayList<>();
        List<Mot> dbWordCount4 = new ArrayList<>();

        // On ajoute les mots dans les tableaux de difficultés associées
        for (Mot mot: dbWordCount){
            if (mot.getScore() == 0){
                dbWordCount0.add(mot);
            }
            if (mot.getScore() == 1){
                dbWordCount1.add(mot);
            }
            if (mot.getScore() == 2){
                dbWordCount2.add(mot);
            }
            if (mot.getScore() == 3){
                dbWordCount3.add(mot);
            }
            if (mot.getScore() == 4){
                dbWordCount4.add(mot);
            }
        }

        /*
            Répartition de la probabilité d'être choisi en fonction de la liste :
            0 : 65%
            1 : 15%
            2 : 10%
            3 :  7%
            4 :  3%
         */
        while(dbWord == null){
            double proba = Math.random() * 2;
            // Sélection d'un mot aléatoire dans le score le moins connu
            if ((proba <= 0.65 && dbWordCount0.size() != 0)){
                int chosenid = new Random().nextInt(dbWordCount0.size()); //((max - min) + 1) + min
                dbWord = dbWordCount0.get(chosenid);
            }
            else if ((proba <= 0.8 && dbWordCount1.size() != 0)){
                int chosenid = new Random().nextInt(dbWordCount1.size()); //((max - min) + 1) + min
                dbWord = dbWordCount1.get(chosenid);
            }
            else if (proba <= 0.9 && dbWordCount2.size() != 0){
                int chosenid = new Random().nextInt(dbWordCount2.size()); //((max - min) + 1) + min
                dbWord = dbWordCount2.get(chosenid);
            }
            else if (proba <= 0.97 && dbWordCount3.size() != 0){
                int chosenid = new Random().nextInt(dbWordCount3.size()); //((max - min) + 1) + min
                dbWord = dbWordCount3.get(chosenid);
            }
            else if (proba <= 1 && dbWordCount4.size() != 0){
                int chosenid = new Random().nextInt(dbWordCount4.size()); //((max - min) + 1) + min
                dbWord = dbWordCount4.get(chosenid);
            }
        }
        return dbWord;
    }
}