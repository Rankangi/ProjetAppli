package fr.insacvl.educations;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import fr.insacvl.educations.helper.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;

    private View.OnClickListener clickSolo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, SelectKidSoloActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener clickMulti = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener clickAdmin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // On crée un listener pour écouter sur les boutons
        RelativeLayout solo = findViewById(R.id.solo);
        RelativeLayout multi = findViewById(R.id.multi);
        RelativeLayout admin = findViewById(R.id.admin);
        solo.setOnClickListener(clickSolo);
        // TODO: Activité multi
//        multi.setOnClickListener(clickMulti);
        admin.setOnClickListener(clickAdmin);

    }
}
