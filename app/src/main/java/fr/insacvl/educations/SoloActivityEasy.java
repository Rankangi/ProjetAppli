package fr.insacvl.educations;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    // Hint textbox
    private TextView hintBox;

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
        public int rand_char_nb;
        public int iterID;
        public String shuffled_dbword;
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
                // On récupère un mot en fonction de son score.
                dbWord = RandomScoreWord.getWord(dbWordCount);

                // le mot n'est pas trouvé
                wordfoud = false;
                enteredText.setText("");

                wordlenght = dbWord.getContenu().length();
                hintString= new String(new char[wordlenght]).replace("\0","_ ");
                hintBox.setText(hintString);
                // On va ajouter des mots random :
                // TODO : mieux gérer le random
                //nb de char random :
                rand_char_nb = (int) Math.round(0.5*wordlenght);
                String[] alphabet_random = new String[] {"a","b","c","d","e","f","g","h","i","j","k","l",
                        "m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
                List<String> strList = Arrays.asList(alphabet_random);
                Collections.shuffle(strList);
                alphabet_random = strList.toArray(new String[rand_char_nb]);
                int iter = 0;
                List<String> temp = new ArrayList<String>();

                while(iter<wordlenght) {
                    temp.add(String.valueOf(dbWord.getContenu().toLowerCase().charAt(iter)));
                    iter++;
                }
                Collections.shuffle(temp);
                shuffled_dbword = "";
                for(String s:temp){
                    shuffled_dbword += s;
                }

                // On va itérer sur les char du mot et leurs ajouter un id
                iter = 0;
                iterID = 0;
                Random rd = new Random();
                while(iter<wordlenght) {
                    if(rand_char_nb!=0 && rd.nextBoolean()){
                        map = new HashMap<String, String>();
                        map.put("char", alphabet_random[rand_char_nb]);
                        map.put("id", "" + iterID);
                        rand_char_nb = rand_char_nb-1;
                        iterID++;
                        str.add(map);
                    }
                    else {
                        map = new HashMap<String, String>();
                        map.put("char", String.valueOf(shuffled_dbword.toLowerCase().charAt(iter)));
                        map.put("id", "" + iterID);
                        iterID++;
                        iter++;
                        str.add(map);
                    }
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
                        LinearLayout test = (LinearLayout) view; //setenable = false
                        test.setOnClickListener(null);
                        test.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.home_gradient_gray)); //to kkchose du gris
                        enteredText.setText(tempTxt);
                        // si c'est la bonne taille
                        if(tempTxt.length()==wordlenght){
                            // on check si le mot est bon
                            if(!wordfoud && String.valueOf(enteredText.getText()).toLowerCase().equals(dbWord.getContenu().toLowerCase())){
                                // on ajoute 10 points
                                scoreUpdate(10);
                                if (dbWord.getScore() <= 3) {
                                    dbWord.setScore(dbWord.getScore() + 1);
                                }
                                db.updateMot(dbWord);
                                // on donne la récompense
                                ttobj.speak("Bravo",TextToSpeech.QUEUE_FLUSH,null);
                                str.clear();
                                listChar.setAdapter(null);
                                // si oui il est trouvé (on aura un nouveau mot avec le speech button)
                                wordfoud = true;

                            }
                            // sinon c'est con
                            else{
                                ttobj.speak("Faux",TextToSpeech.QUEUE_FLUSH,null);
                                str.clear();
                                listChar.setAdapter(null);
                                wordfoud = true;
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
        childscore = 0;
        // link progressbar
        progressBar = findViewById(R.id.progressBarIDEasy);

        listChar = findViewById(R.id.listViewCaractère);

        enteredText = findViewById(R.id.enteredTextEasy);

        hintBox = findViewById(R.id.hintTextEasy);

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
