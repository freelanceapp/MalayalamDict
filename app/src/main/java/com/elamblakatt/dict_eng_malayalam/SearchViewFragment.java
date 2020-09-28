package com.elamblakatt.dict_eng_malayalam;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.elamblakatt.dict_eng_malayalam.adapter.WordListAdapter;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.WordDao;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;
import com.elamblakatt.dict_eng_malayalam.ocr.OcrCaptureActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.CommonStatusCodes;

import static android.app.Activity.RESULT_OK;

/**
 * Created by admin on 11/16/2016.
 */

public class SearchViewFragment extends Fragment
{
//    @BindView(R.id.jjsv)
//    JJSearchView mJJSearchView;
    @BindView(R.id.searchView1)
    SearchView searchView1;
    @BindView(R.id.lvSearchResults)
    ListView lvSearchResults;
    @BindView(R.id.btnImage)
    ImageButton btnImage;
    @BindView(R.id.btnSpeak)
    ImageButton btnSpeak;
    @BindView(R.id.rellay_empty)
    RelativeLayout rellay_empty;
    @BindView(R.id.txtvw_empty)
    TextView txtvw_empty;
    private static final int RC_OCR_CAPTURE = 9003;
    private WordDao wordDao;
    private WordListAdapter wordListAdapter;
    private final int REQ_CODE_SPEECH_INPUT = 100;
   // private  ImageView closeButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View v = inflater.inflate(R.layout.search_fragment, container,
                false);
        ButterKnife.bind(this, v);
        AdView mAdView1 = (AdView) v.findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);
        wordDao = new WordDaoImpl(new DatabaseOpenHelper(getActivity().getApplicationContext()).getReadableDatabase());
        wordListAdapter = new WordListAdapter(getActivity());
        lvSearchResults.setAdapter(wordListAdapter);

//        mJJSearchView.setController(new JJChangeArrowController());
//                mJJSearchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mJJSearchView.startAnim();
//            }
//        });
        changeSearchViewTextColor(searchView1);
//        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
//        ImageView magImage = (ImageView) searchView1.findViewById(magId);
//        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));


        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OcrCaptureActivity.class);

                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

//
//        searchView1 .requestFocus();

        searchView1.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view.findFocus());
                }

            }
        });

//        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
//                toggleSoftInput(InputMethodManager.SHOW_FORCED,
//                        InputMethodManager.HIDE_IMPLICIT_ONLY);

        //***setOnQueryTextListener***
        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

//                if(query.length()==1)
//                    mJJSearchView.startAnim();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                rellay_empty.setVisibility(View.GONE);

//                if(newText.length()==1)
//                    mJJSearchView.startAnim();
//                else if(newText.length()==0)
//                    mJJSearchView.resetAnim();
                /*if(newText.length()>0)
                {
                    closeButton.setImageResource(R.drawable.cancel);
                }
                else if(newText.length()==0)
                {
                    closeButton.setImageResource(R.drawable.mike);
                    closeButton.setVisibility(View.VISIBLE);
                }*/

                String englishWordPrefix = newText;
                List<Word> wordList;
                int lang=0;
                if (englishWordPrefix.length()<2){
                    wordList = new ArrayList<Word>();
                }
                else{
                    //Word exactWord = wordDao.getWordByEnglishWordName(englishWordPrefix);
                    wordList = wordDao.getWordListByPrefixMatching(englishWordPrefix);

                    if(wordList==null || wordList.size()==0)
                    {
                        wordList = wordDao.getWordListByPrefixMatching_Language(englishWordPrefix);
                        lang=1;
                    } if(wordList==null || wordList.size()==0)
                    {
                        rellay_empty.setVisibility(View.VISIBLE);
                        txtvw_empty.setText(""+getString(R.string.search_empty));
                    }
                    else
                    {
                        rellay_empty.setVisibility(View.GONE);
                    }


                    //if (exactWord != null && wordList.size() > 1){
                      //  wordList.add(0, exactWord);
                //    }
                }
              /*  if (wordList.size() == 0){
                    noWordFound.setVisibility(View.VISIBLE);
                }
                else {
                    noWordFound.setVisibility(View.GONE);
                }*/
                wordListAdapter.setWordList(wordList,lang);
                return false;
            }
        });
    /*    final int searchCloseButtonId = searchView1.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        closeButton = (ImageView)v.findViewById(searchCloseButtonId);
        closeButton.setImageResource(R.drawable.mike);
        closeButton.setVisibility(View.VISIBLE);
        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if(searchView1.getQuery().length()>0)
                {
                    searchView1.setQuery("",true);
                    closeButton.setImageResource(R.drawable.mike);
                    closeButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    promptSpeechInput();
                }

            }
        });*/


        if (getUserVisibleHint())
        {
//            searchView1.setFocusable(true);
//            searchView1.setIconified(false);
//            searchView1.requestFocusFromTouch();
        }
        return v;
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }
   /* @SuppressLint("NewApi")
    private void printInputLanguages() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> ims = imm.getEnabledInputMethodList();

        for (InputMethodInfo method : ims) {
            List<InputMethodSubtype> submethods = imm.getEnabledInputMethodSubtypeList(method, true);
            for (InputMethodSubtype submethod : submethods) {
                if (submethod.getMode().equals("keyboard")) {
                    String currentLocale = submethod. getLanguageTag();
                    Log.i(TAG, "Available input method locale: " + currentLocale);
                }
            }
        }
    }*/


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

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.BLACK);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        {

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            if(isResumed())
            {
//                searchView1.setFocusable(true);
//                searchView1.setIconified(false);
//                searchView1.requestFocusFromTouch();

            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView1.setQuery(""+result.get(0).toString(),true);
                }
                break;
            }
            case RC_OCR_CAPTURE:{
                if(resultCode== CommonStatusCodes.SUCCESS && data!=null)
                {
                  String value=  data.getExtras().get("String").toString();
                    if(value!=null)
                    {
                        searchView1.setQuery(value,true);
                    }
                }
                break;
            }

        }
    }
}
