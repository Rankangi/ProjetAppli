package fr.insacvl.educations.modele;

import android.speech.tts.TextToSpeech;

import java.util.Random;


public class SpeechRandom {
    private static Random rd = new Random();
    public static void victoireRdm(TextToSpeech ttobj){
        int max = 6;
        int min = 0;
        int random = rd.nextInt((max-min)+1)+min;
        if(random==0){
            ttobj.speak("Bravo !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==1){
            ttobj.speak("Bien joué !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==2){
            ttobj.speak("Super !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==3){
            ttobj.speak("magnifique !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==4){
            ttobj.speak("merveilleux !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==5){
            ttobj.speak("félicitation !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==6){
            ttobj.speak("parfait !",TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    public static void tropTardRdm(TextToSpeech ttobj){
        int max = 2;
        int min = 0;
        int random = rd.nextInt((max-min)+1)+min;
        if(random==0){
            ttobj.speak("trop tard, ty va y arriver la prochaine fois !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==1){
            ttobj.speak("trop tard, tu fera mieux la prochaine fois !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==2){
            ttobj.speak("c'est trop tard, tu y arivera la prochaine fois !",TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    public static void erreurRdm(TextToSpeech ttobj){
        int max = 2;
        int min = 0;
        int random = rd.nextInt((max-min)+1)+min;
        if(random==0){
            ttobj.speak("erreur d'orthographe, essaye encore tu va y arriver !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==1){
            ttobj.speak("il y a une erreur mais tu peut le faire !",TextToSpeech.QUEUE_FLUSH,null);
        }
        if(random==2){
            ttobj.speak("il y a une erreur, ce n'est pas grave essaye encore !",TextToSpeech.QUEUE_FLUSH,null);
        }
    }
}
