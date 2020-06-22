package fr.insacvl.educations;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;

public class SelectKidSoloActivity extends ListActivity {
    DatabaseHelper db;
    ArrayAdapter<String> adapter;
    List<Enfant> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_kid_solo);

        db = new DatabaseHelper(getApplicationContext());
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
        Intent intent = new Intent(this, SoloActivityDifficultySelector.class);
        Enfant selectedFromList = list.get(position);
        intent.putExtra("child", selectedFromList);
        startActivity(intent);
    }


}