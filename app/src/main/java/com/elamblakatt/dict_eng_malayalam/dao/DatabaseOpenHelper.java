package com.elamblakatt.dict_eng_malayalam.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DatabaseOpenHelper {
    private static final String TAG = DatabaseOpenHelper.class.getSimpleName();
    private Context context;

    public DatabaseOpenHelper(Context context)  {
        this.context = context;
        if (!isDatabaseExists()){
            try {
                DatabaseOpenHelperSQLCipher dd=new DatabaseOpenHelperSQLCipher(context);//.getReadableDatabase();
                //  copyDatabase();
            } catch (Exception e) {
                Log.d(TAG,"Can't copy data base");
                e.printStackTrace();
            }
        }
    }
    public SQLiteDatabase getReadableDatabase(){
        return getDatabase(SQLiteDatabase.OPEN_READONLY);
    }
    public SQLiteDatabase getReadWritableDatabase(){
        return getDatabase(SQLiteDatabase.OPEN_READWRITE);
    }
    /* private SQLiteDatabase getDatabase(int flag){
         File dataBasePath = context.getDatabasePath(Database.DATABASE_NAME);

         SQLiteDatabase.loadLibs(context);


         File databaseFile = context.getDatabasePath(Database.DATABASE_NAME);
         SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "norwynnitin", null);
         return database;
     }*/
    private SQLiteDatabase getDatabase(int flag){
        File dataBasePath = context.getDatabasePath(Database.DATABASE_NAME);
        return SQLiteDatabase.openDatabase(dataBasePath.getPath(),null,flag | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }
    private void copyDatabase() throws IOException {
        InputStream is = context.getAssets().open(Database.DATABASE_NAME);
        File databasePath = context.getDatabasePath(Database.DATABASE_NAME);
        if (!databasePath.exists()){
            databasePath.getParentFile().mkdirs();
            databasePath.createNewFile();
        }
        FileOutputStream os = new FileOutputStream(databasePath);

        byte[] buffer = new byte[1024];
        while (is.read(buffer) > 0) {
            os.write(buffer);
        }
        os.flush();
        os.close();
        is.close();
    }
    private boolean isDatabaseExists(){
        File databasePath = context.getDatabasePath(Database.DATABASE_NAME);
        return databasePath.exists();
    }

}
