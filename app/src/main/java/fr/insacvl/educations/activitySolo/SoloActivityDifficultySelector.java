package fr.insacvl.educations.activitySolo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import fr.insacvl.educations.R;
import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;

public class SoloActivityDifficultySelector extends Activity {
    // Get the DB:
    DatabaseHelper db;
    private Enfant child;
    private RelativeLayout easyButton;
    private RelativeLayout mediumButton;
    private RelativeLayout hardButton;

    private View.OnClickListener easyclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SoloActivityDifficultySelector.this, SoloActivityEasy.class);
            intent.putExtra("child", child);
            startActivity(intent);
        }
    };


    private View.OnClickListener mediumclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SoloActivityDifficultySelector.this, SoloActivityMedium.class);
            intent.putExtra("child", child);
            startActivity(intent);
        }
    };


    private View.OnClickListener hardclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SoloActivityDifficultySelector.this, SoloActivityHard.class);
            intent.putExtra("child", child);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_difficulty_selector);

        Intent myIntent = getIntent(); // gets the previously created intent
        child = (Enfant) myIntent.getSerializableExtra("child");

        // init button
        easyButton = findViewById(R.id.easyDifficultyID);
        mediumButton = findViewById(R.id.mediumDifficultyID);
        hardButton = findViewById(R.id.hardDifficultyID);

        easyButton.setOnClickListener(easyclickListener);
        mediumButton.setOnClickListener(mediumclickListener);
        hardButton.setOnClickListener(hardclickListener);


        Toast toast = Toast.makeText(getApplicationContext(),"Hello " + child.getNom(),Toast. LENGTH_SHORT);
        toast.show();

        // Setup DB:
        db = new DatabaseHelper(getApplicationContext());

    }
}