package fr.insacvl.educations;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.vision.text.TextRecognizer;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.insacvl.educations.helper.DatabaseHelper;
import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;

public class AddWordActivity extends ListActivity {

    private static final int PERMISSON_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    DatabaseHelper db;
    ArrayAdapter<String> adapter;
    private EditText textbox;
    private Button photoButton;
    private TextView photo_txt;
    Uri imgUri;
    private Bitmap bitmapImg;
    Enfant child;
    List<Mot> list;

    private View.OnKeyListener keylistener = new View.OnKeyListener(){

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)&&(i==KeyEvent.KEYCODE_ENTER)){
                addWord(view);
                return true;
            }
            return false;
        }
    };

    private View.OnClickListener takePhotoListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            //sys os < marshmallow faut demander la runtime permission
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                        ||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    // pas les perm : erreur
                    String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    // Request popup
                    requestPermissions(permission,PERMISSON_CODE);
                }
                else{
                    // les perm sont déjà accéptées
                    openCamera();
                }
            }
            else{
                // l'os est < mashmallow
                openCamera();
            }
        }
    };

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Nouvelle Image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Prise par caméra");
        imgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        // Camera intent:
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(camIntent,IMAGE_CAPTURE_CODE);
    }

    // Gestion du resultat des perm :
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case PERMISSON_CODE:{
                if(grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    // perm popup accéptée
                    openCamera();
                }
                else {
                    // perm denied
                    Toast.makeText(this,"Permissions refusées",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // appel quand l'image est capturée
        if(resultCode == RESULT_OK){
            try {
                bitmapImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            Frame imageFrame = new Frame.Builder()
                    .setBitmap(bitmapImg)
                    .build();
            String imageText = "";
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                imageText = textBlock.getValue();                   // return string
            }
            photo_txt.setText(imageText);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        Intent myIntent = getIntent(); // gets the previously created intent
        child = (Enfant) myIntent.getSerializableExtra("child");

        Toast toast = Toast.makeText(getApplicationContext(),"Hello " + child.getNom(),Toast. LENGTH_SHORT);
        toast.show();

        db = new DatabaseHelper(getApplicationContext());
        Log.i("deubg","test1");
        photoButton = findViewById(R.id.takePicture);
        photoButton.setOnClickListener(takePhotoListener);
        photo_txt = findViewById(R.id.photo_txt);

        textbox = findViewById(R.id.getMot);
        textbox.setOnKeyListener(keylistener);
        list = db.getAllMotsByIDEnfant(child.getId());
        List<String> str = new ArrayList<>();
        for (Mot m: list) {
            str.add(m.getContenu() + " (" + m.getLevelOfScore() + ")");
        }
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, str);
        setListAdapter(adapter);
    }

    public void addWord(View view){
        Editable text = textbox.getText();
        if (!text.toString().equals("")) {
            Mot mot = db.addNewMot(text.toString(), child.getId());
            if (mot != null && !mot.getContenu().equals("")) {
                list.add(mot);
                adapter.add(mot.getContenu() + " (" + mot.getLevelOfScore() + ")");
                adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Mot selectedFromList = list.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous supprimer ce mot ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteMot(selectedFromList.getId());
                list.remove(selectedFromList);
                adapter.remove(selectedFromList.getContenu() + " (" + selectedFromList.getLevelOfScore() + ")");
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();

    }
}