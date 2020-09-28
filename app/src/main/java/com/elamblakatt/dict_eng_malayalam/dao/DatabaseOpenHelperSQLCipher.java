package com.elamblakatt.dict_eng_malayalam.dao;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DatabaseOpenHelperSQLCipher {
    private static final String TAG = DatabaseOpenHelperSQLCipher.class.getSimpleName();
    private Context context;
    private DatabseCopyInterface dbCopyInterface;
    public DatabaseOpenHelperSQLCipher(Context context,DatabseCopyInterface dbCopyInterface1)  {
        this.context = context;
        this.dbCopyInterface=dbCopyInterface1;
        if (!isDatabaseExists()){
            try {
                copyDatabase();
                decrypt(context,Database.DATABASE_NAME,"norwynnitin");
            } catch (IOException e) {
                Log.d(TAG,"Can't copy data base");
                e.printStackTrace();
            }
        }
        else
            dbCopyInterface.onCopySuccess("success");
    }
    public DatabaseOpenHelperSQLCipher(Context context)  {
        this.context = context;
        if (!isDatabaseExists()){
            try {
                copyDatabase();
                decrypt(context,Database.DATABASE_NAME,"norwynnitin");
            } catch (IOException e) {
                Log.d(TAG,"Can't copy data base");
                e.printStackTrace();
            }
        }

    }
    /*public SQLiteDatabase getReadableDatabase(){
        return getDatabase(SQLiteDatabase.OPEN_READONLY);
    }
    public SQLiteDatabase getReadWritableDatabase(){
        return getDatabase(SQLiteDatabase.OPEN_READWRITE);
    }*/
  /*  private SQLiteDatabase getDatabase(int flag){
        File dataBasePath = context.getDatabasePath(Database.DATABASE_NAME);
        return SQLiteDatabase.openDatabase(dataBasePath.getPath(),null,flag | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }*/
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

    private void decrypt(Context ctxt, String dbName, String passphrase)
    {
        SQLiteDatabase.loadLibs(context);
        FileInputStream fis=null;
        FileOutputStream fos=null;
        try
        {
            File unencFile = context.getDatabasePath("demo.db");
            unencFile.delete();
            File databaseFile = context.getDatabasePath(dbName);
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, passphrase, null);
            if (database.isOpen()) {
                database.rawExecSQL(String.format("ATTACH DATABASE '%s' as plaintext KEY '';",unencFile.getAbsolutePath()));
                database.rawExecSQL("SELECT sqlcipher_export('plaintext');");
                database.rawExecSQL("DETACH DATABASE plaintext;");
                android.database.sqlite.SQLiteDatabase sqlDB = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(unencFile, null);
                sqlDB.close();
                database.close();
            }
            databaseFile.delete();




            File databasePath = context.getDatabasePath(Database.DATABASE_NAME);
            if (!databasePath.exists()){
                databasePath.getParentFile().mkdirs();
                databasePath.createNewFile();
            }
            fos = new FileOutputStream(databasePath);
            fis=new FileInputStream(unencFile);
           /* File mydir = new File(Environment.getExternalStorageDirectory() + "/Decrypted/");
            if(!mydir.exists())
                mydir.mkdirs();*/
            byte[] buffer = new byte[1024];
            while (fis.read(buffer) > 0) {
                fos.write(buffer);
            }
          /*  while(true)
            {
                int i=fis.read();
                if(i!=-1)
                {
                    fos.write(i);
                }
                else
                {
                    break;
                }
            }*/
            //fos.flush();

        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
        finally
        {
            try
            {
                if(fos!=null)
                    fos.close();
                if(fis!=null)
                    fis.close();
            }
            catch(IOException ioe)
            {}
        }
        dbCopyInterface.onCopySuccess("success");

    }

}
