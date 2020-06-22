package fr.insacvl.educations;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;


public class SoloActivityEasy extends AppCompatActivity {
    // Get the DB:
    DatabaseHelper db;
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
    ListView listChar;

    private TextView enteredText;
    private HashMap<String, String> map;
    private ArrayList<HashMap<String, String>> str = new ArrayList<HashMap<String, String>>();

    Enfant child;

    // Int to becode child score
    // TODO : remplacer par le score de l'enfant dans le constructeur
    int childscore ;

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
        public int wordlenght;
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
                enteredText.setText("");
                // choisi un mot random
                int chosenid = new Random().nextInt(listsize); //((max - min) + 1) + min
                dbWord = dbWordCount.get(chosenid);
                // le mot n'est pas trouvé
                wordfoud = false;

                wordlenght = dbWord.getContenu().length();
                // On va itérer sur les char du mot et leurs ajouter un id
                int iter = 0;
                while(iter<wordlenght) {
                    map = new HashMap<String, String>();
                    map.put("char", String.valueOf(dbWord.getContenu().charAt(iter)));
                    map.put("id", "" + iter);
                    iter++;
                    str.add(map);
                }
                final SimpleAdapter adapter = new SimpleAdapter(view.getContext(), str, R.layout.letter_selector,
                        new String[] {"char"}, new int[] {R.id.caractere});
                // on va set l'adapter
                listChar.setAdapter(adapter);
                listChar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> map = (HashMap<String, String>) listChar.getItemAtPosition(position);  // pour récup les données liées au bouton
                        String tempTxt = String.valueOf(enteredText.getText());
                        // on ajoute le char cliqué au txt
                        tempTxt += map.get("char");
                        enteredText.setText(tempTxt);
                        // si c'est la bonne taille
                        if(tempTxt.length()==wordlenght){
                            // on check si le mot est bon
                            if(!wordfoud && String.valueOf(enteredText.getText()).equals(dbWord.getContenu())){
                                // si oui il est trouvé (on aura un nouveau mot avec le speech button)
                                wordfoud = true;
                                // on ajoute 10 points
                                scoreUpdate(10);
                                // on donne la récompense
                                ttobj.speak("Bravo",TextToSpeech.QUEUE_FLUSH,null);
                                str.clear();
                                listChar.setAdapter(null);
                            }
                            // sinon c'est con
                            else{
                                ttobj.speak("Grosse merde",TextToSpeech.QUEUE_FLUSH,null);
                                enteredText.setText("");
                            }

                        }

                    }
                });
            }

            ttobj.speak(dbWord.getContenu(),TextToSpeech.QUEUE_FLUSH,null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_easy);

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
        progressBar = findViewById(R.id.progressBarIDEasy);

        listChar = findViewById(R.id.listViewCaractère);

        enteredText = findViewById(R.id.enteredTextEasy);

        // link textboxuser to the textbox and the listener
        // link speechButton to the textbox and the listener
        speechButton = findViewById(R.id.speechButtonEasy);
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
