package fr.insacvl.educations.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DIM";
    private static final int DATABASE_VERSION = 1;
    public final static String DATABASE_PATH ="/data/data/fr.insacvl.educations/databases/";
    private static final String DATABASE_NAME = "MotsDeLaSemaine";

    private static final String TABLE_MOTS = "mots";
    private static final String TABLE_ENFANTS = "enfants";

    // NOTES Mots - column names
    private static final String KEY_MOTS_ID = "mot_id";
    private static final String KEY_MOTS_MOT = "mot";
    private static final String KEY_MOTS_SCORE = "score";
    private static final String KEY_MOTS_CREATED_AT = "mot_created";
    private static final String KEY_MOTS_ENFANT = "mot_enfant_id";

    // NOTES Enfants - column nmaes
    private static final String KEY_ENFANTS_ID = "enfant_id";
    private static final String KEY_ENFANTS_NOM = "enfant_nom";

    private static final String CREATE_TABLE_MOTS = "CREATE TABLE "
            + TABLE_MOTS + "("
            + KEY_MOTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_MOTS_MOT + " TEXT UNIQUE,"
            + KEY_MOTS_SCORE + " INTEGER,"
            + KEY_MOTS_CREATED_AT + " DATETIME,"
            + KEY_MOTS_ENFANT + " INTEGER,"
            + " FOREIGN KEY(" + KEY_MOTS_ENFANT + ") REFERENCES " + TABLE_ENFANTS + "(" + KEY_ENFANTS_ID + ")"
            + ")";

    private static final String CREATE_TABLE_ENFANTS = "CREATE TABLE "
            + TABLE_ENFANTS + "("
            + KEY_ENFANTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ENFANTS_NOM + " TEXT UNIQUE"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        boolean dbExist = checkDataBase();

        if(dbExist) {
            Log.w("DIM", "LA BD EXISTE");
        }
        else{
            Log.w("DIM", "LA BD N'EXISTE PAS");
            db.execSQL(CREATE_TABLE_ENFANTS);
            Log.w(LOG, "CREATE_TABLE_MOTS     : ");
            db.execSQL(CREATE_TABLE_MOTS);
            Log.w(LOG, "CREATE_TABLE_ENFANTS      : ");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENFANTS);
    }

    //Check database already exist or not
    private boolean checkDataBase(){

        boolean checkDB = false;

        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            Log.v("DIM", "myPath : " + myPath);

            File dbfile = new File(myPath);
            Log.v("DIM", "dbfile : " + dbfile);
            checkDB = dbfile.exists();
            Log.v("DIM", "checkDB : " + checkDB);
        }
        catch(SQLiteException e){
            Log.v("DIM", "PAS SUPPOSE PASSER ICI JAMAIS");
        }
        return checkDB;
    }

    public Enfant addNewEnfant (String nom){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues enfantBDD = new ContentValues();
        enfantBDD.put(KEY_ENFANTS_NOM, nom);

        long id = db.insert(TABLE_ENFANTS, null, enfantBDD);
        return new Enfant(id, nom);
    }

    public Mot addNewMot (String mot, long id_enfant){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues motBDD = new ContentValues();
        motBDD.put(KEY_MOTS_MOT, mot);
        motBDD.put(KEY_MOTS_CREATED_AT, getDateTime());
        motBDD.put(KEY_MOTS_ENFANT, id_enfant);
        motBDD.put(KEY_MOTS_SCORE, 0);

        long id = db.insert(TABLE_MOTS, null, motBDD);
        if (id != -1)
            return new Mot(mot,  0, id, id_enfant, getDateTime());
        else
            return null;
    }

    public Enfant getEnfant(long enfant_id){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ENFANTS
                + " WHERE " + KEY_ENFANTS_ID + " = " + enfant_id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c == null) {
            Log.i(LOG, "Aucun enfant avec l'id " + enfant_id + " existe");
            return null;
        }
        c.moveToFirst();
        Enfant enfant = new Enfant();
        enfant.setId(c.getLong(c.getColumnIndex(KEY_ENFANTS_ID)));
        enfant.setNom(c.getString(c.getColumnIndex(KEY_ENFANTS_NOM)));
        c.close();
        return enfant;
    }

    public Mot getMot(long mot_id){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_MOTS
                + " WHERE " + KEY_MOTS_ID + " = " + mot_id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c == null) {
            Log.i(LOG, "Aucun mot avec l'id " + mot_id + " existe");
            return null;
        }
        c.moveToFirst();
        Mot mot = new Mot();
        mot.setId(c.getLong(c.getColumnIndex(KEY_MOTS_ID)));
        mot.setScore(c.getInt(c.getColumnIndex(KEY_MOTS_SCORE)));
        mot.setContenu(c.getString(c.getColumnIndex(KEY_MOTS_MOT)));
        mot.setId_enfant(c.getLong(c.getColumnIndex(KEY_MOTS_ENFANT)));
        mot.setCreated_at(c.getString(c.getColumnIndex(KEY_MOTS_CREATED_AT)));
        c.close();
        return mot;
    }

    public List<Mot> getAllMots() {

        List<Mot> mots = new ArrayList<Mot>();
        String selectQuery = "SELECT  * FROM " + TABLE_MOTS;

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Mot mot = new Mot();
                mot.setId(c.getLong(c.getColumnIndex(KEY_MOTS_ID)));
                mot.setScore(c.getInt(c.getColumnIndex(KEY_MOTS_SCORE)));
                mot.setContenu(c.getString(c.getColumnIndex(KEY_MOTS_MOT)));
                mot.setId_enfant(c.getLong(c.getColumnIndex(KEY_MOTS_ENFANT)));
                mot.setCreated_at(c.getString(c.getColumnIndex(KEY_MOTS_CREATED_AT)));

                mots.add(mot);
            } while (c.moveToNext());
        }

        c.close();
        return mots;
    }

    public List<Enfant> getAllEnfants() {

        List<Enfant> enfants = new ArrayList<Enfant>();
        String selectQuery = "SELECT  * FROM " + TABLE_ENFANTS;

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Enfant enfant = new Enfant();
                enfant.setId(c.getLong(c.getColumnIndex(KEY_ENFANTS_ID)));
                enfant.setNom(c.getString(c.getColumnIndex(KEY_ENFANTS_NOM)));
                enfants.add(enfant);
            } while (c.moveToNext());
        }

        c.close();
        return enfants;
    }

    public int getMotsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MOTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public void deleteMot(long mot_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOTS, KEY_MOTS_ID + " = ?",
                new String[] { String.valueOf(mot_id) });
    }

    public void deleteEnfant(long enfant_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENFANTS, KEY_ENFANTS_ID + " = ?",
                new String[] { String.valueOf(enfant_id) });
    }

    public void deleteEverything(){
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+TABLE_MOTS;
        db.execSQL(clearDBQuery);
        clearDBQuery = "DELETE FROM "+TABLE_ENFANTS;
        db.execSQL(clearDBQuery);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public List<Mot> getAllMotsByIDEnfant(long id_enfant) {

        List<Mot> mots = new ArrayList<Mot>();
        String selectQuery = "SELECT  * FROM " + TABLE_MOTS
                + " WHERE " + KEY_MOTS_ENFANT + " = " + id_enfant;

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Mot mot = new Mot();
                mot.setId(c.getLong(c.getColumnIndex(KEY_MOTS_ID)));
                mot.setScore(c.getInt(c.getColumnIndex(KEY_MOTS_SCORE)));
                mot.setContenu(c.getString(c.getColumnIndex(KEY_MOTS_MOT)));
                mot.setId_enfant(c.getLong(c.getColumnIndex(KEY_MOTS_ENFANT)));
                mot.setCreated_at(c.getString(c.getColumnIndex(KEY_MOTS_CREATED_AT)));

                mots.add(mot);
            } while (c.moveToNext());
        }

        c.close();
        return mots;
    }
}

