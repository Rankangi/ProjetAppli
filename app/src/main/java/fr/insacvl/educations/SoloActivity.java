package fr.insacvl.educations;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class SoloActivity extends AppCompatActivity {
    // initialize text input by user
    private EditText textbox;
    // initialize object for text to speech
    private TextToSpeech ttobj;

    

    private View.OnKeyListener keylistener = new View.OnKeyListener(){

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                TextView text;
                text = findViewById(R.id.enteredText);
                text.setText(textbox.getText());
                // ttobj usage to speak
                ttobj.speak(String.valueOf(textbox.getText()),TextToSpeech.QUEUE_FLUSH,null);
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo);
        textbox = findViewById(R.id.getTheWord);
        textbox.setOnKeyListener(keylistener);
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
