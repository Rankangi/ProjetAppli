package fr.insacvl.educations.activitySolo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import fr.insacvl.educations.R;
import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;
import fr.insacvl.educations.modele.RandomScoreWord;
import fr.insacvl.educations.modele.SpeechRandom;
import fr.insacvl.educations.modele.Syllabes;


public class SoloActivityMedium extends Activity {
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
    private int nbLettreSaisie = 0;
    private boolean wordwritten = false;
    private String writtenString = "";
    private Button keyboardButton;
    private String hintString;
    private ImageView arcEnCiel;

    Enfant child;
    List<String> listeSyllabes;

    // Int to becode child score
    // TODO : remplacer par le score de l'enfant dans le constructeur
    int childscore;

    private void scoreUpdate(int addedScore){
        circle_fill += addedScore;
        childscore += addedScore;
        child.setXp(childscore);
        db.updateEnfant(child);
        if(circle_fill>=100){
            circle_fill = circle_fill -100;
        }
        progressBarText.setText(""+(int)childscore/100);
        progressBar.setProgress(circle_fill);
    }

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
            if (!wordwritten){
                hintString = new String(new char[wordsize]).replace("\0","_ ");
                hintBox.setText(hintString);
                wordwritten = true;
            }
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
                listeSyllabes = Syllabes.getSyllabes(dbWord.getContenu());
                hintBox.setText("");
                // pour altérner les couleurs
                Boolean color = true;
                for(String s:listeSyllabes){
                    Spannable wordColored = new SpannableString(s);
                    if(color){
                        wordColored.setSpan(new ForegroundColorSpan(Color.BLUE),0,wordColored.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        hintBox.append(wordColored);
                        color = false;
                    }
                    else{
                        wordColored.setSpan(new ForegroundColorSpan(Color.RED),0,wordColored.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        hintBox.append(wordColored);
                        color = true;
                    }
                }
                wordwritten = false;
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
    public boolean onKeyMultiple(int KeyCode,int i, KeyEvent event){
        if (!wordfoud &&wordsize > nbLettreSaisie/2){
            String caractereRecupere = event.getCharacters() + "";
            String text = hintBox.getText().toString();
            writtenString = writtenString.concat(caractereRecupere);
            text = text.substring(0, nbLettreSaisie) + caractereRecupere + text.substring(nbLettreSaisie + 1);
            hintBox.setText(text);
            nbLettreSaisie++;
            nbLettreSaisie++;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        RelativeLayout layout = findViewById(R.id.layout);
        layout.requestFocus();
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){

        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){

        }
        else if(keyCode==KeyEvent.KEYCODE_ENTER){
            boolean bonneOrthographe = true;
            // on check si le mot entré est le bon

            if( !wordfoud && !countdown_finished && dbWord.getContenu().toLowerCase().equals(String.valueOf(writtenString).toLowerCase())){
                // si oui il est trouvé (on aura un nouveau mot avec le speech button)
                wordfoud = true;
                // on ajoute 10 points
                scoreUpdate(20);
                DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                if (dbWord.getScore() <= 3) {
                    dbWord.setScore(dbWord.getScore() + 1);
                }
                listeSyllabes = Syllabes.getSyllabes(dbWord.getContenu());
                hintBox.setText("");
                // pour altérner les couleurs
                Boolean color = true;
                for(String s:listeSyllabes){
                    Spannable wordColored = new SpannableString(s);
                    if(color){
                        wordColored.setSpan(new ForegroundColorSpan(Color.BLUE),0,wordColored.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        hintBox.append(wordColored);
                        color = false;
                    }
                    else{
                        wordColored.setSpan(new ForegroundColorSpan(Color.RED),0,wordColored.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        hintBox.append(wordColored);
                        color = true;
                    }
                }
                databaseHelper.updateMot(dbWord);
                // on donne la récompense
                Animation animation = AnimationUtils.loadAnimation(SoloActivityMedium.this, R.anim.zoomin);
                animation.setAnimationListener(new Animation.AnimationListener(){

                    @Override
                    public void onAnimationStart(Animation animation){
                        SpeechRandom.victoireRdm(ttobj);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation){}

                    @Override
                    public void onAnimationEnd(Animation animation){
                        arcEnCiel.setVisibility(View.INVISIBLE);
                    }
                });
                arcEnCiel.startAnimation(animation);
                countDownTimer.cancel();
            }
            else if(countdown_finished){
                SpeechRandom.tropTardRdm(ttobj);
            }
            else if(wordfoud){
                ttobj.speak("Le mot est déjà trouvé, choisisez un nouveau mot",TextToSpeech.QUEUE_FLUSH,null);
            }
            else{
                hintBox.setText(hintString);
                bonneOrthographe = false;
                SpeechRandom.erreurRdm(ttobj);
            }
            // clean de la text box
            wordwritten = false;
            nbLettreSaisie = 0;
            writtenString = "";
            /*if(bonneOrthographe) {
                hintBox.setText("");
            }*/
        }

        else if(!wordfoud &&keyCode == KeyEvent.KEYCODE_DEL){
            if (nbLettreSaisie >= 2) {
                String text = hintBox.getText().toString();
                writtenString = writtenString.substring(0, writtenString.length()-1);
                int lenght = text.length();
                text = text.substring(0, nbLettreSaisie-2);
                while (text.length() != lenght) {
                    text = text.concat("_ ");
                }
                nbLettreSaisie -= 2;
                hintBox.setText(text);
            }
        }
        else if (!wordfoud &&wordsize > nbLettreSaisie/2){
            String caractereRecupere = (char) event.getUnicodeChar() + "";
            String text = hintBox.getText().toString();
            writtenString = writtenString.concat(caractereRecupere);
            text = text.substring(0, nbLettreSaisie) + caractereRecupere + text.substring(nbLettreSaisie + 1);
            hintBox.setText(text);
            nbLettreSaisie++;
            nbLettreSaisie++;
        }
        return super.onKeyDown(keyCode, event);
    }

    private View.OnClickListener keyboardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_medium);

        arcEnCiel = (ImageView) findViewById(R.id.arcEnCiel);
        arcEnCiel.setVisibility(View.INVISIBLE);

        // Setup DB:
        db = new DatabaseHelper(getApplicationContext());

        Intent myIntent = getIntent(); // gets the previously created intent
        long childId = (long) myIntent.getSerializableExtra("childId");
        child = db.getEnfant(childId);

        // initialize score
        // TODO add child score
        childscore = child.getXp();
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

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        keyboardButton = findViewById(R.id.keyboardButton);
        keyboardButton.setOnClickListener(keyboardClickListener);
        // link hintBox
        hintBox = findViewById(R.id.hintTextMedium);

        // link textboxuser to the textbox and the listener
        hintBox.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

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
