package com.elamblakatt.dict_eng_malayalam.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.elamblakatt.dict_eng_malayalam.Meaning.MeaningDatabse;
import com.elamblakatt.dict_eng_malayalam.Meaning.WordDetail;
import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.config.Configuration;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Jasmine on 27/12/16.
 */

public class TodaysWordFragment extends Fragment {
    private WordDaoImpl wordDao;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtvwSelectedProunoce)
    TextView txtvwSelectedProunoce;
    @BindView(R.id.txtvwHindi)
    TextView txtvwHindi;
    @BindView(R.id.txtvwSelectedWord)
    TextView txtvwSelectedWord;
    @BindView(R.id.imgbtnSound)
    ImageButton imgbtnSound;
    @BindView(R.id.imgbtnFav)
    ImageButton imgbtnFav;
    @BindView(R.id.btnPreviousWord)
    ImageButton btnPreviousWord;
    @BindView(R.id.btnNextWord)
    ImageButton btnNextWord;
    @BindView(R.id.linlay_wordDetailRoot)
    LinearLayout linlay_wordDetailRoot;
    @BindView(R.id.btnGoogle)
    ImageButton btnGoogle;
    @BindView(R.id.card_view3)
    CardView card_view3;
    @BindView(R.id.viewGreenBottom)
    View viewGreenBottom;

    private Typeface typeface;
    private Word todayWord=null;
    private String pronounce;
    private int wordIndex,finalIndex;
    private TextToSpeech t1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.meaning_display, container,
                false);
        ButterKnife.bind(this,v);
       // setSupportActionBar(toolbar);
        typeface= Typeface.createFromAsset(getActivity().getAssets(), Configuration.HINDI_TYPEFACE_PATH);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Today's Word");
        viewGreenBottom.setVisibility(View.GONE);

        AdView mAdView1 = (AdView) v.findViewById(R.id.adView3);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        t1=new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    String localCode = "ml_IN";
                    Locale locale = new Locale(localCode);
//                    t1.setLanguage(Locale.UK);
                    t1.setLanguage(locale);
                }
            }
        });
        //todayWord= HomeActivity.todayWord;
        if (getUserVisibleHint()) {
            wordIndex = pref.getInt("WordIndex", 1);
            finalIndex=wordIndex;
            if(wordDao==null)
            wordDao = new WordDaoImpl(new DatabaseOpenHelper(getActivity().getApplicationContext()).getReadWritableDatabase());
            todayWord = wordDao.getWordFromId(wordIndex);
            String meaning=wordDao.getMeaningFromEnglishWord(todayWord.getEngId());
            todayWord.setHindi_meaning(meaning);
            if (todayWord != null) {
                String word = todayWord.getEnglish_meaning();
                String upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
         //       pronounce = wordDao.getProunceFromWordName("" + todayWord.getEnglish_meaning());
            }
           // todayWord= HomeActivity.todayWord;
            setWordData();
            setWordDetail();
            btnPreviousWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    todayWord=null;
                    while(todayWord==null && wordIndex!=0)
                    {
                        wordIndex=wordIndex-1;
                        if(wordIndex!=0) {
                            todayWord = wordDao.getWordFromId(wordIndex);
                            if (todayWord != null) {
                                String word = todayWord.getEnglish_meaning();

                                String meaning=wordDao.getMeaningFromEnglishWord(todayWord.getEngId());
                                todayWord.setHindi_meaning(meaning);
                              //  String upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
                                setWordData();
                            } else {
                                wordIndex = wordIndex - 1;
                            /*SharedPreferences.Editor myeditor = pref.edit();
                            myeditor.putInt("WordIndex", wordIndex);
                            myeditor.commit();*/
                            }
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"No words to display",Toast.LENGTH_SHORT).show();
                          //  btnPreviousWord.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });




            btnNextWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    todayWord=null;
                    while(todayWord==null && wordIndex!=finalIndex+1)
                    {
                        wordIndex=wordIndex+1;
                        if(wordIndex!=finalIndex+1) {
                            todayWord = wordDao.getWordFromId(wordIndex);
                            if (todayWord != null) {
                                String word = todayWord.getEnglish_meaning();

                                String meaning=wordDao.getMeaningFromEnglishWord(todayWord.getEngId());
                                todayWord.setHindi_meaning(meaning);
                                //  String upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
                                setWordData();
                            } else {
                                wordIndex = wordIndex +1;
                            /*SharedPreferences.Editor myeditor = pref.edit();
                            myeditor.putInt("WordIndex", wordIndex);
                            myeditor.commit();*/
                            }
                        }
                        else
                        {
                           // btnNextWord.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"No words to display",Toast.LENGTH_SHORT).show();

                        }
                    }

                }
            });

            btnGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String query = todayWord.getEnglish_meaning();
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.setClassName("com.google.android.googlequicksearchbox", "com.google.android.googlequicksearchbox.SearchActivity");
                    intent.putExtra("query", query);
                    startActivity(intent);
                }
            });




        }
//        hideKeyboard();

        return v;
    }


    public void hideKeyboard(){
//        if(!(view instanceof EditText)){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        hideKeyboard();

    }

    private void addToHistory()
    {
     //   wordDao.addToSearchHistory(wordSel);
    }
    private void setWordData()
    {

       /* if(wordIndex==1)
        {
            btnPreviousWord.setVisibility(View.GONE);
        }
        else
        {
            btnPreviousWord.setVisibility(View.VISIBLE);
        }

        if(wordIndex>=finalIndex)
        {
            btnNextWord.setVisibility(View.GONE);
        }
        else
        {
            btnNextWord.setVisibility(View.VISIBLE);
        }*/
        /*for(int i=0;i<arrWordSel.size();i++)
        {*/
            /*View child = getLayoutInflater().inflate(R.layout.meaning_display_item, null);
            TextView txtvwEng=(TextView) child.findViewById(R.id.txtvwEng);
            TextView txtvwHindi=(TextView) child.findViewById(R.id.txtvwHindi);*/
        //txtvwSelectedProunoce.setText("("+pronounce+")");
        txtvwHindi.setTypeface(typeface);
        String eng=todayWord.getEnglish_meaning();
        if(Character.isDigit(eng.charAt(0)))
            eng=eng.substring(1,eng.length());
        Character str1=eng.charAt(0);
        if(str1.equals('.'))
            eng=eng.substring(1,eng.length());

        txtvwSelectedWord.setText(eng);
        txtvwHindi.setText(todayWord.getHindi_meaning());

        if (todayWord.isFavorite()){
            imgbtnFav.setImageResource(R.drawable.fav_sel);
        }
        else {
            imgbtnFav.setImageResource(R.drawable.fav);
        }

        imgbtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todayWord.isFavorite()) {
                    imgbtnFav.setImageResource(R.drawable.fav);
                    todayWord.setFavorite(false);
                    wordDao.removeFromFavorite(todayWord.getEngId());

                } else {
                    imgbtnFav.setImageResource(R.drawable.fav_sel);
                    todayWord.setFavorite(true);
                    wordDao.makeFavorite(todayWord.getEngId());

                }
            }
        });

        imgbtnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String str=txtvwSelectedWord.getText().toString();
                String str=txtvwHindi.getText().toString();
//                df
                t1.speak(str, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

      //  imgbtnShare.setOnClickListener(new View.OnClickListener() {
          /*  @Override
            public void onClick(View v) {
                shareAction();
            }

        });*/



    }

    private void setWordDetail()
    {

        MeaningDatabse db = new MeaningDatabse(getActivity());
        WordDetail wordDetail = db.getWordDetails(todayWord.getEnglish_meaning());


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
            txtvw_WordType.setText(todayWord.getEnglish_meaning() + " (" + wordDetail.getWord_type() + ")");
            txtvw_Example.setText(wordDetail.getExample());
            linlay_wordDetailRoot.addView(child);
            card_view3.setVisibility(View.VISIBLE);
        } else {
            card_view3.setVisibility(View.GONE);
        }


    }

    private void shareAction()
    {
        String strSelected="";
        strSelected= "Selected Word:\n "+todayWord.getEnglish_meaning() +" \n "+txtvwHindi.getText();
        strSelected= strSelected+"\n\nInstall the "+getString(R.string.app_name)+" app. "+getString(R.string.app_link);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, strSelected);
        startActivity(Intent.createChooser(intent, "Share with"));

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isResumed()) {
                if(todayWord==null) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    wordIndex = pref.getInt("WordIndex", 1);
                    if (wordDao == null)
                        wordDao = new WordDaoImpl(new DatabaseOpenHelper(getActivity()).getReadWritableDatabase());
                    todayWord = wordDao.getWordFromId(wordIndex);
                    String meaning=wordDao.getMeaningFromEnglishWord(todayWord.getEngId());
                    todayWord.setHindi_meaning(meaning);
                   // todayWord = wordDao.getWordFromId(wordIndex);
                    if (todayWord != null) {
                        String word = todayWord.getEnglish_meaning();
                        if (word != null && word.length() > 0) {
                            String upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
                           // pronounce = wordDao.getProunceFromWordName("" + todayWord.getEnglish_meaning());
                        }
                    }
                    setWordData();
                }
                else
                    setWordData();
            }
        }

    }
}
