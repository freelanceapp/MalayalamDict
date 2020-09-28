package com.elamblakatt.dict_eng_malayalam.Meaning;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.util.ArrayList;


public class MeaningDatabse extends SQLiteOpenHelper {


    private SQLiteDatabase db;
    public static final String DATABASE_NAME = "eng_pun_dict";
    private String DATABASE_PATH = null;
    private static final int DATABASE_VERSION = 1;
    private static  Context mContext;

    public MeaningDatabse(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        DATABASE_PATH = getDatabaseFolder()+DATABASE_NAME+".sqlite";
//        db = getWritableDatabase();
    }


    public void initialiseDB(){
        db = getWritableDatabase();
    }

    public static String getDatabaseFolder() {

        if (android.os.Build.VERSION.SDK_INT >= 17)
           return mContext.getApplicationInfo().dataDir + "/databases/";
        else
          return    "/data/data/" + mContext.getPackageName() + "/databases/";
//        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+PACKAGE_NAME+"/databases/";
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        try {
            if (db != null) {
                if (db.isOpen()) {
                    return db;
                }
            }
            return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
            db = null;
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }


    public WordDetail getWordDetails(String wordSel){
        initialiseDB();
        WordDetail wrdDetail=new WordDetail();
        Cursor cursor=db.rawQuery("SELECT word,type,synonym,antonym,meaning,example FROM dictEnglish where word='"+wordSel.toLowerCase()+"'",null);
        String strEng = "";
        try {
            int idColumnIndexWord = cursor.getColumnIndex("word");
            int idColumnIndexWordType = cursor.getColumnIndex("type");
            int idColumnIndexSynomyns = cursor.getColumnIndex("synonym");
            int idColumnIndexAnomyns = cursor.getColumnIndex("antonym");
            int idColumnIndexMeaning = cursor.getColumnIndex("meaning");
            int idColumnIndexExample = cursor.getColumnIndex("example");


            while(cursor.moveToNext()) {

                wrdDetail.setWord(cursor.getString(idColumnIndexWord));
                wrdDetail.setWord_type(cursor.getString(idColumnIndexWordType));
                wrdDetail.setSynonyms(cursor.getString(idColumnIndexSynomyns));
                wrdDetail.setAntonyms(cursor.getString(idColumnIndexAnomyns));
                wrdDetail.setMeaning(cursor.getString(idColumnIndexMeaning));
                wrdDetail.setExample(cursor.getString(idColumnIndexExample));

            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return wrdDetail;
    }

}
