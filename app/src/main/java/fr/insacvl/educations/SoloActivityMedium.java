package fr.insacvl.educations;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;


public class SoloActivityMedium extends Activity {
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
    // text in progressBar
    private TextView progressBarText;
    // gestion du countdown
    private int circle_fill;
    // size of the chosen word
    private int wordsize;
    // Hint textbox
    private TextView hintBox;
    // gestion du countdown
    private TextView countdowntext;
    private long timeLeftMilisec = 30000; //30 sec
    private boolean countdown_finished;
    private CountDownTimer countDownTimer;


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
                if( !wordfoud && !countdown_finished && dbWord.getContenu().toLowerCase().equals(String.valueOf(text.getText()).toLowerCase())){
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
                    countDownTimer.cancel();
                }
                else if(countdown_finished){
                    ttobj.speak("Le temps est écoulé, choisisez un nouveau mot",TextToSpeech.QUEUE_FLUSH,null);
                }
                else if(wordfoud){
                    ttobj.speak("Le mot est déjà trouvé, choisisez un nouveau mot",TextToSpeech.QUEUE_FLUSH,null);
                }
                else{
                    ttobj.speak("Ce n'est pas la bonne orthographe",TextToSpeech.QUEUE_FLUSH,null);
                }
                // clean de la text box
                textboxUser.setText("");
                return true;
            }
            return false;
        }
    };

    private void scoreUpdate(int addedScore){
        circle_fill += addedScore;
        childscore += addedScore;
        if(circle_fill>=100){
            circle_fill = circle_fill -100;
        }
        progressBarText.setText(""+(int)childscore/100);
        progressBar.setProgress(circle_fill);
    };
    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // gérer la couleur pour que ça soit un peu plus joli
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        public String hintString;
        @Override
        public void onClick(View view) {
            // pour générer le mot random
            buttonEffect(view);
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
                // + 1 sec car la première passe direct au lancement
                countdown_finished = false;
                timeLeftMilisec = 31000;
                startTimer();
            }
            wordsize = dbWord.getContenu().length();
            hintString = new String(new char[wordsize]).replace("\0","_ ");
            hintBox.setText(hintString);
            ttobj.speak(dbWord.getContenu(),TextToSpeech.QUEUE_FLUSH,null);
        }
    };

    // timer function
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftMilisec,1000) {
            @Override
            public void onTick(long l) {
                timeLeftMilisec = l;
                updateTimer();
            }
            @Override
            public void onFinish() {
                ttobj.speak("Trop tard",TextToSpeech.QUEUE_FLUSH,null);
                wordfoud = true;
                countDownTimer.cancel();
                countdown_finished = true;
            }
        }.start();
    }

    public void updateTimer(){
        int seconds = (int) timeLeftMilisec/1000;
        String timeLeftString = ""+seconds;
        countdowntext.setText(timeLeftString);
    }

    // to make it so the countown does not run in the background
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        finish();
    }
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
        childscore = 440;
        if(childscore==0) {
            circle_fill = 0;
        }
        else{
            circle_fill = (int) ((childscore / Math.pow(10, 0)) % 10) +
                    (int) ((childscore / Math.pow(10, 1)) % 10)*10;
        }
        // link progressbar
        progressBar = findViewById(R.id.progressBarIDMedium);
        progressBar.setProgress(circle_fill);
        progressBarText = findViewById(R.id.txtProgressIDMedium);
        progressBarText.setText(""+(int)childscore/100);



        // link hintBox
        hintBox = findViewById(R.id.hintTextMedium);

        // link textboxuser to the textbox and the listener
        textboxUser = findViewById(R.id.getTheWordMedium);
        textboxUser.setOnKeyListener(keylistener);
        // link speechButton to the textbox and the listener
        speechButton = findViewById(R.id.speechButtonMedium);
        speechButton.setOnClickListener(clickListener);

        countdowntext = findViewById(R.id.countown_medium);
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
