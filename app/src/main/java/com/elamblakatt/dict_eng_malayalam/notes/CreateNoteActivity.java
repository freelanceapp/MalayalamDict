package com.elamblakatt.dict_eng_malayalam.notes;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.Utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jasmine Nitin on 9/16/2017.
 */

public class CreateNoteActivity extends AppCompatActivity {

    private EditText edittextContent;
    private EditText edittext_temp;
    CardView cardview_content;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private RelativeLayout relativelayout_createnote;
    private DataBaseHelperNoteImage dbHelper;
    private Context context;
    private static final int PERMISSION_ALL_CAMERA = 111;
    private static final int PERMISSION_ALL_SDCARD = 110;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int CAMERA_REQUEST = 1000;
    private static final int GALLERY_REQUEST = 2000;
    private static final int TAGSELECT_REQUEST = 3000;
    static String strRootFoldr, imageFolderPath, imageName;
    static Uri fileUri;
    final int total_image_count = 10;
    static int iMainImageIndex = 0;
    private static String currentDateTime;
    private Note noteSelected;
    private Toolbar toolbar;
    private String dateSelectedFromCalender;
    private DataBaseHelperNoteImage dbHelperSavePDF;
    private FileUtils mFileUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_createnote);
        setTitle("Create Note");
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//    toolbar.setTitle("Add Note");
//        setSupportActionBar(toolbar);
        dbHelper = new DataBaseHelperNoteImage(this);
         context = CreateNoteActivity.this;
        dbHelperSavePDF = new DataBaseHelperNoteImage(this);
        if(getIntent().getExtras()!=null) {
            noteSelected=getIntent().getExtras().getParcelable("noteSelected");
            dateSelectedFromCalender=getIntent().getExtras().getString("dateSelected");

        }
        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        SharedPreferences preferences = this.getSharedPreferences("MyPref", this.MODE_PRIVATE);
        int selColor=preferences.getInt("SelColor", getResources().getColor(R.color.colorPrimary));
//        toolbar.setBackgroundColor(selColor);
        int selColor1=preferences.getInt("SelColor", getResources().getColor(R.color.colorPrimaryDark));
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(selColor1);

        initialiseView();
    }



    private void initialiseView() {
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        //getSupportActionBar().setTitle("Add Note");

        edittextContent=(EditText)findViewById(R.id.edittext_content);
        edittext_temp=(EditText)findViewById(R.id.edittext_temp);
        cardview_content=(CardView) findViewById(R.id.cardview_content);
        relativelayout_createnote=(RelativeLayout)findViewById(R.id.relativelayout_createnote);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (dateSelectedFromCalender != null) {

            Calendar calNow = Calendar.getInstance();
            Date dateNow=calNow.getTime();
            SimpleDateFormat dfNow = new SimpleDateFormat("HHmmss");
            String strdateNow = dfNow.format(dateNow);

            dateSelectedFromCalender=dateSelectedFromCalender+strdateNow;
            currentDateTime=dateSelectedFromCalender;
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = df.parse(dateSelectedFromCalender);
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy HH:mm aa");
                String formattedDate = df1.format(date);
//                textDate.setText(formattedDate);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm aa");
            String formattedDate = df.format(c.getTime());
            currentDateTime= Utils.getCurrentDateAndTimeInString();
//            textDate.setText(formattedDate);
        }


        if(noteSelected!=null)
        {
            edittext_temp.requestFocus();

            edittextContent.setText(""+noteSelected.getContent());
            setTitle("Edit Note");
//            toolbar.setTitle("Edit Note");

        }
        cardview_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edittextContent.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edittextContent, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.create_menu, menu);
        // Get the SearchView and set the searchable configuration
        //MenuItem searchItem = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
      if(item.getItemId()==R.id.action_save)
        {
            saveNote();
            return true;
        }
        else if(item.getItemId()==R.id.action_voice)
        {
            promptSpeechInput();
            return true;
        }
//      else if(item.getItemId()==R.id.action_convert)
//      {
//          convert();
//          return true;
//      }
        else if(item.getItemId()==android.R.id.home)
        {
           /* if(noteSelected==null)
            {*/
                saveNote();
           // }
           // finish();
        }
        return false;
    }




    private void saveNote()
    {
        if (edittextContent.getText().toString() != null && edittextContent.getText().toString().length() > 0) {

                if(noteSelected!=null )
                {
                    dbHelper.UpdateNote( edittextContent.getText().toString(),
                            currentDateTime,noteSelected.getId());
                }
                else {
                    dbHelper.insertIntoNoteList( edittextContent.getText().toString(),
                            currentDateTime,0);
                }
          finish();
        } else {
//            Toast.makeText(context, "Please enter the content for the note", Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something"
        );
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            /*Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();*/
        }
    }


    private boolean checkAndRequestPermissions(int flag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);*/


            int storagePermission = ContextCompat.checkSelfPermission(this,


                    Manifest.permission.READ_EXTERNAL_STORAGE);

            int storageWritePermission = ContextCompat.checkSelfPermission(this,


                    Manifest.permission.WRITE_EXTERNAL_STORAGE);


            List<String> listPermissionsNeeded = new ArrayList<>();
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
           /* if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }*/
            if (storageWritePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), flag);
                return false;
            }
        }
        return true;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      if(requestCode==REQ_CODE_SPEECH_INPUT)
        {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                edittextContent.setText(edittextContent.getText()+result.get(0).toString());


            }

        }
    }

    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        saveNote();
    }
}
