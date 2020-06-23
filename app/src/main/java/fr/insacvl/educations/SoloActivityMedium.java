package fr.insacvl.educations;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;


public class SoloActivityMedium extends AppCompatActivity {
    // Get the DB:
    DatabaseHelper db;
    // initialize variable text input by user
    private EditText textboxUser;
    // initialize variable buton speech
    private Button speechButton;
    // initialize object for text to speech
    private TextToSpeech ttobj;
    // initialize word chosen in BD :
    private Mot dbWord;
    // to check if the word was found
    boolean wordfoud = true;
    // the Progress Bar
    private ProgressBar progressBar;
    // size of the chosen word
    private int wordsize;
    // Hint textbox
    private TextView hintBox;


    Enfant child;

    // Int to becode child score
    // TODO : remplacer par le score de l'enfant dans le constructeur
    int childscore ;


    private View.OnKeyListener keylistener = new View.OnKeyListener(){

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                TextView text;
                text = findViewById(R.id.enteredTextMedium);
                text.setText(textboxUser.getText());
                // on check si le mot entré est le bon
                if( !wordfoud && dbWord.getContenu().toLowerCase().equals(String.valueOf(text.getText()).toLowerCase())){
                    // si oui il est trouvé (on aura un nouveau mot avec le speech button)
                    wordfoud = true;
                    // on ajoute 10 points
                    scoreUpdate(10);
                    DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                    if (dbWord.getScore() <= 3) {
                        dbWord.setScore(dbWord.getScore() + 1);
                    }
                    databaseHelper.updateMot(dbWord);
                    // on donne la récompense
                    ttobj.speak("Bravo",TextToSpeech.QUEUE_FLUSH,null);
                    hintBox.setText(textboxUser.getText());
                }
                else {
                    ttobj.speak("Ce n'est pas la bonne orthographe",TextToSpeech.QUEUE_FLUSH,null);
                }
                // clean de la text box
                textboxUser.setText("");
                return true;
            }
            return false;
        }
    };

    // function to update the score
    private void scoreUpdate(int addedScore){
        childscore += addedScore;
        if(childscore>100){
            childscore = childscore -100;
            //TODO ajouter un niveau à l'enfant ?
        }
        progressBar.setProgress(childscore);
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        public String hintString;
        @Override
        public void onClick(View view) {
            // pour générer le mot random
            List<Mot> dbWordCount = db.getAllMotsByIDEnfant(child.getId());
            int listsize = dbWordCount.size();
            // check si il y a au moins un mot dans la bd
            if(listsize == 0){
                ttobj.speak("Pas de mots",TextToSpeech.QUEUE_FLUSH,null);
                return;
            }
            // si oui :
            if(wordfoud) {
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

                // Sélection d'un mot aléatoire dans le score le moins connu
                if (dbWordCount0.size() != 0){
                    int chosenid = new Random().nextInt(dbWordCount0.size()); //((max - min) + 1) + min
                    dbWord = dbWordCount0.get(chosenid);
                }
                else if (dbWordCount1.size() != 0){
                    int chosenid = new Random().nextInt(dbWordCount1.size()); //((max - min) + 1) + min
                    dbWord = dbWordCount1.get(chosenid);
                }
                else if (dbWordCount2.size() != 0){
                    int chosenid = new Random().nextInt(dbWordCount2.size()); //((max - min) + 1) + min
                    dbWord = dbWordCount2.get(chosenid);
                }
                else if (dbWordCount3.size() != 0){
                    int chosenid = new Random().nextInt(dbWordCount3.size()); //((max - min) + 1) + min
                    dbWord = dbWordCount3.get(chosenid);
                }
                else if (dbWordCount4.size() != 0){
                    int chosenid = new Random().nextInt(dbWordCount4.size()); //((max - min) + 1) + min
                    dbWord = dbWordCount4.get(chosenid);
                }

                // le mot n'est pas trouvé
                wordfoud = false;
            }
            wordsize = dbWord.getContenu().length();
            hintString = new String(new char[wordsize]).replace("\0","_ ");
            hintBox.setText(hintString);
            ttobj.speak(dbWord.getContenu(),TextToSpeech.QUEUE_FLUSH,null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_medium);

        Intent myIntent = getIntent(); // gets the previously created intent
        child = (Enfant) myIntent.getSerializableExtra("child");

        Toast toast = Toast.makeText(getApplicationContext(),"Hello " + child.getNom(),Toast. LENGTH_SHORT);
        toast.show();

        // Setup DB:
        db = new DatabaseHelper(getApplicationContext());
        // initialize score
        // TODO add child score
        childscore = 0;
        // link progressbar
        progressBar = findViewById(R.id.progressBarIDMedium);
        // link hintBox
        hintBox = findViewById(R.id.hintTextMedium);

        // link textboxuser to the textbox and the listener
        textboxUser = findViewById(R.id.getTheWordMedium);
        textboxUser.setOnKeyListener(keylistener);
        // link speechButton to the textbox and the listener
        speechButton = findViewById(R.id.speechButtonMedium);
        speechButton.setOnClickListener(clickListener);
        // Create Object Text to Speech
        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // Set the Text To Speech language
                ttobj.setLanguage(Locale.FRENCH);
            }
        },"com.google.android.tts");

    }



}
