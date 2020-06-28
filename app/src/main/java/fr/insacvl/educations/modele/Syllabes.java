package fr.insacvl.educations.modele;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Syllabes {

    /*

    --------------------------------- Erreurs -------------------------------------------

    - tiret en milieu de mot

     */

    public static List<String> getSyllabes(String mot){
        // On met en lowerCase au cas ou
        String motCopy = mot.toLowerCase();
        // on créer la liste des syllabes
        List<String> listeSyllabes = new ArrayList<String>();
        List<Character> charList = new ArrayList<>();

        // Gestion de quelques exeptions à la con avec plus de 3 consonnes d'affilé (pas toutes):
        if(motCopy.equals("flyschs ")){
            listeSyllabes.add("flys");
            listeSyllabes.add("chs");
            return listeSyllabes;
        }
        if (motCopy.equals("schnaps")){
            listeSyllabes.add("schnaps");
            return listeSyllabes;
        }
        if(motCopy.equals("borchtchs")){
            listeSyllabes.add("bor");
            listeSyllabes.add("chtchs");
            return listeSyllabes;
        }
        if(motCopy.equals("schmilblicks")){
            listeSyllabes.add("schmil");
            listeSyllabes.add("blicks");
            return listeSyllabes;
        }
        if(motCopy.equals("gewurztraminer")){
            listeSyllabes.add("ge");
            listeSyllabes.add("wurz");
            listeSyllabes.add("tra");
            listeSyllabes.add("mi");
            listeSyllabes.add("ner");
            return listeSyllabes;
        }
        if(motCopy.equals("ichthus")){
            listeSyllabes.add("ich");
            listeSyllabes.add("thus");
            return listeSyllabes;
        }
        if(motCopy.equals("aeschne")){
            listeSyllabes.add("aes");
            listeSyllabes.add("chne");
            return listeSyllabes;
        }
        if(motCopy.equals("welwitschia")){
            listeSyllabes.add("wel");
            listeSyllabes.add("wits");
            listeSyllabes.add("chia");
            return listeSyllabes;
        }
        if(motCopy.equals("breitschwanz")){
            listeSyllabes.add("breits");
            listeSyllabes.add("chwanz");
            return listeSyllabes;
        }
        if(motCopy.equals("calcschiste")){
            listeSyllabes.add("calc");
            listeSyllabes.add("schis");
            listeSyllabes.add("te");
            return listeSyllabes;
        }
        if(motCopy.equals("nietzschéen")){
            listeSyllabes.add("nietz");
            listeSyllabes.add("schéen");
            return listeSyllabes;
        }
        if(motCopy.equals("schproum")){
            listeSyllabes.add("schproum");
            return listeSyllabes;
        }


        // on parcour la string et on enregistre les syllabes
        for(int i = 0; i< motCopy.length();i++) {
            charList.add(motCopy.charAt(i));
        }

        // la string à ajouter
        String syllabe= "";
        // Si on a trouvé une voyelle, passe à vrai
        boolean voyelle = false;
        int nbConsone = 0;
        String tempString;
        Log.i("DIM",""+charList.size());
        for(int k = 0; k < charList.size();k++){
            Log.i("DIM",""+charList.size());
            if(Syllabes.isVoyel(charList.get(k))){
                if(!voyelle) {
                    // cas 1 consone avant une voyelle : on coupe avant la consone et on ajoute cette
                    // consone à la prochaine syllabe
                    // ex : ca - deau
                    if (nbConsone == 1) {
                        tempString = "" + syllabe.charAt(syllabe.length() - 1);
                        syllabe = syllabe.substring(0, syllabe.length() - 1);
                        listeSyllabes.add(syllabe);
                        syllabe = "" + tempString;
                    }
                    // cas 2 consonne avant une voyelle : si les deux consonnes forment un groupe
                    // d'attaque, elle font partie de la syllabe suivante, sinon on coupe au milieu
                    if (nbConsone == 2 || nbConsone == 3) {
                        if (Syllabes.isGConsAtk(syllabe.charAt(syllabe.length() - 2), syllabe.charAt(syllabe.length() - 1))) {
                            tempString = "" + syllabe.charAt(syllabe.length() - 2) + "" + syllabe.charAt(syllabe.length() - 1);
                            syllabe = syllabe.substring(0, syllabe.length() - 1);
                            syllabe = syllabe.substring(0, syllabe.length() - 1);
                            listeSyllabes.add(syllabe);
                            syllabe = "" + tempString;
                        } else {
                            tempString = "" + syllabe.charAt(syllabe.length() - 1);
                            syllabe = syllabe.substring(0, syllabe.length() - 1);
                            listeSyllabes.add(syllabe);
                            syllabe = "" + tempString;
                        }
                    }
                }
                else{
                    voyelle = true;
                }
                syllabe +=""+charList.get(k);
                nbConsone=0;
            }
            else {
                // CAS consone après voyelle
                if(voyelle){
                    nbConsone++;
                    syllabe += String.valueOf(charList.get(k));
                    syllabe = "";
                    voyelle = false;

                }
                else{
                    nbConsone++;
                    syllabe += String.valueOf(charList.get(k));
                }
            }
        }
        // on procède aux correction mineurs
        listeSyllabes.add(syllabe);
        if(listeSyllabes.get(0).equals("")){
            listeSyllabes.remove(0);
        }
        String correction = "";
        int indiceCorrection = 0;
        boolean correctionNeeded = false;
        for(int j=0; j<listeSyllabes.size(); j++){
            if(listeSyllabes.get(j).charAt(0)=='\''){
                correction = listeSyllabes.get(j-1)+listeSyllabes.get(j);
                indiceCorrection=j;
                correctionNeeded=true;
            }
        }
        if(correctionNeeded) {
            listeSyllabes.remove(indiceCorrection);
            listeSyllabes.set(indiceCorrection-1,correction);
        }
        return listeSyllabes;
    }


    public static boolean isVoyel(Character c) {
        return (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
    }
    // si group cons d'atttaque en milieu de mot -> début la syllabe suivante
    // il en manque peut-être
    public static boolean isGConsAtk(Character c1,Character c2){
        String s = ""+c1+c2;
        return(s.equals("bl") || s.equals("cl") || s.equals("fl") || s.equals("gl") || s.equals("pl") || s.equals("br") || s.equals("cr") ||
                s.equals("dr") || s.equals("fr") || s.equals("gr") || s.equals("kr") || s.equals("pr") || s.equals("tr") || s.equals("vr") ||
                s.equals("ch") || s.equals("ph") || s.equals("th") || s.equals("gn") || s.equals("cr") );
    }

    // si groupe cons de début de mot : ensemble
    public static boolean isGConsAtkDebut(Character c1,Character c2){
        String s = ""+c1+c2;
        return(s.equals("pn") || s.equals("kn") || s.equals("vi") || s.equals("ps") || s.equals("ts") ||
                s.equals("sb") || s.equals("sc") || s.equals("sp") || s.equals("st"));
    }
}
