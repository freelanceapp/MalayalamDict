package com.elamblakatt.dict_eng_malayalam;

import android.app.Application;

import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.google.firebase.FirebaseApp;

/**
 * Created by Jasmine on 02/01/17.
 */

public class RRApplication extends Application {

    private WordDaoImpl wordDao;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        //setDatabaseObject();
    }

   /* void setDatabaseObject()
    {
        wordDao = new WordDaoImpl(new DatabaseOpenHelper(getApplicationContext()).getReadWritableDatabase());
    }*/
    //public WordDaoImpl getDatabaseObject()
    /*{
      return wordDao;
    }*/

}
