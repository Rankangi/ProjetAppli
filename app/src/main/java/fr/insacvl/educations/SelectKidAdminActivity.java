package fr.insacvl.educations;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;

public class SelectKidAdminActivity extends ListActivity {

    DatabaseHelper db;
    ArrayAdapter<String> adapter;
    private EditText textbox;

    private View.OnKeyListener keylistener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                addChild(view);
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_kid);

        db = new DatabaseHelper(getApplicationContext());
        textbox = findViewById(R.id.getMot);
        textbox.setOnKeyListener(keylistener);
        List<Enfant> list = db.getAllEnfants();
        List<String> str = new ArrayList<>();
        for (Enfant e: list) {
            str.add(e.getNom());
            Log.i("DIM", e.getNom());
        }
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, str);
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, AdminActivity.class);
        String selectedFromList = (String) l.getItemAtPosition(position);
        intent.putExtra("child",selectedFromList);
        startActivity(intent);
    }

    public void addChild(View view) {
        Editable text = textbox.getText();
        Enfant enfant = db.addNewEnfant(text.toString()); // TODO : mettre l'id de l'enfant
        if (enfant != null){
            adapter.add(enfant.getNom());
            adapter.notifyDataSetChanged();
        }
    }
}