package fr.insacvl.educations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;

public class SelectKidSoloActivity extends Activity {
    DatabaseHelper db;
    List<Enfant> list;
    ListView listEnfant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_kid_solo);
        listEnfant = (ListView) findViewById(R.id.listViewEnfant);

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
                intent.putExtra("child", selectedFromList);
                startActivity(intent);
            }
        });
    }
}