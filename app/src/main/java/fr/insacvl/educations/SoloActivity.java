package fr.insacvl.educations;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;


public class SoloActivity extends AppCompatActivity {
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

    Enfant child;


    private View.OnKeyListener keylistener = new View.OnKeyListener(){

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                TextView text;
                text = findViewById(R.id.enteredText);
                text.setText(textboxUser.getText());
                // on check si le mot entré est le bon
                if( !wordfoud && dbWord.getContenu().toLowerCase().equals(String.valueOf(text.getText()).toLowerCase())){
                    // si oui il est trouvé (on aura un nouveau mot avec le speech button)
                    wordfoud = true;
                    // on donne la récompense
                    ttobj.speak(String.valueOf("Bravo"),TextToSpeech.QUEUE_FLUSH,null);
                }

                else {
                    wordfoud = false;
                    ttobj.speak(String.valueOf("Ce n'est pas la bonne orthographe"),TextToSpeech.QUEUE_FLUSH,null);

                }
                // clean de la text box
                textboxUser.setText("");
                return true;
            }
            return false;
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // pour générer le mot random
            List<Mot> dbWordCount = db.getAllMotsByIDEnfant(child.getId());
            int listsize = dbWordCount.size();
            // check si il y a au moins un mot dans la bd
            if(listsize == 0){
                ttobj.speak(String.valueOf("Pas de mots"),TextToSpeech.QUEUE_FLUSH,null);
                return;
            }
            // si oui :
            if(wordfoud) {
                // choisi un mot random
                int chosenid = new Random().nextInt(listsize); //((max - min) + 1) + min
                dbWord = dbWordCount.get(chosenid);
                // le mot n'est pas trouvé
                wordfoud = false;
            }

            ttobj.speak(dbWord.getContenu(),TextToSpeech.QUEUE_FLUSH,null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo);

        Intent myIntent = getIntent(); // gets the previously created intent
        child = (Enfant) myIntent.getSerializableExtra("child");

        Toast toast = Toast.makeText(getApplicationContext(),"Hello " + child.getNom(),Toast. LENGTH_SHORT);
        toast.show();

        // Setup DB:
        db = new DatabaseHelper(getApplicationContext());
        // link textboxuser to the textbox and the listener
        textboxUser = findViewById(R.id.getTheWord);
        textboxUser.setOnKeyListener(keylistener);
        // link speechButton to the textbox and the listener
        speechButton = findViewById(R.id.speechButton);
        speechButton.setOnClickListener(clickListener);
        // Create Object Text to Speech
        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Set the Text To Speech language
                ttobj.setLanguage(Locale.FRENCH);
            }
        });
    }



}
