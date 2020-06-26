package fr.insacvl.educations.activityAdmin;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import fr.insacvl.educations.R;
import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;
import fr.insacvl.educations.modele.Package;

public class AddWordPackageActivity extends ListActivity {

    Package selectPackage;
    DatabaseHelper db;
    EditText getMot;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word_package);

        Intent lastIntent = getIntent();
        selectPackage = (Package) lastIntent.getSerializableExtra("package");

        getMot = findViewById(R.id.getMot);

        db = new DatabaseHelper(getApplicationContext());
        List<Mot> listeMot = db.getAllMotsByPackage(selectPackage.getId());
        List<String> str = new ArrayList<String>();
        for (Mot m:listeMot){
            str.add(m.getContenu());
        }
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, str);
        setListAdapter(adapter);
    }

    public void addWord(View view) {
        List<Enfant> listEnfants = db.getEnfantByPackage(selectPackage.getId());
        String text = getMot.getText().toString();
        if (!text.equals("")) {
            if(text.charAt(text.length()-1)==' '){
                text = text.substring(0,text.length()-1);
            }
            Mot mot = null;
            for (Enfant e:listEnfants){
                mot = db.addNewMot(text,e.getId(),selectPackage.getId());
            }
            mot = db.addNewMot(text, 0, selectPackage.getId());
            if (mot != null && !mot.getContenu().equals("")) {
                adapter.add(mot.getContenu());
                adapter.notifyDataSetChanged();
            }
        }
    }
}