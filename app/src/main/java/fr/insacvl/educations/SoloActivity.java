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

    private EditText textbox;
    private TextToSpeech ttobj;

    

    private View.OnKeyListener keylistener = new View.OnKeyListener(){

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                TextView text;
                text = findViewById(R.id.enteredText);
                text.setText(textbox.getText());
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
        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttobj.setLanguage(Locale.FRENCH);
            }
        });
    }



}
