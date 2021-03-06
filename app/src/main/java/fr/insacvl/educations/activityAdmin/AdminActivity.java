package fr.insacvl.educations.activityAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import fr.insacvl.educations.R;

public class AdminActivity extends AppCompatActivity {

    private View.OnClickListener modifKidClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(AdminActivity.this, SelectKidAdminActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener modifWordPackageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(AdminActivity.this, AddPackageActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RelativeLayout modifKid = findViewById(R.id.modifKid);
        modifKid.setOnClickListener(modifKidClick);

        RelativeLayout modifWordPackage = findViewById(R.id.modifWordPackage);
        modifWordPackage.setOnClickListener(modifWordPackageClick);
    }
}