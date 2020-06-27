package fr.insacvl.educations.modele;

import java.util.ArrayList;
import java.util.List;

public class Syllabes {

    /*

    --------------------------------- Erreurs -------------------------------------------

    - Si y'a deux consones à la suite à la fin d'un mot ça prend pas la deuxième

    - Si la consone suivant une voyelle est dans la syllabe suivante (ex voi - tu - re )

     */
    public static List<String> getSyllabes(String mot){
        // On met en lowerCase au cas ou
        String motCopy = mot.toLowerCase();
        // on créer la liste des syllabes
        List<String> listeSyllabes = new ArrayList<String>();
        // question de pratique Char = le caractère de l'itération
        char Char;
        // la string à ajouter
        String syllabe= "";
        // Si on a trouvé une voyelle, passe à vrai
        boolean voyelle = false;
        // on parcour la string
        for(int i = 0; i< motCopy.length();i++){
            Char = motCopy.charAt(i);
            // si char == voyel, on à touver une voyelle
            if(Syllabes.isVoyel(Char)){
                syllabe += String.valueOf(Char);
                voyelle = true;
            }
            // sinon 2 cas : le char est une consonne
            else {
                // le char précédent est une voyelle : ça termine la voyelle
                if(voyelle){
                    syllabe += String.valueOf(Char);
                    listeSyllabes.add(syllabe);
                    syllabe = "";
                    voyelle = false;
                }
                // sinon on l'ajoute simplement à la string
                else{
                    syllabe += String.valueOf(Char);
                }
            }
        }
      return listeSyllabes;
    }


    public static boolean isVoyel(char c) {
        return (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
    }
}
