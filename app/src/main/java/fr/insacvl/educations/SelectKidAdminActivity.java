package fr.insacvl.educations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;

public class SelectKidAdminActivity extends Activity {

    DatabaseHelper db;
    private EditText textbox;
    List<Enfant> list;
    GridView listEnfant;
    SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> str;

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
        textbox = findViewById(R.id.getMot);
        textbox.setOnKeyListener(keylistener);
        listEnfant = (GridView) findViewById(R.id.listViewEnfant);

        db = new DatabaseHelper(getApplicationContext());
        list = db.getAllEnfants();
        str = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        String nomEnfant;
        String upperNomEnfant;
        for (Enfant e : list) {
            nomEnfant = e.getNom();
            upperNomEnfant = nomEnfant.substring(0, 1).toUpperCase() + nomEnfant.substring(1);
            map = new HashMap<String, String>();
            map.put("nom", upperNomEnfant);
            map.put("id", "" + e.getId());
            str.add(map);
        }
        adapter = new SimpleAdapter(this.getBaseContext(), str, R.layout.card_enfant,
                new String[]{"nom"}, new int[]{R.id.nom_enfant});
        listEnfant.setAdapter(adapter);
        listEnfant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                final HashMap<String, String> map = (HashMap<String, String>) listEnfant.getItemAtPosition(position);  // pour récup les données liées au bouton
                final Intent intent = new Intent(SelectKidAdminActivity.this, AddWordActivity.class);
                final Enfant selectedFromList = list.get(position);
                intent.putExtra("child", selectedFromList);

                AlertDialog.Builder builder = new AlertDialog.Builder(SelectKidAdminActivity.this);
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
                        str.remove(map);
                        adapter.notifyDataSetChanged();
                    }
                });

                builder.show();
            }
        });
    }
    public void addChild(View view) {
        Editable text = textbox.getText();
        Enfant enfant = db.addNewEnfant(text.toString());
        if (enfant != null){
            list.add(enfant);
            HashMap<String, String> map = new HashMap<String, String>();
            String nomEnfant = enfant.getNom();
            String upperNomEnfant = nomEnfant.substring(0, 1).toUpperCase() + nomEnfant.substring(1);
            map.put("nom", upperNomEnfant);
            map.put("id", ""+enfant.getId());
            str.add(map);
            adapter.notifyDataSetChanged();
        }
        textbox.setText("");
    }
}