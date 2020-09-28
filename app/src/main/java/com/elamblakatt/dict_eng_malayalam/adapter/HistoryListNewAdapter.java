package com.elamblakatt.dict_eng_malayalam.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.elamblakatt.dict_eng_malayalam.MeaningDisplayActivity;
import com.elamblakatt.dict_eng_malayalam.MeaningDisplayActivityInLan;
import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.config.Configuration;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.WordDao;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;

import java.util.List;

public class HistoryListNewAdapter extends RecyclerView.Adapter<HistoryListNewAdapter.ViewHolder>{
    private List<Word> wordList;
    private WordDao wordDao;
    private Context context;
    private TypedArray optionIcons;
    private String[] optionNames;
    private Typeface hindiTypeFace;

    // RecyclerView recyclerView;
    public HistoryListNewAdapter(List<Word> listdata, Context context) {
        this.wordList = listdata;
        wordDao = new WordDaoImpl(new DatabaseOpenHelper(context).getReadWritableDatabase());
        context = context;
        optionNames = context.getResources().getStringArray(R.array.option_names);
        optionIcons = context.getResources().obtainTypedArray(R.array.option_icons);
        hindiTypeFace = Typeface.createFromAsset(context.getAssets(), Configuration.HINDI_TYPEFACE_PATH);

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.favorite_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);


        view = layoutInflater.inflate(R.layout.favorite_list_item, null);
        viewHolder = new HistoryListNewAdapter.ViewHolder(view);



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.english.setText(word.getEnglish_meaning());
        // holder.hindi.setText(word.getHindi_meaning());
        // holder.hindi.setTypeface(hindiTypeFace);

        holder.wordContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(word.getEngId()!=0 && word.getMl_id()==0) {
                    String meaning = wordDao.getMeaningFromEnglishWord(word.getEngId());
                    word.setHindi_meaning(meaning);
                    wordDao.addToSearchHistory(word);
                    Intent intent = new Intent(context, MeaningDisplayActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("WordSel", word);//new ArrayList<Word>(lstExactWord));
                    bundle.putBoolean("TodayWord", false);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
                else if(word.getEngId()==0 && word.getMl_id()!=0)
                {
                    String meaning = wordDao.getMeaningFromLangWord(word.getMl_id());
                    word.setHindi_meaning(meaning);
                    wordDao.addToSearchHistory(word);
                    Intent intent = new Intent(context, MeaningDisplayActivityInLan.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("WordSel", word);//new ArrayList<Word>(lstExactWord));
                    bundle.putBoolean("TodayWord", false);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });



        holder.wordContainer.setLongClickable(true);
        holder.wordContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog optionDialog = new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.choose_one_option) + " " +  word.getEnglish_meaning())
                        .setAdapter(new OptionAdapter(context, optionNames, optionIcons), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        LayoutInflater  layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                        View updateView = layoutInflater.inflate(R.layout.update_word,null);
                                        final TextView englishView = (TextView) updateView.findViewById(R.id.english);
                                        final TextView banglaView = (TextView) updateView.findViewById(R.id.hindi);
                                        banglaView.setTypeface(hindiTypeFace);
                                        englishView.setText(word.getEnglish_meaning());
                                        banglaView.setText(word.getHindi_meaning());
                                        AlertDialog updateDialog = new AlertDialog.Builder(context)
                                                .setTitle(R.string.update_word)
                                                .setView(updateView)
                                                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String englishText = englishView.getText().toString();
                                                        String hindiText = banglaView.getText().toString();
                                                        if (englishText.length() == 0 || hindiText.length() == 0) {
                                                            return;
                                                        }
                                                        word.setEnglish_meaning(englishText);
                                                        word.setHindi_meaning(hindiText);
                                                        wordDao.updateWord(word);
                                                        notifyDataSetChanged();
                                                    }
                                                })
                                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .create();
                                        updateDialog.show();
                                        break;
                                    case 1:
                                        wordDao.deleteWord(word.getEngId());
                                        wordList.remove(position);
                                        notifyDataSetChanged();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .create();
                optionDialog.show();
                return true;
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wordDao.removeWordFromHistory(word);
                wordList.remove(position);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout wordContainer;
        private TextView english;
        private TextView hindi;
        private ImageView remove;
        public ViewHolder(View itemView) {
            super(itemView);
          wordContainer = (LinearLayout) itemView.findViewById(R.id.word_container);
            english = (TextView) itemView.findViewById(R.id.english);
            hindi = (TextView) itemView.findViewById(R.id.hindi);
            remove = (ImageView) itemView.findViewById(R.id.remove);
        }
    }
}
