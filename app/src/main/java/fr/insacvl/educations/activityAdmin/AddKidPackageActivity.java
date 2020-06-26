package fr.insacvl.educations.activityAdmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import fr.insacvl.educations.R;
import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Package;

public class AddKidPackageActivity extends Activity {
    private DatabaseHelper db;
    private GridView gridView;
    private HashMap<RelativeLayout, Boolean> mapChild = new HashMap<>();
    private Package selectPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kid_package);
        gridView = findViewById(R.id.listViewEnfant);

        Intent lastIntent = getIntent();
        selectPackage = (Package) lastIntent.getSerializableExtra("package");

        db = new DatabaseHelper(getApplicationContext());
        final List<Enfant> list = db.getAllEnfants();
        final List<Enfant> listPackageEnfant = db.getEnfantByPackage(selectPackage.getId());


        ArrayAdapter<Enfant> adapter = new ArrayAdapter<Enfant>(this,0,list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                Enfant curentEnfant = list.get(position);

                if (convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.card_enfant, null, false);
                }

                RelativeLayout rl = convertView.findViewById(R.id.rl);
                Log.w("DIM", String.valueOf(listPackageEnfant.contains(curentEnfant)));
                if (!listPackageEnfant.contains(curentEnfant)){
                    rl.setBackground(ContextCompat.getDrawable(rl.getContext(), R.drawable.home_gradient_gray)); //to kkchose du gris
                }else{
                    rl.setBackground(ContextCompat.getDrawable(rl.getContext(), R.drawable.home_gradient_maths)); //to kkchose du gris
                }

                TextView test = convertView.findViewById(R.id.nom_enfant);
                test.setText(curentEnfant.getNom());

                return convertView;
            }
        };

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.w("DIM", String.valueOf(view));
                LinearLayout thisLayout = (LinearLayout) view;
                CardView cd = (CardView) thisLayout.getChildAt(0);
                RelativeLayout rl = (RelativeLayout) cd.getChildAt(0);
                if (!mapChild.containsKey(rl) || mapChild.get(rl) ){
                    mapChild.put(rl,false);
                    rl.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.home_gradient_maths));
                }else if (!mapChild.get(rl) ){
                    mapChild.put(rl,true);
                    rl.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.home_gradient_gray));
                }
                
            }
        });

    }
}