package com.elamblakatt.dict_eng_malayalam.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.elamblakatt.dict_eng_malayalam.config.Configuration;
import com.elamblakatt.dict_eng_malayalam.model.Word;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class WordDaoImpl implements WordDao {
    private SQLiteDatabase sqLiteDatabase;
    public WordDaoImpl(SQLiteDatabase sqLiteDatabase){
        this.sqLiteDatabase = sqLiteDatabase;
    }
    @Override
    public Word getWordById(int id) {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME,new String[]{Database.COLUMN_ID, Database.COLUMN_ENGLISH, Database.COLUMN_HINDI, Database.COLUMN_FAVORITE},
                Database.COLUMN_ID + " = ?",new String[]{String.valueOf(id)},null,null,null,null);
        int idCoulumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        int hindiColumnInex = cursor.getColumnIndex(Database.COLUMN_HINDI);
        int favoriteColumnIndex = cursor.getColumnIndex(Database.COLUMN_FAVORITE);
        Word word = null;
        if (cursor.moveToNext()){
            word = new Word(cursor.getInt(idCoulumnIndex),cursor.getString(englishColumnIndex), cursor.getString(hindiColumnInex), cursor.getInt(favoriteColumnIndex)==1);
        }
        return word;
    }

    @Override
    public String getProunceFromWordName(String word) {
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM Pronounce where enword='"+word+"'",null);
        String strPronounce = "";
        try {
            int idColumnIndex = cursor.getColumnIndex("enpronounce");
                if(cursor.moveToNext())
                strPronounce = cursor.getString(idColumnIndex);
        }
        catch(Exception ex)
        {
          ex.printStackTrace();
        }

        return strPronounce;
    }

    @Override
    public void removeWordFromHistory(Word word) {
      //  sqLiteDatabase.rawQuery("DELETE FROM history where word='"+word.getEnglish_meaning()+"'",null);
        sqLiteDatabase.delete(Database.TABLE_NAME_History,Database.COLUMN_ENGLISH+"=?",new String[]{word.getEnglish_meaning()});

    }

    @Override
    public List<Word> getWordByEnglishWordSrNo(int englishName) {
        return null;
    }

    @Override
    public Word getWordByEnglishWordName(String englishWordName) {

        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME,new String[]{Database.COLUMN_ID, Database.COLUMN_ENGLISH, Database.COLUMN_FAVORITE},
                Database.COLUMN_ENGLISH + " = ?  COLLATE NOCASE", new String[]{englishWordName},null,null,null);
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
         int favoriteColumnIndex = cursor.getColumnIndex(Database.COLUMN_FAVORITE);
        Word word = null;
        if (cursor.moveToNext()){
            word = new Word(cursor.getInt(idColumnIndex), cursor.getString(englishColumnIndex),  cursor.getInt(favoriteColumnIndex)==1);
        }
        return word;
    }



    @Override
    public List<Word> getWordListByPrefixMatching(String prefix) {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME_WORD,new String[]{Database.COLUMN_ID, Database.COLUMN_ENGLISH, Database.COLUMN_FAVORITE},
                Database.COLUMN_ENGLISH + " LIKE ?",new String[]{prefix + "%"},null,null,Database.COLUMN_ENGLISH);
        List<Word> wordList = new ArrayList<Word>();
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        int favoriteColumnIndex = cursor.getColumnIndex(Database.COLUMN_FAVORITE);
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(idColumnIndex),cursor.getString(englishColumnIndex),  cursor.getInt(favoriteColumnIndex)==1);
            wordList.add(word);
        }
        return wordList;
    }

    @Override
    public List<Word> getWordListByPrefixMatching_Language(String prefix) {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME_WORD_mal,new String[]{Database.COLUMN_ID, Database.COLUMN_ENGLISH},
                Database.COLUMN_ENGLISH + " LIKE ?",new String[]{prefix + "%"},null,null,Database.COLUMN_ENGLISH);
        List<Word> wordList = new ArrayList<Word>();
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(idColumnIndex),cursor.getString(englishColumnIndex));
            wordList.add(word);
        }
        return wordList;
    }

    @Override
    public List<Word> getFavoriteWordList() {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME,new String[]{Database.COLUMN_ID, Database.COLUMN_ENGLISH},
                Database.COLUMN_FAVORITE + " = ?",new String[]{String.valueOf(1)},null,null,Database.COLUMN_ENGLISH, String.valueOf(Configuration.MAX_DISPLAYABLE_FAVORITE_WORDS));
        List<Word> favoriteWordList = new ArrayList<Word>();
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);


        while (cursor.moveToNext()){
            String meaning=getMeaningFromEnglishWord(cursor.getInt(idColumnIndex));
            Word word = new Word(cursor.getInt(idColumnIndex),cursor.getString(englishColumnIndex),meaning, true);
            favoriteWordList.add(word);
        }
        return favoriteWordList;
    }

    @Override
    public List<String> getEngilshWordByPrefixMatching(String prefix) {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME,new String[]{Database.COLUMN_ENGLISH},
                Database.COLUMN_ENGLISH + " LIKE ?",new String[]{prefix + "%"},null,null,Database.COLUMN_ENGLISH);
        List<String> englishWordList = new ArrayList<String>();
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        while (cursor.moveToNext()){
            englishWordList.add(cursor.getString(englishColumnIndex));
        }
        return englishWordList;
    }

    @Override
    public void makeFavorite(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_FAVORITE,1);
        sqLiteDatabase.update(Database.TABLE_NAME,contentValues,Database.COLUMN_ID + " = ?" ,new String[]{String.valueOf(id)});

    }

    @Override
    public void removeFromFavorite(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_FAVORITE,0);
        sqLiteDatabase.update(Database.TABLE_NAME,contentValues,Database.COLUMN_ID + " = ?" ,new String[]{String.valueOf(id)});
    }

    @Override
    public void insertWord(Word word) {
        int favorite;
        if (word.isFavorite()){
            favorite = 1;
        }
        else {
            favorite = 0;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_ENGLISH, word.getEnglish_meaning());
        contentValues.put(Database.COLUMN_HINDI, word.getHindi_meaning());
        contentValues.put(Database.COLUMN_FAVORITE, favorite);
        //TODO nullabl column hack
        sqLiteDatabase.insert(Database.TABLE_NAME,null,contentValues);
    }

   /* @Override
    public void addToSearchHistory(Word word) {
        int favorite;
        if (word.isFavorite()){
            favorite = 1;
        }
        else {
            favorite = 0;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_ENGLISH, word.getEnglish_meaning());
        contentValues.put(Database.COLUMN_HINDI, word.getHindi_meaning());
        contentValues.put(Database.COLUMN_FAVORITE, favorite);
        //TODO nullabl column hack
        sqLiteDatabase.insert("history",null,contentValues);
    }*/

    @Override
    public void updateWord(Word word) {
        int favorite;
        if (word.isFavorite()){
            favorite = 1;
        }
        else {
            favorite = 0;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_ENGLISH, word.getEnglish_meaning());
        contentValues.put(Database.COLUMN_HINDI, word.getHindi_meaning());
        contentValues.put(Database.COLUMN_FAVORITE, favorite);
        sqLiteDatabase.update(Database.TABLE_NAME,contentValues,Database.COLUMN_ID + " = ? ", new String[]{String.valueOf(word.getEngId())});
    }


    @Override
    public void addToSearchHistory(Word word)
    {
        /*ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_HISTORY,1);
        contentValues.put(Database.COLUMN_UPDDATE,getCurrentTime());
        sqLiteDatabase.update(Database.TABLE_NAME_WORD,contentValues,Database.COLUMN_ID + " = ?" ,new String[]{String.valueOf(id)});

*/

        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_ENGLISH,word.getEnglish_meaning());
        contentValues.put(Database.COLUMN_UPDDATE,getCurrentTime());
        if(word.getEngId()!=0)
        contentValues.put(Database.COLUMN_ENGID, word.getEngId());
        if(word.getMl_id()!=0)
            contentValues.put(Database.COLUMN_MLID, word.getMl_id());
        //TODO nullabl column hack
        sqLiteDatabase.insert(Database.TABLE_NAME_History,null,contentValues);





    }

    @Override
    public void deleteWord(int id) {
        sqLiteDatabase.delete(Database.TABLE_NAME,Database.COLUMN_ID + " = ? ", new String[]{String.valueOf(id)});
    }

    @Override
    public List<Word> getSearchHistory() {
        /*Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME_History,new String[]{Database.COLUMN_ENGLISH, Database.COLUMN_UPDDATE, Database.COLUMN_ENGID,Database.COLUMN_MLID},
                null,null,null,Database.COLUMN_UPDDATE, String.valueOf(Configuration.MAX_DISPLAYABLE_FAVORITE_WORDS));
    */    Cursor cursor =sqLiteDatabase.rawQuery("SELECT distinct(word),updDate,id_en,id_ml FROM history order by updDate desc",null);

        List<Word> favoriteWordList = new ArrayList<Word>();
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        int mlColumnInex = cursor.getColumnIndex(Database.COLUMN_MLID);
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(idColumnIndex), cursor.getInt(mlColumnInex),cursor.getString(englishColumnIndex));
            favoriteWordList.add(word);
        }
        return favoriteWordList;
    }

    @Override
    public Word getWordFromId(int ID)
    {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME_VOCAL,new String[]{Database.COLUMN_VOCAL_ID, Database.COLUMN_VOCAL_WORD},
                Database.COLUMN_VOCAL_ID + " = ?", new String[]{String.valueOf(ID)},null,null,null);
        int vocalidColumnIndex = cursor.getColumnIndex(Database.COLUMN_VOCAL_ID);
        int wordColumnIndex = cursor.getColumnIndex(Database.COLUMN_VOCAL_WORD);

        String strWord=null;
        Word word=new Word();
        while (cursor.moveToNext()){
            strWord=cursor.getString(wordColumnIndex);
         }
        if(strWord!=null)
           word=getWordByEnglishWordName(strWord);
        return word;
    }

    @Override
    public void deleteAllHistory() {
     /*   ContentValues con = new ContentValues();
        con.put(Database.COLUMN_HISTORY, 0);
        sqLiteDatabase.update(Database.TABLE_NAME,con,null,null);*/
           sqLiteDatabase.execSQL("DELETE  FROM history");
    }

    public Timestamp getCurrentDateAndTimeInTimeStamp() {

        Timestamp timeCurr = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDateandTime = sdf.format(new Date());
        Date current;
        try {
            current = sdf.parse(currentDateandTime);
            timeCurr = new Timestamp(current.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return timeCurr;
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public int getRandomNumber(int min,int max)
    {
        Random r = new Random();
        int i1 = r.nextInt(max - min) + min;
        return i1;
    }

    public String getMeaningFromEnglishWord(int english_id)
    {
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT word from words_ml where _id in( select id_ml from relations_en_ml where id_en=="+english_id+")",null);
        String strMalayalam = "";
        try {
            int idColumnIndex = cursor.getColumnIndex("word");
            while(cursor.moveToNext())
                strMalayalam = strMalayalam+" - "+cursor.getString(idColumnIndex)+"\n";
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return strMalayalam;

    }

    public String getMeaningFromLangWord(int lang_id)
    {
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT word from words_en where _id in( select id_en from relations_en_ml where id_ml="+lang_id+")",null);
        String strEng = "";
        try {
            int idColumnIndex = cursor.getColumnIndex("word");

            while(cursor.moveToNext())
                strEng = strEng+" - "+cursor.getString(idColumnIndex)+"\n";
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return strEng;

    }
}