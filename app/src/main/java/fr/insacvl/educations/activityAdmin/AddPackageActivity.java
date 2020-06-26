package fr.insacvl.educations.activityAdmin;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.insacvl.educations.R;
import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Package;

public class AddPackageActivity extends ListActivity {

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
        setContentView(R.layout.activity_add_package);

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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Package selectedFromList = list.get(position);
        final Intent intent = new Intent(AddPackageActivity.this, ManagePackageActivity.class);
        intent.putExtra("package", selectedFromList);

        AlertDialog.Builder builder = new AlertDialog.Builder(AddPackageActivity.this);
        builder.setTitle("Que voulez-vous faire ?");
        builder.setMessage("Vous pouvez soit modifier le package, soit le supprimer.");
        builder.setPositiveButton("Modifier le package", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Supprimez le package", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deletePackage(selectedFromList.getId());
                list.remove(selectedFromList);
                adapter.remove(selectedFromList.getNom());
                adapter.notifyDataSetChanged();
            }
        });

        builder.show();

    }
}