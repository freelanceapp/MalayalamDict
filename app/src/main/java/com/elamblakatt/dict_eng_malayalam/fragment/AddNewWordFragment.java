package com.elamblakatt.dict_eng_malayalam.fragment;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.config.Configuration;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.WordDao;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddNewWordFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.english)
    EditText english;
    @BindView(R.id.hindi)
    EditText bangla;
    @BindView(R.id.add)
    Button add;
    WordDao wordDao;

    Typeface hindiTypeFace;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hindiTypeFace = Typeface.createFromAsset(getActivity().getAssets(), Configuration.HINDI_TYPEFACE_PATH);
        wordDao = new WordDaoImpl(new DatabaseOpenHelper(getActivity()).getReadWritableDatabase());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.add_new_word, null);
        ButterKnife.bind(this,view);
        add.setOnClickListener(this);
        bangla.setTypeface(hindiTypeFace);
        return view;
    }

    @Override
    public void onDestroy() {
//        ButterKnife.
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add){
            String englishWord = english.getText().toString().trim();
            final String banglaMeaning = bangla.getText().toString().trim();
            if (englishWord.length() == 0){
                showToast(getString(R.string.english_word_name_require));
            }
            if (banglaMeaning.length()==0){
                showToast(getString(R.string.bangla_meaning_require));
            }
            if (englishWord.length() != 0 && banglaMeaning.length() != 0){
                final Word word = wordDao.getWordByEnglishWordName(englishWord);
                if (word != null){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).
                            setTitle("Update?")
                            .setMessage(englishWord + " word already exists.\n" + "Do you want update it?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    word.setHindi_meaning(banglaMeaning);
                                    wordDao.updateWord(word);
                                    moveHomeView();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    alertDialog.show();
                }
                else {
                    wordDao.insertWord(new Word(englishWord,banglaMeaning,false));
                    moveHomeView();
                }
            }
        }
    }

    private void moveHomeView() {
        english.setText("");
        bangla.setText("");
       // ((MainActivity)getActivity()).displayView(0);
    }

    void showToast(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();
    }
}
