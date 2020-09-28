package com.elamblakatt.dict_eng_malayalam;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.elamblakatt.dict_eng_malayalam.Meaning.MeaningDatabse;
import com.elamblakatt.dict_eng_malayalam.Utils.NetworkStatus;
import com.elamblakatt.dict_eng_malayalam.Utils.Utils;
import com.elamblakatt.dict_eng_malayalam.dao.Database;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelperSQLCipher;
import com.elamblakatt.dict_eng_malayalam.dao.DatabseCopyInterface;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,DatabseCopyInterface {

    private GridLayout gridLayout;
    private TextView txtvwTodaysDate,txtvwWordOfDay,txtvwWordOfDayHindi;
    private LinearLayout linlayTodaysWord;
    private  Word todayWord;
    private WordDaoImpl wordDao;
    private Typeface typeface;
    PendingIntent pendingIntent;
    private ProgressDialog progress;
    private Context context;
    private InterstitialAd mInterstitialAd;
    public static final String RATE="rate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context=HomeActivity.this;

        AdView mAdView1 = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(""+getString(R.string.app_name));
        setSupportActionBar(toolbar);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        typeface=Typeface.createFromAsset(getAssets(),"Meera.ttf");

         final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.milkshake);

        gridLayout= (GridLayout) findViewById(R.id.gridLayout);

        int width = metrics.widthPixels;

        int count=0;
        for(int i=0;i<2;i++)
        {
            for(int j=0;j<2;j++)
            {
                View view= this.getLayoutInflater().inflate(R.layout.home_content_item,null);
                TextView txtvw=(TextView)view.findViewById(R.id.text1);
                ImageView imgvwHomeIcon=(ImageView)view.findViewById(R.id.imgvwHomeIcon);
                txtvw.setText(""+count);
                if(count==0)
                {
                    imgvwHomeIcon.setImageResource(R.drawable.home_search);
                    txtvw.setText("Search");
                }

                else if(count==1)
                {
                    txtvw.setText("Favorite");
                    imgvwHomeIcon.setImageResource(R.drawable.home_fav);

                }
                else if(count==2)
                {
                    txtvw.setText("Todays Word");
                    imgvwHomeIcon.setImageResource(R.drawable.home_word);

                }
                else if(count==3)
                {
                    txtvw.setText("Search History");
                    imgvwHomeIcon.setImageResource(R.drawable.home_hist);

                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setMargins(2,2,2,2);
                params.height =((width/2)-100);
                params.width =(width/2);
                params.setGravity(Gravity.CENTER);
                params.columnSpec = GridLayout.spec(i);
                params.rowSpec = GridLayout.spec(j);

                view.setTag(count);
                view.setLayoutParams(params);
                gridLayout.addView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int count=Integer.parseInt(view.getTag().toString());

                            Intent intent = new Intent(HomeActivity.this, HorizontalNtbActivity.class);
                            intent.putExtra("SelectedTab", count);
                            startActivity(intent);

                    }
                });
                count=count+1;
            }
        }
        gridLayout.setAlpha(0f);
        gridLayout.setVisibility(View.VISIBLE);

// Animate the content view to 100% opacity, and clear any animation
// listener set on the view.
        gridLayout.animate()
                .alpha(1f)
                .setDuration(4000)
                .setListener(null);
     /*   gridLayout.setAnimation(myAnim);
        myAnim.start();*/

        txtvwTodaysDate=(TextView)findViewById(R.id.txtvwTodaysDate);
        txtvwWordOfDay=(TextView)findViewById(R.id.txtvwWordOfDay);
        txtvwWordOfDayHindi=(TextView)findViewById(R.id.txtvwWordOfDayHindi);
        txtvwWordOfDayHindi.setTypeface(typeface);
        linlayTodaysWord=(LinearLayout)findViewById(R.id.linlayTodaysWord);
        linlayTodaysWord.setOnClickListener(this);
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        txtvwTodaysDate.setText(formattedDate);
        setInterstitialAd();

        RegisterAlarmBroadcast();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseOpenHelperSQLCipher dd=new DatabaseOpenHelperSQLCipher(context,HomeActivity.this);//.getReadableDatabase();
//dd.getDatabase();
            }
        }).start();
       show();


        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadDBMeaningFile();
            }
        }).start();


    }


    private void downloadDBMeaningFile() {

            MeaningDatabse meaningDb = new MeaningDatabse(context);

                File dbDownloadPath = new File(meaningDb.getDatabaseFolder());
                if (!dbDownloadPath.exists()) {
                    dbDownloadPath.mkdirs();
                }

            final File localFile = new File(dbDownloadPath,meaningDb.DATABASE_NAME+".sqlite");
                if (!localFile.exists()) {

                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://dictionary-a0a8c.appspot.com");
                    StorageReference  islandRef = storageRef.child("eng_pun_dict.sqlite");
                    islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                            //  updateDb(timestamp,localFile.toString(),position);
                            meaningDb.initialiseDB();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                        }
                    });
                }
    }

    private void RegisterAlarmBroadcast()
    {

        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        boolean isJustLaunced= preferences.getBoolean("isJustLaunched", true);
        if(isJustLaunced) {

            Utils.RegisterAlarmBroadcast(HomeActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isJustLaunched", false);
            editor.commit();
        }

    }

    @Override
    public void onClick(View view)
    {
        if(view==linlayTodaysWord) {
            //String prounce = wordDao.getProunceFromWordName("" + todayWord.getEnglish_meaning());
            Intent intent = new Intent(HomeActivity.this, MeaningDisplayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("WordSel", todayWord);//new ArrayList<Word>(lstExactWord));
           // bundle.putString("Pronounce", prounce);
            bundle.putBoolean("TodayWord",true);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_share:
//                shareAction();
                return true;
            case R.id.nav_rate:
//               rateApp();
                return true;
            case R.id.nav_setting:
//                openSettingScreen();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//    private void openSettingScreen() {
//        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
//        startActivity(intent);
//    }

    @Override
    public void onCopySuccess(String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                while(todayWord==null) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                    int wordIndex = pref.getInt("WordIndex", 1);
                    wordDao = new WordDaoImpl(new DatabaseOpenHelper(context.getApplicationContext()).getReadableDatabase());
                    todayWord = wordDao.getWordFromId(wordIndex);
                    if (todayWord != null) {
                        String word = todayWord.getEnglish_meaning();
                        String meaning=wordDao.getMeaningFromEnglishWord(todayWord.getEngId());
                        todayWord.setHindi_meaning(meaning);
                        //String  pronounce = wordDao.getProunceFromWordName("" + todayWord.getEnglish_meaning());
                       // todayWord.setPronouce(pronounce);
                        String upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
                        txtvwWordOfDay.setText("" + upperString);
                        txtvwWordOfDayHindi.setText(""+todayWord.getHindi_meaning()+"\n...");
                    }
                    else
                    {
                        wordIndex=wordIndex+1;
                        SharedPreferences.Editor myeditor = pref.edit();
                        myeditor.putInt("WordIndex", wordIndex) ;
                        myeditor.commit();
                    }
                }
                if(progress!=null && progress.isShowing())
                    progress.dismiss();
            }
        });

    }

    public void show() {
        if (progress != null && progress.isShowing()) {
            return;
        }
        progress = ProgressDialog.show(this, ""+getString(R.string.app_name),
                "Please wait...Setting up dictionary...", true);
        progress.setCancelable(false);
    }

    private void showExitDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                HomeActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert);
        // set title
        alertDialogBuilder.setTitle(""+HomeActivity.this.getString(R.string.app_name));

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to exit the app?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                       /* if(NetworkStatus.getInstance(MainActivity.this).isOnline())
                            showInterstitialAd();
                        else*/
                        finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        loadInterestialAd();
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
        public void showRateExitDialog() {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_rating_exit, null);
        dialogBuilder.setView(dialogView);
        final TextView txtvwMessage=(TextView)dialogView.findViewById(R.id.txtvwMessage);

        RatingBar ratingBar=(RatingBar)dialogView.findViewById(R.id.ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(v==1 || v==2 || v==3)
                {
                    txtvwMessage.setText("Thank you for the feedback...Will take action to improve the app.");
                    txtvwMessage.setVisibility(View.VISIBLE);
                    Utils.addToSharedPref(RATE,HomeActivity.this);
                }
                else if(v==4 ||v==5)
                {
//                    rateApp();
                    txtvwMessage.setVisibility(View.GONE);
                    Utils.addToSharedPref(RATE,HomeActivity.this);
                }
            }
        });


        dialogBuilder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                loadInterestialAd();
                dialog.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog b = dialogBuilder.create();
        b.show();
    }


    @Override
    public void onBackPressed() {
        if(NetworkStatus.getInstance(HomeActivity.this).isOnline()) {
            showInterstitialAd();
        }
        else {
            if (!Utils.isPresentInSharedPref(RATE, HomeActivity.this)) {
                showRateExitDialog();
            } else {
                showExitDialog();
            }
        }
        //showExitDialog();
    }


    private void showInterstitialAd()
    {
        if(mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        else {
            if (!Utils.isPresentInSharedPref(RATE, HomeActivity.this)) {
                showRateExitDialog();
            } else {
                showExitDialog();
            }
        }
    }

    private void loadInterestialAd(){
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void setInterstitialAd()
    {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interestial_ad_unit_id));
        AdRequest adRequestInter = new AdRequest.Builder().build();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //  mInterstitialAd.show();
            }




            @Override
            public void onAdClosed() {
                super.onAdClosed();
                showExitDialog();
            }
        });
        mInterstitialAd.loadAd(adRequestInter);
    }


}
