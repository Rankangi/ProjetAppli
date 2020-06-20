package fr.insacvl.educations;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;

public class AdminActivity extends ListActivity {

    DatabaseHelper db;
    ArrayAdapter<String> adapter;
    private EditText textbox;
    Enfant child;

    private View.OnKeyListener keylistener = new View.OnKeyListener(){

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                Editable text = textbox.getText();
                Mot mot = db.addNewMot(text.toString(), child.getId()); // TODO : mettre l'id de l'enfant
                if (mot != null){
                    adapter.add(mot.getContenu());
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent myIntent = getIntent(); // gets the previously created intent
        child = (Enfant) myIntent.getSerializableExtra("child");

        Toast toast = Toast.makeText(getApplicationContext(),"Hello " + child.getNom(),Toast. LENGTH_SHORT);
        toast.show();

        db = new DatabaseHelper(getApplicationContext());
        textbox = findViewById(R.id.getMot);
        textbox.setOnKeyListener(keylistener);
        List<Mot> list = db.getAllMotsByIDEnfant(child.getId());
        List<String> str = new ArrayList<>();
        for (Mot m: list) {
            str.add(m.getContenu());
            Log.i("DIM", m.getContenu());
        }
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, str);
        setListAdapter(adapter);
    }


    public void clearBDD(View view) {
        db.deleteEverything();
    }
}