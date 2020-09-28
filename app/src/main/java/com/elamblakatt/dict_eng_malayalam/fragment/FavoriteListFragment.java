package com.elamblakatt.dict_eng_malayalam.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.adapter.FavoriteListAdapter;
import com.elamblakatt.dict_eng_malayalam.dao.DatabaseOpenHelper;
import com.elamblakatt.dict_eng_malayalam.dao.WordDaoImpl;
import com.elamblakatt.dict_eng_malayalam.model.Word;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class FavoriteListFragment extends Fragment {
    private WordDaoImpl wordDao=null;
    @BindView(R.id.lstvwFav)
    ListView lstvwFav;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rellayDeleteHistoryAll)
    RelativeLayout rellayDeleteHistoryAll;
    @BindView(R.id.rellay_empty)
    RelativeLayout rellay_empty;
    @BindView(R.id.txtvw_empty)
    TextView txtvw_empty;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favorite_fragment, container, false);
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Favorite");

        AdView mAdView1 = (AdView) rootView.findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);
        if(getUserVisibleHint())
        {
            if(wordDao==null)
                wordDao = new WordDaoImpl(new DatabaseOpenHelper(getActivity()).getReadWritableDatabase());

            List<Word> favoriteWordList = wordDao.getFavoriteWordList();
            lstvwFav.setAdapter(new FavoriteListAdapter(getActivity(),favoriteWordList));
            if(favoriteWordList==null||(favoriteWordList!=null && favoriteWordList.size()==0))
            {
                // Toast.makeText(getActivity(),"No Words added in Favorite",Toast.LENGTH_SHORT).show();;
                rellay_empty.setVisibility(View.VISIBLE);
                txtvw_empty.setText(""+getString(R.string.fav_empty));
            }
            else
            {
                rellay_empty.setVisibility(View.GONE);
            }
        }
        rellayDeleteHistoryAll.setVisibility(View.GONE);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isResumed()){
                if(wordDao==null)
                    wordDao = new WordDaoImpl(new DatabaseOpenHelper(getActivity()).getReadWritableDatabase());

                List<Word> favoriteWordList = wordDao.getFavoriteWordList();
                lstvwFav.setAdapter(new FavoriteListAdapter(getActivity(),favoriteWordList));
                if(favoriteWordList==null||(favoriteWordList!=null && favoriteWordList.size()==0))
                {
                    // Toast.makeText(getActivity(),"No Words added in Favorite",Toast.LENGTH_SHORT).show();;
                    rellay_empty.setVisibility(View.VISIBLE);
                    txtvw_empty.setText("No Words added in Favorite");
                }
                else
                {
                    rellay_empty.setVisibility(View.GONE);
                }
            }
        }
    }
}
