package com.elamblakatt.dict_eng_malayalam.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.adapter.HistoryListAdapter;
import com.elamblakatt.dict_eng_malayalam.adapter.HistoryListNewAdapter;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SeachHistoryListActivity extends AppCompatActivity {
    private WordDaoImpl wordDao=null;
    @BindView(R.id.recylerFav)
    RecyclerView recylerFav;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//     @BindView(R.id.rellayDeleteHistoryAll)
//    RelativeLayout rellayDeleteHistoryAll;
    @BindView(R.id.rellay_empty)
    RelativeLayout rellay_empty;
    @BindView(R.id.txtvw_empty)
    TextView txtvw_empty;
    private Context mContext;
    private RecyclerView.LayoutManager layoutManager;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        ButterKnife.bind(this);

        mContext = SeachHistoryListActivity.this;

        if (getSupportActionBar() != null){
////            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Search History");
        }


        AdView mAdView1 = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);



            if(wordDao==null)
                wordDao = new WordDaoImpl(new DatabaseOpenHelper(this).getReadWritableDatabase());

            List<Word> favoriteWordList = wordDao.getSearchHistory();

        HistoryListNewAdapter adapter = new HistoryListNewAdapter(favoriteWordList,mContext);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recylerFav.setLayoutManager(layoutManager);
        recylerFav.setAdapter(adapter);
//        recylerFav.setAdapter(new HistoryListNewAdapter(this,favoriteWordList));
//            if(favoriteWordList!=null && favoriteWordList.size()>0)
//                rellayDeleteHistoryAll.setVisibility(View.VISIBLE);
//            else
//                rellayDeleteHistoryAll.setVisibility(View.GONE);
            if(favoriteWordList==null||(favoriteWordList!=null && favoriteWordList.size()==0))
            {
                //    Toast.makeText(getActivity(),"Search History is empty",Toast.LENGTH_SHORT).show();;
                rellay_empty.setVisibility(View.VISIBLE);
                txtvw_empty.setText(""+getString(R.string.history_empty));
            }
            else
            {
                rellay_empty.setVisibility(View.GONE);
            }

        //rellayDeleteHistoryAll.setVisibility(View.VISIBLE);
//        rellayDeleteHistoryAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDeleteDialog();
//
//            }
//        });
//        getActivity().invalidateOptionsMenu();

    }






//
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//           if (isResumed()){
//               if(wordDao==null)
//                   wordDao = new WordDaoImpl(new DatabaseOpenHelper(getActivity()).getReadWritableDatabase());
//
//               List<Word> favoriteWordList = wordDao.getSearchHistory();
//                lstvwFav.setAdapter(new HistoryListAdapter(getActivity(),favoriteWordList));
//                if(favoriteWordList!=null && favoriteWordList.size()>0)
//                    rellayDeleteHistoryAll.setVisibility(View.VISIBLE);
//               else
//                    rellayDeleteHistoryAll.setVisibility(View.GONE);
//
//               if(favoriteWordList==null||(favoriteWordList!=null && favoriteWordList.size()==0))
//               {
//                   rellay_empty.setVisibility(View.VISIBLE);
//                   txtvw_empty.setText("Search History is empty");
//               }
//               else
//               {
//                   rellay_empty.setVisibility(View.GONE);
//               }
//            }
//        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.nav_delete_all:
                showDeleteDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showDeleteDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
               this,android.R.style.Theme_Material_Light_Dialog_Alert);
        // set title
        alertDialogBuilder.setTitle(""+this.getString(R.string.app_name));

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to delete Search History?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                       /* if(NetworkStatus.getInstance(MainActivity.this).isOnline())
                            showInterstitialAd();
                        else*/
                       wordDao.deleteAllHistory();
//                        rellayDeleteHistoryAll.setVisibility(View.GONE);
//                        List<Word> favoriteWordList = wordDao.getSearchHistory();
//                       recylerFav.relo
//                        lstvwFav.setAdapter(new HistoryListAdapter(mContext,favoriteWordList));
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}
