package fr.insacvl.educations.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.insacvl.educations.modele.Enfant;
import fr.insacvl.educations.modele.Mot;
import fr.insacvl.educations.modele.Package;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DIM";
    private static final int DATABASE_VERSION = 8;
    public final static String DATABASE_PATH ="/data/data/fr.insacvl.educations/databases/";
    private static final String DATABASE_NAME = "MotsDeLaSemaine";

    private static final String TABLE_MOTS = "mots";
    private static final String TABLE_ENFANTS = "enfants";
    private static final String TABLE_PACKAGE = "package";
    private static final String TABLE_ENFANTSPACKAGES = "enfantspackages";

    // NOTES Mots - column names
    private static final String KEY_MOTS_ID = "mot_id";
    private static final String KEY_MOTS_MOT = "mot";
    private static final String KEY_MOTS_SCORE = "score";
    private static final String KEY_MOTS_CREATED_AT = "mot_created";
    private static final String KEY_MOTS_ENFANT = "mot_enfant_id";
    private static final String KEY_MOTS_PACKAGE = "mot_package_id";

    // NOTES Enfants - column names
    private static final String KEY_ENFANTS_ID = "enfant_id";
    private static final String KEY_ENFANTS_NOM = "enfant_nom";
    private static final String KEY_ENFANTS_XP = "enfant_xp";

    // NOTES Packages - column names
    private static final String KEY_PACKAGE_ID = "package_id";
    private static final String KEY_PACKAGE_NOM = "package_nom";

    // NOTES EnfantsPackages - column names
    private static final String KEY_ENFANTSPACKAGES_ENFANT = "enfantspackages_enfant_id";
    private static final String KEY_ENFANTSPACKAGES_PACKAGE = "enfantspackages_package_id";

    private static final String CREATE_TABLE_MOTS = "CREATE TABLE "
            + TABLE_MOTS + "("
            + KEY_MOTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_MOTS_MOT + " TEXT,"
            + KEY_MOTS_SCORE + " INTEGER,"
            + KEY_MOTS_CREATED_AT + " DATETIME,"
            + KEY_MOTS_ENFANT + " INTEGER,"
            + KEY_MOTS_PACKAGE + " INTEGER,"
            + " FOREIGN KEY(" + KEY_MOTS_ENFANT + ") REFERENCES " + TABLE_ENFANTS + "(" + KEY_ENFANTS_ID + "),"
            + " FOREIGN KEY(" + KEY_MOTS_PACKAGE + ") REFERENCES " + TABLE_PACKAGE + "(" + KEY_PACKAGE_ID + ")"
            + ")";

    private static final String CREATE_TABLE_ENFANTS = "CREATE TABLE "
            + TABLE_ENFANTS + "("
            + KEY_ENFANTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ENFANTS_NOM + " TEXT UNIQUE,"
            + KEY_ENFANTS_XP + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_PACKAGE = "CREATE TABLE "
            + TABLE_PACKAGE + "("
            + KEY_PACKAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_PACKAGE_NOM + " TEXT UNIQUE"
            + ")";

    private static final String CREATE_TABLE_ENFANTSPACKAGES = "CREATE TABLE "
            + TABLE_ENFANTSPACKAGES + "("
            + KEY_ENFANTSPACKAGES_ENFANT + " INTEGER,"
            + KEY_ENFANTSPACKAGES_PACKAGE + " INTEGER,"
            + " PRIMARY KEY(" + KEY_ENFANTSPACKAGES_ENFANT + "," + KEY_ENFANTSPACKAGES_PACKAGE + "),"
            + " FOREIGN KEY(" + KEY_ENFANTSPACKAGES_ENFANT + ") REFERENCES " + TABLE_ENFANTS + "(" + KEY_ENFANTS_ID + "),"
            + " FOREIGN KEY(" + KEY_ENFANTSPACKAGES_PACKAGE + ") REFERENCES " + TABLE_PACKAGE + "(" + KEY_PACKAGE_ID + ")"
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
            Log.w(LOG, "CREATE_TABLE_ENFANTSPACKAGES     : ");
            db.execSQL(CREATE_TABLE_ENFANTSPACKAGES);
            Log.w(LOG, "CREATE_TABLE_ENFANTS     : ");
            db.execSQL(CREATE_TABLE_ENFANTS);
            Log.w(LOG, "CREATE_TABLE_PACKAGE     : ");
            db.execSQL(CREATE_TABLE_PACKAGE);
            Log.w(LOG, "CREATE_TABLE_MOTS     : ");
            db.execSQL(CREATE_TABLE_MOTS);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENFANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACKAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENFANTSPACKAGES);
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
        return new Enfant(id, nom, 0);
    }

    public Mot addNewMot (String mot, long id_enfant){
        return addNewMot(mot,id_enfant,0);
    }

    public Mot addNewMot (String mot, long id_enfant, long id_package){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues motBDD = new ContentValues();
        motBDD.put(KEY_MOTS_MOT, mot);
        motBDD.put(KEY_MOTS_CREATED_AT, getDateTime());
        motBDD.put(KEY_MOTS_ENFANT, id_enfant);
        motBDD.put(KEY_MOTS_SCORE, 0);
        motBDD.put(KEY_MOTS_PACKAGE,id_package);

        long id = db.insert(TABLE_MOTS, null, motBDD);
        if (id != -1)
            return new Mot(mot,  0, id, id_enfant, getDateTime(), id_package);
        else
            return null;
    }

    public void updateMot (Mot mot){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues motBDD = new ContentValues();
        motBDD.put(KEY_MOTS_MOT, mot.getContenu());
        motBDD.put(KEY_MOTS_CREATED_AT, mot.getCreated_at());
        motBDD.put(KEY_MOTS_ENFANT, mot.getId_enfant());
        motBDD.put(KEY_MOTS_SCORE, mot.getScore());
        motBDD.put(KEY_MOTS_PACKAGE, mot.getId_package());
        db.update(TABLE_MOTS, motBDD, KEY_MOTS_ID + " = ? AND " + KEY_MOTS_ENFANT + " = ?", new String[] { String.valueOf(mot.getId()), String.valueOf(mot.getIdEnfant())});
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
        mot.setId_package(c.getLong(c.getColumnIndex(KEY_MOTS_PACKAGE)));
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
                mot.setId_package(c.getLong(c.getColumnIndex(KEY_MOTS_PACKAGE)));
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
        db.delete(TABLE_MOTS , KEY_MOTS_ENFANT + " = ?",
                new String[] { String.valueOf(enfant_id) });
        db.delete(TABLE_ENFANTSPACKAGES, KEY_ENFANTSPACKAGES_ENFANT + " = ?",
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
                mot.setId_package(c.getLong(c.getColumnIndex(KEY_MOTS_PACKAGE)));
                mots.add(mot);
            } while (c.moveToNext());
        }

        c.close();
        return mots;
    }

    public Package addNewPackage(String test) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues motBDD = new ContentValues();
        motBDD.put(KEY_PACKAGE_NOM,test);
        long id = db.insert(TABLE_PACKAGE, null, motBDD);
        if (id != -1)
            return new Package(id, test);
        else
            return null;
    }

    public List<Package> getAllPackage() {
        List<Package> packages = new ArrayList<Package>();
        String selectQuery = "SELECT  * FROM " + TABLE_PACKAGE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Package newPackage = new Package();
                newPackage.setId(c.getLong(c.getColumnIndex(KEY_PACKAGE_ID)));
                newPackage.setNom(c.getString(c.getColumnIndex(KEY_PACKAGE_NOM)));
                packages.add(newPackage);
            } while (c.moveToNext());
        }

        c.close();
        return packages;
    }

    public void deletePackage(long package_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PACKAGE, KEY_PACKAGE_ID + " = ?",
                new String[] { String.valueOf(package_id) });
        db.delete(TABLE_MOTS , KEY_MOTS_PACKAGE + " = ?",
                new String[] { String.valueOf(package_id) });
    }

    public void addPackageForEnfant(long enfant_id, long package_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues motBDD = new ContentValues();
        motBDD.put(KEY_ENFANTSPACKAGES_ENFANT, enfant_id);
        motBDD.put(KEY_ENFANTSPACKAGES_PACKAGE, package_id);
        db.insert(TABLE_ENFANTSPACKAGES, null, motBDD);
    }

    public List<Package> getPackageByEnfant(long enfant_id){
        List<Package> packages = new ArrayList<Package>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_PACKAGE
                + " JOIN " + TABLE_ENFANTSPACKAGES
                + " WHERE " + KEY_ENFANTSPACKAGES_ENFANT + " = " + enfant_id
                + " AND " + KEY_ENFANTSPACKAGES_PACKAGE + " = " + KEY_PACKAGE_ID;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            do {
                Package newPackage = new Package();
                newPackage.setId(c.getLong(c.getColumnIndex(KEY_PACKAGE_ID)));
                newPackage.setNom(c.getString(c.getColumnIndex(KEY_PACKAGE_NOM)));
                packages.add(newPackage);
            } while (c.moveToNext());
        }

        c.close();
        return packages;
    }

    public List<Enfant> getEnfantByPackage(long package_id){
        List<Enfant> enfants = new ArrayList<Enfant>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ENFANTS
                + " JOIN " + TABLE_ENFANTSPACKAGES
                + " WHERE " + KEY_ENFANTSPACKAGES_PACKAGE + " = " + package_id
                + " AND " + KEY_ENFANTSPACKAGES_ENFANT + " = " + KEY_ENFANTS_ID;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            do {
                Enfant enfant = new Enfant();
                enfant.setId(c.getLong(c.getColumnIndex(KEY_ENFANTS_ID)));
                enfant.setNom(c.getString(c.getColumnIndex(KEY_ENFANTS_NOM)));
                enfant.setXp(c.getInt(c.getColumnIndex(KEY_ENFANTS_XP)));
                enfants.add(enfant);
            } while (c.moveToNext());
        }
        c.close();
        return enfants;
    }

    public List<Mot> getAllMotsByPackage(long package_id){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Mot> mots = new ArrayList<Mot>();
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_MOTS
                + " WHERE " + KEY_MOTS_PACKAGE + " = " + package_id
                + " AND " + KEY_MOTS_ENFANT + " = " + 0;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()){
            do {
                Mot mot = new Mot();
                mot.setId(c.getLong(c.getColumnIndex(KEY_MOTS_ID)));
                mot.setContenu(c.getString(c.getColumnIndex(KEY_MOTS_MOT)));
                mot.setScore(c.getInt(c.getColumnIndex(KEY_MOTS_SCORE)));
                mot.setId_enfant(c.getLong(c.getColumnIndex(KEY_MOTS_ENFANT)));
                mot.setCreated_at(c.getString(c.getColumnIndex(KEY_MOTS_CREATED_AT)));
                mot.setId_package(package_id);
                mots.add(mot);
            } while (c.moveToNext());
        }
        c.close();
        return mots;
    }

    public Enfant getChildrenByName(String chlidren_name){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ENFANTS
                + " WHERE " + KEY_ENFANTS_NOM + " = " + chlidren_name;
        Cursor c = db.rawQuery(selectQuery, null);
        Enfant enfant = new Enfant();
        if (c.moveToFirst()){
            enfant.setId(c.getLong(c.getColumnIndex(KEY_ENFANTS_ID)));
            enfant.setNom(c.getString(c.getColumnIndex(KEY_ENFANTS_NOM)));
            enfant.setXp(c.getInt(c.getColumnIndex(KEY_ENFANTS_XP)));
        }
        c.close();
        return enfant;
    }

    public void removeChildrenFromPackage(long id_package, String children_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Enfant enfant = getChildrenByName(children_name);
        db.delete(TABLE_MOTS,KEY_MOTS_PACKAGE + " = ? AND " + KEY_MOTS_ENFANT + " = ?", new String[] { String.valueOf(id_package), String.valueOf(enfant.getId())});
        db.delete(TABLE_ENFANTSPACKAGES, KEY_ENFANTSPACKAGES_PACKAGE + " =? AND " + KEY_ENFANTSPACKAGES_ENFANT + " = ?", new String[] { String.valueOf(id_package), String.valueOf(enfant.getId())});
    }

    public void newPackageForEnfant(long id_package, String children_name){
        Enfant enfant = getChildrenByName(children_name);
        List<Package> listePackage = getPackageByEnfant(enfant.getId());
        boolean update = false;
        for (Package p:listePackage){
            if (p.getId() == id_package){
                update = true;
                break;
            }
        }
        if (update){
            List<Mot> listeMotEnfant = getAllMotsByIDEnfant(enfant.getId());
            List<Mot> listeMotPackage = getAllMotsByPackage(id_package);
            List<Long> listeIdMotEnfant = new ArrayList<>();
            for (Mot m :listeMotEnfant){
                listeIdMotEnfant.add(m.getId());
            }
            for (Mot m: listeMotPackage){
                if (!listeIdMotEnfant.contains(m.getId())){
                    addNewMot(m.getContenu(), enfant.getId(), id_package);
                }
            }
        }else {
            addPackageForEnfant(enfant.getId(), id_package);
            List<Mot> listeMot = getAllMotsByPackage(id_package);
            for (Mot m : listeMot) {
                addNewMot(m.getContenu(), enfant.getId(), id_package);
            }
        }
    }

    public Package getPackage (long id_package){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_PACKAGE
                + " WHERE " + KEY_PACKAGE_ID + " = " + id_package;
        Cursor c = db.rawQuery(selectQuery, null);
        Package aPackage = new Package();
        if (c.moveToFirst()){
            aPackage.setId(c.getLong(c.getColumnIndex(KEY_PACKAGE_ID)));
            aPackage.setNom(c.getString(c.getColumnIndex(KEY_PACKAGE_NOM)));
        }
        c.close();
        return aPackage;
    }


    public List<Mot> getAllMotsFromWeek(long id_enfant) {

        List<Mot> mots = new ArrayList<Mot>();
        String selectQuery = "SELECT  * FROM " + TABLE_MOTS
                + " WHERE " + KEY_MOTS_ENFANT + " = " + id_enfant;

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        String dateS;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateAJD = new Date();
        Calendar calendarBDD = Calendar.getInstance();
        Calendar calendarOfDay = Calendar.getInstance();
        calendarOfDay.setTime(dateAJD);
        if (c.moveToFirst()) {
            do {
                dateS = c.getString(c.getColumnIndex(KEY_MOTS_CREATED_AT));
                try {
                    calendarBDD.setTime(format.parse(dateS));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //On regarde si on est bien dans la même semaine
                if (calendarBDD.get(Calendar.WEEK_OF_YEAR) == calendarOfDay.get(Calendar.WEEK_OF_YEAR)){
                    Mot mot = new Mot();
                    mot.setId(c.getLong(c.getColumnIndex(KEY_MOTS_ID)));
                    mot.setScore(c.getInt(c.getColumnIndex(KEY_MOTS_SCORE)));
                    mot.setContenu(c.getString(c.getColumnIndex(KEY_MOTS_MOT)));
                    mot.setId_enfant(c.getLong(c.getColumnIndex(KEY_MOTS_ENFANT)));
                    mot.setCreated_at(c.getString(c.getColumnIndex(KEY_MOTS_CREATED_AT)));
                    mot.setId_package(c.getLong(c.getColumnIndex(KEY_MOTS_PACKAGE)));
                    mots.add(mot);
                }
            } while (c.moveToNext());
        }

        c.close();
        return mots;
    }
}

