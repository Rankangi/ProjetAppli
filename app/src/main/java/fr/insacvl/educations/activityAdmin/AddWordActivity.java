package fr.insacvl.educations.activityAdmin;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.insacvl.educations.R;
import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;

public class AddWordActivity extends ListActivity {

    DatabaseHelper db;
    ArrayAdapter<String> adapter;
    private EditText textbox;
    Enfant child;
    List<Mot> list;

    private View.OnKeyListener keylistener = new View.OnKeyListener(){

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                addWord(view);
                textbox.setText("");
                return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        Intent myIntent = getIntent(); // gets the previously created intent
        child = (Enfant) myIntent.getSerializableExtra("child");

        Toast toast = Toast.makeText(getApplicationContext(),"Hello " + child.getNom(),Toast. LENGTH_SHORT);
        toast.show();

        db = new DatabaseHelper(getApplicationContext());
        textbox = findViewById(R.id.getMot);
        textbox.setOnKeyListener(keylistener);
        list = db.getAllMotsByIDEnfant(child.getId());
        List<String> str = new ArrayList<>();
        for (Mot m: list) {
            str.add(m.getContenu() + " (" + m.getLevelOfScore() + ")");
            Log.i("DIM", m.getContenu());
        }
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, str);
        setListAdapter(adapter);
    }

    public void addWord(View view){
        String text = textbox.getText().toString();
        if (!text.equals("")) {
            if(text.charAt(text.length()-1)==' '){
                text = text.substring(0,text.length()-1);
            }
            Mot mot = db.addNewMot(text, child.getId());
            if (mot != null && !mot.getContenu().equals("")) {
                list.add(mot);
                adapter.add(mot.getContenu() + " (" + mot.getLevelOfScore() + ")");
                adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Mot selectedFromList = list.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous supprimer ce mot ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteMot(selectedFromList.getId());
                list.remove(selectedFromList);
                adapter.remove(selectedFromList.getContenu() + " (" + selectedFromList.getLevelOfScore() + ")");
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }
}