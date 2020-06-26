package fr.insacvl.educations.activityAdmin;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import fr.insacvl.educations.R;
import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Package;

public class AddWordPackageActivity extends ListActivity {

    EditText namePackage;
    ArrayAdapter<String> adapter;
    DatabaseHelper db;
    List<Package> list;

    private View.OnKeyListener keylistener = new View.OnKeyListener(){

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                addPackage(view);
                namePackage.setText("");
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word_package);

        //On récupère la BDD
        db = new DatabaseHelper(getApplicationContext());

        // On récupère le champ de texte et on set un listener pour la touche entré
        namePackage = findViewById(R.id.getPackage);
        namePackage.setOnKeyListener(keylistener);

        // On récupère la liste des Packages de la BDD
        list = db.getAllPackage();

        List<String> str = new ArrayList<>();
        for (Package p:list){
            str.add(p.getNom());
        }
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, str);
        setListAdapter(adapter);
    }

    public void addPackage(View view) {
        Editable text = namePackage.getText();
        if(String.valueOf(text).equals("")){
            //check si rien
            return;
        }
        Package newPackage = db.addNewPackage(text.toString());
        if (newPackage != null && !newPackage.getNom().equals("")){
            list.add(newPackage);
            adapter.add(newPackage.getNom());
            adapter.notifyDataSetChanged();
        }
        namePackage.setText("");
    }
}