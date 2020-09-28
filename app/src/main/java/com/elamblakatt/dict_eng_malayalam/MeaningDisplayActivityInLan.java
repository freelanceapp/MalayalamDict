package com.elamblakatt.dict_eng_malayalam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elamblakatt.dict_eng_malayalam.Meaning.MeaningDatabse;
import com.elamblakatt.dict_eng_malayalam.Meaning.WordDetail;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.WordDao;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by admin on 11/16/2016.
 */

public class MeaningDisplayActivityInLan extends AppCompatActivity
{
   /* @InjectView(R.id.txtvwEng)
    TextView txtvwEng;
    @InjectView(R.id.txtvwHindi)
    TextView txtvwHindi;*/
    private Word wordSel;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtvwSelectedProunoce)
    TextView txtvwSelectedProunoce;
    @BindView(R.id.txtvwHindi)
    TextView txtvwHindi;
    @BindView(R.id.txtvwSelectedWord)
    TextView txtvwSelectedWord;

    @BindView(R.id.imgbtnFav)
    ImageButton imgbtnFav;
    @BindView(R.id.imgbtnSound)
    ImageButton imgbtnSound;
    @BindView(R.id.imgbtnShare)
    ImageButton imgbtnShare;

    @BindView(R.id.btnPreviousWord)
    ImageButton btnPreviousWord;
    @BindView(R.id.linlay_wordDetailRoot)
    LinearLayout linlay_wordDetailRoot;
    @BindView(R.id.btnGoogle)
    ImageButton btnGoogle;
    @BindView(R.id.card_view3)
    CardView card_view3;


    private Typeface typeface;
   // private String pronounce;
    private WordDao wordDao;
    private static int wordIndex;
    private SharedPreferences pref;
    private boolean isTodayWord=false;
     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meaning_display);
        View vw=(View)findViewById(R.id.rellaymeaning_display_root);
//        ButterKnife.inject(this,vw);
        ButterKnife.bind(vw);
        setSupportActionBar(toolbar);
         AdView mAdView1 = (AdView) vw.findViewById(R.id.adView3);
         AdRequest adRequest1 = new AdRequest.Builder().build();
         mAdView1.loadAd(adRequest1);
        wordDao = new WordDaoImpl(new DatabaseOpenHelper(MeaningDisplayActivityInLan.this).getReadWritableDatabase());
        typeface=Typeface.createFromAsset(getAssets(),"Meera.ttf");
        getSupportActionBar().setTitle("Meaning");
        wordSel = getIntent().getParcelableExtra("WordSel");
       // pronounce=getIntent().getStringExtra("Pronounce");
        isTodayWord=getIntent().getBooleanExtra("TodayWord",false);
        txtvwHindi.setMovementMethod(new ScrollingMovementMethod());
       /* txtvwSelectedProunoce.setText(""+arrWordSel.get(0).getPronouce());
        txtvwSelectedType.setText(""+arrWordSel.get(0).getWordType());*/
       // txtvwSelectedWord.setText(""+arrWordSel.get(0).getWord());
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setWordData();

      //  addToHistory();
         pref = PreferenceManager.getDefaultSharedPreferences(MeaningDisplayActivityInLan.this);
        wordIndex = pref.getInt("WordIndex", 1);
            if (wordIndex == 1)
                btnPreviousWord.setVisibility(View.GONE);
        if(!isTodayWord)
            btnPreviousWord.setVisibility(View.GONE);

         imgbtnSound.setVisibility(View.GONE);
         imgbtnFav.setVisibility(View.GONE);
        btnPreviousWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wordSel=null;
                while(wordSel==null && wordIndex!=0)
                {
                    wordIndex=wordIndex-1;
                    if(wordIndex!=0) {
                        wordSel = wordDao.getWordFromId(wordIndex);
                        if (wordSel != null) {
                            String word = wordSel.getEnglish_meaning();
                            String upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
                            setWordData();
                        } else {
                            wordIndex = wordIndex - 1;
                            /*SharedPreferences.Editor myeditor = pref.edit();
                            myeditor.putInt("WordIndex", wordIndex);
                            myeditor.commit();*/
                        }
                    }
                }
            }
        });

         setWordDetail();
       /* imgbtnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String str=txtvwSelectedWord.getText().toString();
                t1.speak(str, TextToSpeech.QUEUE_FLUSH, null);
            }
        });*/
    }

    /*private void addToHistory()
    {
        wordDao.addToSearchHistory(wordSel);
    }*/
    private void setWordData()
    {
        /*for(int i=0;i<arrWordSel.size();i++)
        {*/
            /*View child = getLayoutInflater().inflate(R.layout.meaning_display_item, null);
            TextView txtvwEng=(TextView) child.findViewById(R.id.txtvwEng);
            TextView txtvwHindi=(TextView) child.findViewById(R.id.txtvwHindi);*/

             txtvwSelectedWord.setTypeface(typeface);
            /*String eng=wordSel.getEnglish_meaning();
            if(Character.isDigit(eng.charAt(0)))
                eng=eng.substring(1,eng.length());
            Character str1=eng.charAt(0);
            if(str1.equals('.'))
                eng=eng.substring(1,eng.length());*//**/

            txtvwSelectedWord.setText(wordSel.getEnglish_meaning());
            txtvwHindi.setText(wordSel.getHindi_meaning());

        if (wordSel.isFavorite()){
            imgbtnFav.setImageResource(R.drawable.fav_sel);
        }
        else {
            imgbtnFav.setImageResource(R.drawable.fav);
        }

        imgbtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordSel.isFavorite()) {
                    imgbtnFav.setImageResource(R.drawable.fav);
                    wordSel.setFavorite(false);
                    wordDao.removeFromFavorite(wordSel.getEngId());

                } else {
                    imgbtnFav.setImageResource(R.drawable.fav_sel);
                    wordSel.setFavorite(true);
                    wordDao.makeFavorite(wordSel.getEngId());

                }
            }
        });

        imgbtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAction();
            }

    });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = wordSel.getEnglish_meaning();
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.setClassName("com.google.android.googlequicksearchbox", "com.google.android.googlequicksearchbox.SearchActivity");
                intent.putExtra("query", query);
                startActivity(intent);
            }
        });
        //}

    }

    private void setWordDetail()
    {

        MeaningDatabse db = new MeaningDatabse(this);
        WordDetail wordDetail = db.getWordDetails(wordSel.getEnglish_meaning());


        if (wordDetail != null && wordDetail.getWord() != null){

            View child = getLayoutInflater().inflate(R.layout.custom_word_detail, null);
            TextView txtvw_Antonyms = (TextView) child.findViewById(R.id.txtvw_Antonyms);
            TextView txtvw_Meaning = (TextView) child.findViewById(R.id.txtvw_Meaning);
            TextView txtvw_Synonyms = (TextView) child.findViewById(R.id.txtvw_Synonyms);
            TextView txtvw_WordType = (TextView) child.findViewById(R.id.txtvw_WordType);
            TextView txtvw_Example = (TextView) child.findViewById(R.id.txtvw_Example);

            txtvw_Antonyms.setText(wordDetail.getAntonyms());
            txtvw_Meaning.setText(wordDetail.getMeaning());
            txtvw_Synonyms.setText(wordDetail.getSynonyms().replace(",", ", "));
            txtvw_WordType.setText(wordSel.getEnglish_meaning() + " (" + wordDetail.getWord_type() + ")");
            txtvw_Example.setText(wordDetail.getExample());
            linlay_wordDetailRoot.addView(child);
            card_view3.setVisibility(View.VISIBLE);
        } else {
            card_view3.setVisibility(View.GONE);
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        MenuItem searchItem = menu.findItem(R.id.nav_setting);
        if(isTodayWord)
            searchItem.setVisible(true);
        else
            searchItem.setVisible(false);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        {

        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.nav_setting:
//                openSettingScreen();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void openSettingScreen() {
//        Intent intent = new Intent(MeaningDisplayActivityInLan.this, SettingsActivity.class);
//        startActivity(intent);
//    }
    private void shareAction()
    {
        String strSelected="";
        strSelected= "Selected Word:\n "+wordSel.getEnglish_meaning() +" \n "+txtvwHindi.getText();
         strSelected= strSelected+"\n\nInstall the "+getString(R.string.app_name)+" app. "+getString(R.string.app_link);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,strSelected);
        startActivity(Intent.createChooser(intent, "Share with"));

    }
}
