package fr.insacvl.educations.activityAdmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import fr.insacvl.educations.R;
import fr.insacvl.educations.modele.Package;

public class ManagePackageActivity extends Activity {

    private Package selectPackage;

    private View.OnClickListener modifKidPackageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ManagePackageActivity.this, AddKidPackageActivity.class);
            intent.putExtra("package",selectPackage);
            startActivity(intent);
        }
    };


    private View.OnClickListener modifWordPackageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ManagePackageActivity.this, AddWordPackageActivity.class);
            intent.putExtra("package",selectPackage);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_package);

        Intent lastIntent = getIntent();
        selectPackage = (Package) lastIntent.getSerializableExtra("package");

        RelativeLayout modifKidPackage = findViewById(R.id.modifKidPackage);
        RelativeLayout modifWordPackage = findViewById(R.id.modifWordPackage);
        modifKidPackage.setOnClickListener(modifKidPackageClick);
        modifWordPackage.setOnClickListener(modifWordPackageClick);
    }
}