package fr.insacvl.educations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

public class AdminActivity extends AppCompatActivity {

    private View.OnClickListener modifKidClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(AdminActivity.this, SelectKidAdminActivity.class);
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
    }
}