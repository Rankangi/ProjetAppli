package fr.insacvl.educations.activitySolo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import fr.insacvl.educations.R;
import fr.insacvl.educations.modele.Enfant;

public class SoloActivityRecapSemaine extends Activity {
    private ArrayList<String> tentatives, listMot;
    private GridView tentativeGrid, listMotGrid;
    SimpleAdapter adapterTentative;
    ArrayList<HashMap<String, String>> arrayListTentative;
    SimpleAdapter adapterListMot;
    ArrayList<HashMap<String, String>> arrayListMot;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recapitulatif_mot_semaine);

        Intent myIntent = getIntent(); // gets the previously created intent
        tentatives = (ArrayList<String>) myIntent.getStringArrayListExtra("tentative");
        listMot = (ArrayList<String>) myIntent.getStringArrayListExtra("listMot");

        tentativeGrid = (GridView) findViewById(R.id.tentatives);
        listMotGrid = (GridView) findViewById(R.id.motsCorrects);
        arrayListMot = new ArrayList<>();
        arrayListTentative = new ArrayList<>();

        HashMap<String, String> map;
        for (String str : tentatives) {
            map = new HashMap<String, String>();
            map.put("nom", str);
            arrayListTentative.add(map);
        }
        adapterTentative = new SimpleAdapter(this.getBaseContext(), arrayListTentative, R.layout.recap_mot_semaine,
                new String[]{"nom"}, new int[]{R.id.mot});
        tentativeGrid.setAdapter(adapterTentative);
        for (String str : listMot) {
            map = new HashMap<String, String>();
            map.put("nom", str);
            arrayListMot.add(map);
        }
        adapterListMot = new SimpleAdapter(this.getBaseContext(), arrayListMot, R.layout.recap_mot_semaine,
                new String[]{"nom"}, new int[]{R.id.mot});
        listMotGrid.setAdapter(adapterListMot);

    }
}
