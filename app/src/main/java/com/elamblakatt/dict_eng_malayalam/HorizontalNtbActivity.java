package com.elamblakatt.dict_eng_malayalam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.elamblakatt.dict_eng_malayalam.Meaning.MeaningDatabse;
import com.elamblakatt.dict_eng_malayalam.Utils.NetworkStatus;
import com.elamblakatt.dict_eng_malayalam.Utils.Utils;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelperSQLCipher;
import com.elamblakatt.dict_eng_malayalam.dao.DatabseCopyInterface;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;
import com.elamblakatt.dict_eng_malayalam.notes.NoteListFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class HorizontalNtbActivity extends AppCompatActivity implements DatabseCopyInterface {

    private TabAdapter mTabPagerAdapter;
    private FragmentManager fm;
    private List<Fragment> fragments;
    private int selectedPage=0;
    private NavController navController;
    private BottomNavigationView bottomBar;
    public static final String RATE="rate";
    private InterstitialAd mInterstitialAd;
    private Context context;
    private ProgressDialog progress;
    private Word todayWord;
    private WordDaoImpl wordDao;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_ntb);
        context=HorizontalNtbActivity.this;

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        navController = Navigation.findNavController(this, R.id.main_fragment);
        bottomBar  = (BottomNavigationView)findViewById(R.id.bottomBar);
        NavigationUI.setupWithNavController(bottomBar, navController);
        setInterstitialAd();

        RegisterAlarmBroadcast();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseOpenHelperSQLCipher dd=new DatabaseOpenHelperSQLCipher(HorizontalNtbActivity.this,HorizontalNtbActivity.this);//.getReadableDatabase();
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

    public void show() {
        if (progress != null && progress.isShowing()) {
            return;
        }
        progress = ProgressDialog.show(this, ""+getString(R.string.app_name),
                "Please wait...Setting up dictionary...", true);
        progress.setCancelable(false);
    }

    @Override
    public void onCopySuccess(String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                while(todayWord==null) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(HorizontalNtbActivity.this);
                    int wordIndex = pref.getInt("WordIndex", 1);
                    wordDao = new WordDaoImpl(new DatabaseOpenHelper(context.getApplicationContext()).getReadableDatabase());
                    todayWord = wordDao.getWordFromId(wordIndex);
                    if (todayWord != null) {
                        String word = todayWord.getEnglish_meaning();
                        String meaning=wordDao.getMeaningFromEnglishWord(todayWord.getEngId());
                        todayWord.setHindi_meaning(meaning);
                        String upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
//                        txtvwWordOfDay.setText("" + upperString);
//                        txtvwWordOfDayHindi.setText(""+todayWord.getHindi_meaning()+"\n...");
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

            Utils.RegisterAlarmBroadcast(HorizontalNtbActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isJustLaunched", false);
            editor.commit();
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return true;
    }



    private void initUI() {


//        fragments = new Vector<Fragment>();
//        fragments.add(Fragment.instantiate(this,
//                SearchViewFragment.class.getName()));
//        fragments.add(Fragment.instantiate(this,
//                FavoriteListFragment.class.getName()));
//        fragments.add(Fragment.instantiate(this,
//                TodaysWordFragment.class.getName()));
//        fragments.add(Fragment.instantiate(this,
//                NoteListFragment.class.getName()));
//        fragments.add(Fragment.instantiate(this,
//                SeachHistoryListFragment.class.getName()));
//
//        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
//        mTabPagerAdapter = new TabAdapter(fm,fragments);
//        viewPager.setAdapter(mTabPagerAdapter);
//        viewPager.setOffscreenPageLimit(3);
//        viewPager.setCurrentItem(selectedPage);

//        final String[] colors = getResources().getStringArray(R.array.default_preview);
//
//        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
//        navigationTabBar.setBgColor(R.color.whiteColor);
//        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
//        models.add(
//                new NavigationTabBar.Model.Builder(
//                        getResources().getDrawable(R.drawable.search),
//                        Color.parseColor(colors[0]))
//                        //.selectedIcon(getResources().getDrawable(R.drawable.ic_search))
//                        .title("Search")
//                        .badgeTitle(null)
//                        .build()
//                       // .badgeTitle("NTB")
//                       // .build()
//        );
//        models.add(
//                new NavigationTabBar.Model.Builder(
//                        getResources().getDrawable(R.drawable.star_ic),
//                        Color.parseColor(colors[1]))
//                     //.selectedIcon(getResources().getDrawable(R.drawable.star))
//                        .title("Favorite")
//                       .badgeTitle(".")
//                        .build()
//        );
//        models.add(
//                new NavigationTabBar.Model.Builder(
//                        getResources().getDrawable(R.drawable.word),
//                        Color.parseColor(colors[2]))
//                        //.selectedIcon(getResources().getDrawable(R.drawable.ic_menu_camera))
//                        .title("Today's Word")
//                        .badgeTitle(".")
//                        .build()
//        );
//        models.add(
//                new NavigationTabBar.Model.Builder(
//                        getResources().getDrawable(R.drawable.ic_notepad),
//                        Color.parseColor(colors[4]))
//                        //.selectedIcon(getResources().getDrawable(R.drawable.ic_menu_camera))
//                        .title("Notes")
//                        .badgeTitle(".")
//                        .build()
//        );
//        models.add(
//                new NavigationTabBar.Model.Builder(
//                        getResources().getDrawable(R.drawable.history),
//                        Color.parseColor(colors[3]))
////                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
//                        .title("History")
//                        .badgeTitle("icon")
//                        .build()
//        );
//       /*
//        models.add(
//                new NavigationTabBar.Model.Builder(
//                        getResources().getDrawable(R.drawable.ic_menu_camera),
//                        Color.parseColor(colors[4]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_menu_camera))
//                        .title("Medal")
//                        .badgeTitle("777")
//                        .build()
//        );*/
//
//        navigationTabBar.setModels(models);
//        navigationTabBar.setViewPager(viewPager, selectedPage);
//        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(final int position) {
//                navigationTabBar.getModels().get(position).hideBadge();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(final int state) {
//
//            }
//        });
//
//        navigationTabBar.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
//                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
//                    navigationTabBar.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            model.showBadge();
//                        }
//                    }, i * 100);
//                }
//            }
//        }, 500);
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==NOTE_CREATE_REQUEST_CODE)
//        {
//            mFragmentManager.findFragmentBy(FRAGMENT_LIST_TAG).onActivityResult(requestCode, resultCode, data);
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mTabPagerAdapter != null) {
            NoteListFragment fragment = (NoteListFragment) mTabPagerAdapter.getItem(3);
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(NoteListFragment.class.getName());
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if(NetworkStatus.getInstance(HorizontalNtbActivity.this).isOnline()) {
            showInterstitialAd();
        }
        else {
            if (!Utils.isPresentInSharedPref(RATE, HorizontalNtbActivity.this)) {
                showRateExitDialog();
            } else {
                showExitDialog();
            }
        }
        //showExitDialog();
    }




    private void showExitDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                HorizontalNtbActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert);
        // set title
        alertDialogBuilder.setTitle(""+HorizontalNtbActivity.this.getString(R.string.app_name));

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
                    Utils.addToSharedPref(RATE,HorizontalNtbActivity.this);
                }
                else if(v==4 ||v==5)
                {
                    rateApp();
                    txtvwMessage.setVisibility(View.GONE);
                    Utils.addToSharedPref(RATE,HorizontalNtbActivity.this);
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

    private void rateApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + this.getPackageName()));
        startActivity(intent);
    }

    private void shareAction()
    {
        String strSelected="Install the "+getString(R.string.app_name)+" app. "+getString(R.string.app_link);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, strSelected);
        startActivity(Intent.createChooser(intent, "Share with"));

    }

    private void showInterstitialAd()
    {
        if(mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        else {
            if (!Utils.isPresentInSharedPref(RATE, HorizontalNtbActivity.this)) {
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

