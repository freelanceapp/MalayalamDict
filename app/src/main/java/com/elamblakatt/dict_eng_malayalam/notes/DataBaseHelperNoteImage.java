package com.elamblakatt.dict_eng_malayalam.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * This class performs all database related functionality.
 * @author jasminee
 *
 */
public class DataBaseHelperNoteImage extends SQLiteOpenHelper
{

    private static String DB_NAME= "notedb.sqlite";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 1;

    private static String DB_VERSE_COLUMN = "";

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public DataBaseHelperNoteImage(Context context) {


        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
        this.close();
    }


    public static String getDBName(Context mContext){

        // For English section
//
//            SharedPreferences preferences = mContext.getSharedPreferences("MyPref", mContext.MODE_PRIVATE);
//            String bibletypeSelected = preferences.getString("BibleType", BIBLETYPE_KJV);
//            if (bibletypeSelected.equals(BIBLETYPE_NIV)) {
//                DB_NAME = "n_e_new.aaa";
//                DB_VERSE_COLUMN = "eng_niv";
//            } else if (bibletypeSelected.equals(BIBLETYPE_ESV)) {
//                DB_NAME = "n_e_new.aaa";
//                DB_VERSE_COLUMN = "eng_esv";
//
//            } else {
//                DB_NAME = "zz.aaa";
//                DB_VERSE_COLUMN = "verse";
//            }
//            return DB_NAME;


        // For other section
        DB_NAME = "zz.aaa";
        DB_VERSE_COLUMN = "verse";
        return DB_NAME;

    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }


    public int update(String table, ContentValues values, String whereClause, String[] whereArgs){
        SQLiteDatabase mDataBase = this.getWritableDatabase();
        return mDataBase.update(table, values, whereClause, whereArgs);
    }

    public long countRows(String query){
        SQLiteDatabase db = this.getWritableDatabase();

        return DatabaseUtils.longForQuery(db, query, null);
    }

    public long insert(String table, ContentValues values){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.insert(table, null, values);
    }

    public long delete(String table, String where, String[] whereArgs){
        SQLiteDatabase mDataBase = this.getWritableDatabase();
        return mDataBase.delete(table, where, whereArgs);
    }

    public Cursor rawQuery(String sql,String[] args){
        SQLiteDatabase mDataBase = this.getWritableDatabase();
        return mDataBase.rawQuery(sql, args);
    }


    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
//        InputStream mInput = mContext.getResources().openRawResource(R.raw.xyz);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }







    /**
     * Method to get the day of the different types of reminders in CycleReminders.
     */
    public ArrayList<Note> getNoteList()
    {
        String day = null;
        Cursor cursor = null;
        ArrayList<Note> lstNote=new ArrayList<>();
        try
        {
            SQLiteDatabase db=this.getReadableDatabase();
//            openDatabase(SQLiteDatabase.OPEN_READONLY);// | SQLiteDatabase.OPEN_READWRITE>);
            //String totalContacts = "SELECT * FROM verses1 where rowid='"+GetRandomNo()+"'";

            //	myPath = DB_PATH + DB_NAME;
            ///myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            String totalContacts = "SELECT * FROM note order by CreatedDate DESC;";
            cursor = db.rawQuery(totalContacts, null);
            int i2=cursor.getColumnIndex("Content");
            int i4=cursor.getColumnIndex("Id");
            int i5=cursor.getColumnIndex("CreatedDate");
            if (cursor.moveToFirst())
            {
                do
                {
                    Note note=new Note();
                    note.setContent(cursor.getString(i2));
                    note.setId(cursor.getInt(i4));
                    note.setCreatedDate(cursor.getString(i5));
                    lstNote.add(note);
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return lstNote;
    }


    public void UpdateNote( String content,  String createdDate, int Id)
    {

        boolean isSucces = false;
        long id = -1;
        try
        {
            SQLiteDatabase db=this.getWritableDatabase();
//            openDatabase(SQLiteDatabase.OPEN_READWRITE);// | SQLiteDatabase.OPEN_READWRITE>);
            String strQuery;
            Calendar c = Calendar.getInstance();
            //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = df.parse(createdDate);
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
            String calenderDate = df1.format(date);

            ContentValues values = new ContentValues();
            values.put("Content", content);
            values.put("CreatedDate",createdDate);
            values.put("CalenderDate",calenderDate);


            // updating row
            id= db.update("note", values,  "Id = ?",
                    new String[] { String.valueOf(Id) });

            if (id != -1)
            {
                isSucces = true;
            }
            close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            //	myDatabase.close();
        }


    }




    public boolean insertIntoNoteList( String content,  String createdDate, int Id)
    {
        boolean isSucces = false;
        long id = -1;
        try
        {
            //SQLiteDatabase myDatabase=this.getWritableDatabase();

            Calendar c = Calendar.getInstance();
            //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = df.parse(createdDate);
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
            String calenderDate = df1.format(date);

            SQLiteDatabase db=this.getWritableDatabase();
            SQLiteStatement stmt;
            if(Id!=0) {
                //For Editting notee

                stmt = db.compileStatement("Update  note set(Content,CreatedDate,CalenderDate) values(?,?,?) where Id="+Id);
                stmt.bindString(1, content);
                stmt.bindString(2, createdDate);
                stmt.bindString(3, calenderDate);

            }
            else
            {
                //For Adding Note
                stmt = db.compileStatement("INSERT INTO note(Content,CreatedDate,CalenderDate) values(?,?,?)");
                stmt.bindString(1, content);
                stmt.bindString(2, createdDate);
                stmt.bindString(3, calenderDate);

            }
            id = stmt.executeInsert();

            if (id != -1)
            {
                isSucces = true;
            }
            close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            //	myDatabase.close();
        }

        return isSucces;
    }


    public void deleteNote(String Id) {

        try {
            SQLiteDatabase db=this.getWritableDatabase();
//            openDatabase(SQLiteDatabase.OPEN_READWRITE);// | SQLiteDatabase.OPEN_READWRITE>);
            String strQuery;

            strQuery = "DELETE FROM note WHERE Id='" + Id + "'";


            db.execSQL(strQuery);
            close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
