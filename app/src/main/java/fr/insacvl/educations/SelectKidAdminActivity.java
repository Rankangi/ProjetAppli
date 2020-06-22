package fr.insacvl.educations;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
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
    List<Enfant> list;

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
        list = db.getAllEnfants();
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
        final Intent intent = new Intent(this, AddWordActivity.class);
        final Enfant selectedFromList = list.get(position);
        intent.putExtra("child", selectedFromList);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Que voulez-vous faire ?");
        builder.setMessage("Vous pouvez soit ajouter des mots à la liste ou soit supprimer ce nom.");
        builder.setPositiveButton("Ajouter des mots à la liste", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Supprimez ce nom", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteEnfant(selectedFromList.getId());
                list.remove(selectedFromList);
                adapter.remove(selectedFromList.getNom());
                adapter.notifyDataSetChanged();
            }
        });

        builder.show();

    }

    public void addChild(View view) {
        Editable text = textbox.getText();
        Enfant enfant = db.addNewEnfant(text.toString());
        list.add(enfant);
        if (enfant != null){
            adapter.add(enfant.getNom());
            adapter.notifyDataSetChanged();
        }
    }
}