package fr.insacvl.educations.activitySolo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.insacvl.educations.R;
import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;

public class SelectKidSoloActivity extends Activity {
    DatabaseHelper db;
    List<Enfant> list;
    GridView listEnfant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_kid_solo);
        listEnfant = (GridView) findViewById(R.id.listViewEnfant);

        db = new DatabaseHelper(getApplicationContext());
        list = db.getAllEnfants();
        ArrayList<HashMap<String, String>> str = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        String nomEnfant;
        String upperNomEnfant;
        for (Enfant e: list) {
            nomEnfant = e.getNom();
            upperNomEnfant = nomEnfant.substring(0,1).toUpperCase() + nomEnfant.substring(1);
            map = new HashMap<String, String>();
            map.put("nom", upperNomEnfant);
            map.put("id", ""+e.getId());
            str.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(), str, R.layout.card_enfant,
                new String[] {"nom"}, new int[] {R.id.nom_enfant});
        listEnfant.setAdapter(adapter);
        listEnfant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //HashMap<String, String> map = (HashMap<String, String>) listEnfant.getItemAtPosition(position);  // pour récup les données liées au bouton
                Intent intent = new Intent(SelectKidSoloActivity.this, SoloActivityDifficultySelector.class);
                Enfant selectedFromList = list.get(position);
                intent.putExtra("childId", selectedFromList.getId());
                startActivity(intent);
            }
        });
    }
}