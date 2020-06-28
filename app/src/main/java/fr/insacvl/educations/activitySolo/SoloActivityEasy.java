package fr.insacvl.educations.activitySolo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

import fr.insacvl.educations.R;
import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;
import fr.insacvl.educations.modele.RandomScoreWord;
import fr.insacvl.educations.modele.SpeechRandom;
import fr.insacvl.educations.modele.Syllabes;


public class SoloActivityEasy extends Activity {
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
    GridView listChar;
    // Hint textbox
    private TextView hintBox;
    // Hint string
    private String hintString;
    // Dernier indice modifier dans la hintBox
    private int indexHintBox;
    // gestion du countdown
    private TextView countdowntext;
    private long timeLeftMilisec = 30000; //30 sec

    private CountDownTimer countDownTimer;

    private Button buttonAnnuler;


    public int wordlenght;
    private HashMap<String, String> map;
    private ArrayList<HashMap<String, String>> str = new ArrayList<HashMap<String, String>>();

    private ImageView arcEnCiel;
    private Stack stackButton;
    private HashMap<RelativeLayout, Boolean> letterMap = new HashMap<>();

    Enfant child;
    List<String> listeSyllabes;

    // Int to becode child score
    // TODO : remplacer par le score de l'enfant dans le constructeur
    int childscore ;

    // function to update the score
    private void scoreUpdate(int addedScore){
        circle_fill += addedScore;
        childscore += addedScore;
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

    public void buttonAnnuler(View v){
        if (stackButton == null || stackButton.isEmpty()){ return;}

        RelativeLayout rl = (RelativeLayout) stackButton.pop();
        letterMap.put(rl,true);
        rl.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_gradient_maths));

        String tempTxt2 = String.valueOf(hintBox.getText());
        // on ajoute le char cliqué au txt
        tempTxt2 = tempTxt2.substring(0,indexHintBox-1) +  "_ " + tempTxt2.substring(indexHintBox);
        hintBox.setText(tempTxt2);
        indexHintBox--;
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
                stackButton = new Stack();
                // On récupère un mot en fonction de son score.
                dbWord = RandomScoreWord.getWord(dbWordCount);
                // le mot n'est pas trouvé
                wordfoud = false;
                wordlenght = dbWord.getContenu().length();
                // the func that creates the letter button
                createLetter(view);
                hintString= new String(new char[wordlenght]).replace("\0","_ ");
                hintBox.setText(hintString);
                // + 1 sec car la première passe direct au lancement
                timeLeftMilisec = 31000;
                startTimer();
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
                SpeechRandom.tropTardRdm(ttobj);
                wordfoud = true;
                str.clear();
                listChar.setAdapter(null);
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
                countDownTimer.cancel();
            }
        }.start();
    }

    public void updateTimer(){
        int seconds = (int) timeLeftMilisec/1000;
        String timeLeftString = ""+seconds;
        countdowntext.setText(timeLeftString);
    }

    private void createLetter(View view){
         int rand_char_nb;
         int iterID;
         String shuffled_dbword;
        // On va ajouter des mots random :
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
        listChar.setOnItemClickListener(clickListenerActivated);
    };


    private AdapterView.OnItemClickListener clickListenerActivated =new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, String> map = (HashMap<String, String>) listChar.getItemAtPosition(position);  // pour récup les données liées au bouton
            String tempTxt2 = String.valueOf(hintBox.getText());
            // on ajoute le char cliqué au txt
            tempTxt2 = tempTxt2.substring(0,indexHintBox) +  map.get("char") + tempTxt2.substring(indexHintBox+2);

            // To deactivate button when clicked
            LinearLayout thisLayout = (LinearLayout) view; //setenable = false
            CardView cd = (CardView) thisLayout.getChildAt(0);
            RelativeLayout rl = (RelativeLayout) cd.getChildAt(0);

            if (!letterMap.containsKey(rl) ||letterMap.get(rl) ){
                letterMap.put(rl,false);
                rl.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.home_gradient_gray)); //to kkchose du gris
                stackButton.push(rl);
                indexHintBox++;
            }else{
                return;
            }
            hintBox.setText(tempTxt2);
            // si c'est la bonne taille
            if(tempTxt2.length()==wordlenght){
                // on check si le mot est bon
                if(!wordfoud && String.valueOf(hintBox.getText()).toLowerCase().equals(dbWord.getContenu().toLowerCase())){
                    // on ajoute 10 points
                    scoreUpdate(10);
                    if (dbWord.getScore() <= 3) {
                        dbWord.setScore(dbWord.getScore() + 1);
                    }
                    db.updateMot(dbWord);
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
                    // on donne la récompense
                    Animation animation = AnimationUtils.loadAnimation(SoloActivityEasy.this, R.anim.zoomin);
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
                    indexHintBox = 0;
                    stackButton = new Stack();
                    str.clear();
                    listChar.setAdapter(null);
                    // si oui il est trouvé (on aura un nouveau mot avec le speech button)
                    wordfoud = true;
                    // reset du timer
                    countDownTimer.cancel();

                }
                // sinon c'est con
                else{
                    SpeechRandom.erreurRdm(ttobj);
                    str.clear();
                    listChar.setAdapter(null);
                    indexHintBox = 0;
                    hintBox.setText(hintString);
                    createLetter(view);
                    stackButton = new Stack();
                }

            }

        }
    };

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
        setContentView(R.layout.activity_solo_easy);

        arcEnCiel = (ImageView) findViewById(R.id.arcEnCiel);
        arcEnCiel.setVisibility(View.INVISIBLE);

        Intent myIntent = getIntent(); // gets the previously created intent
        child = (Enfant) myIntent.getSerializableExtra("child");


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
        progressBar = findViewById(R.id.progressBarIDEasy);
        progressBar.setProgress(circle_fill);
        progressBarText = findViewById(R.id.txtProgressIDEasy);
        progressBarText.setText(""+(int)childscore/100);

        listChar = findViewById(R.id.gridViewCaractère);

        hintBox = findViewById(R.id.hintTextEasy);
        indexHintBox = 0;

        countdowntext = findViewById(R.id.countown_easy);

        buttonAnnuler = findViewById(R.id.buttonAnnuler);

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
