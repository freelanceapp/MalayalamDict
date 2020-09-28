package com.elamblakatt.dict_eng_malayalam.notes;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elamblakatt.dict_eng_malayalam.HorizontalNtbActivity;
import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.notes.DataBaseHelperNoteImage;
import com.elamblakatt.dict_eng_malayalam.notes.Note;
import com.elamblakatt.dict_eng_malayalam.notes.NoteAdapter1;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jasmine Nitin on 3/29/2018.
 */


public class NoteListFragment extends Fragment implements  RecyclerView.OnItemTouchListener,ActionMode.Callback,View.OnClickListener,SearchView.OnQueryTextListener,OnClickListItemListerner
{

    private RecyclerView recyclerView;
    private TextView textWritenow;
    private FloatingActionButton fab;
    private GestureDetectorCompat gestureDetector;
    private AppCompatActivity mActivity;
    private TextView listFooter;
    private NoteAdapter1 listAdapter;
    private ArrayList<Note> notes;
    //For Other
//        private DatabaseOpenHelper dbHelper;

    //For English
    private DataBaseHelperNoteImage dbHelper;
    private String mActionModeTitle;
    private TextView mActionModeTitleTextView;
    private ImageView mActionImageView,mActionDelete;
    private ActionMode actionMode;
    private RecyclerView.LayoutManager layoutManager;
    private RelativeLayout rellayNoteRoot;
    private MenuItem menu_DisplaySel;
    public static int NOTE_CREATE_REQUEST_CODE_FROM_LIST=90;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notelist, container, false);
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        recyclerView=(RecyclerView) view.findViewById(R.id.recylerview);
        textWritenow=(TextView)view.findViewById(R.id.text_writenow);
        rellayNoteRoot=(RelativeLayout)view.findViewById(R.id.rellayNoteRoot);
        fab=(FloatingActionButton)view.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Favorite");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notes");

        initListView();
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(""+getString(R.string.app_name));
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (HorizontalNtbActivity)getActivity();

        if(((AppCompatActivity) mActivity).getSupportActionBar()!=null) {
            ((AppCompatActivity) mActivity).getSupportActionBar().setTitle("Notes");
            ((AppCompatActivity) mActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }


        setHasOptionsMenu(true);
    }

    private void setViewofDisplay() {


        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        notes = dbHelper.getNoteList();
        if(notes!=null && notes.size()>0)
        {
            textWritenow.setVisibility(View.GONE);
        }
        else
        {
            textWritenow.setVisibility(View.VISIBLE);
        }

        listAdapter = new NoteAdapter1(getActivity(), notes,this);
        recyclerView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

    }
    /**
     * Notes list initialization. Data, actions and callback are defined here.
     */
    private void initListView() {




        // For other
//            dbHelper=new DatabaseOpenHelper(getActivity().getApplicationContext());

        //for English
        dbHelper= new DataBaseHelperNoteImage(getActivity().getApplicationContext());

        setViewofDisplay();

        recyclerView.addOnItemTouchListener(this);


        gestureDetector = new GestureDetectorCompat(mActivity,
                new RecyclerViewOnGestureListener());

        textWritenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateCreateNote();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigateCreateNote();
            }
        });



    }

    public void navigateCreateNote(Note note)
    {

        Intent intent =new Intent(getActivity(),CreateNoteActivity.class);

        Bundle bundle= new Bundle();
        bundle.putParcelable("noteSelected",note);
        intent.putExtras(bundle);

        getActivity().startActivityForResult(intent,NOTE_CREATE_REQUEST_CODE_FROM_LIST);
        // MainActivity.this.overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up);
        //MainActivity.this.overridePendingTransition( R.anim.slide_in_up,0);
        getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
        //   FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        // fragmentTransaction.replace(R.id.content_main, new CreateNoteFragment(), FRAGMENT_LIST_TAG).commit();

    }


    public void navigateCreateNote()
    {

        Intent intent =new Intent(getActivity(),CreateNoteActivity.class);

        getActivity().startActivityForResult(intent,NOTE_CREATE_REQUEST_CODE_FROM_LIST);
        // MainActivity.this.overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up);
        //MainActivity.this.overridePendingTransition( R.anim.slide_in_up,0);
        getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
        //   FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        // fragmentTransaction.replace(R.id.content_main, new CreateNoteFragment(), FRAGMENT_LIST_TAG).commit();

    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onResume() {
        super.onResume();
//        ((HorizontalNtbActivity) getActivity()).enableViews(false);
        if(notes!=null && notes.size()>0)
        {
            textWritenow.setVisibility(View.GONE);
        }
       /* if(dbHelper!=null) {
            notes = dbHelper.getNoteList();
            listAdapter.refreshList(notes);

        }*/

    }

    @Override
    public void onItemShareClick(int pos) {

        String note = notes.get(pos).getContent();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, note);
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    @Override
    public void onItemDeleteClick(int pos) {
        Note note = notes.get(pos);
        dbHelper.deleteNote(""+note.getId());
        notes.remove(pos);
        if(notes!=null && notes.size()>0)
        {
            textWritenow.setVisibility(View.GONE);
        }
        else
        {
            textWritenow.setVisibility(View.VISIBLE);
        }
        listAdapter.refreshList(notes);


    }


    @Override
    public void onItemEditClick(int pos) {
        navigateCreateNote(notes.get(pos));
    }


    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = actionMode.getMenuInflater();

        View customNav = LayoutInflater.from(getActivity()).inflate(R.layout.item_add_actionmode, null);
        actionMode.setCustomView(customNav);

        mActionModeTitleTextView = (TextView) customNav.findViewById(R.id.tv_am_title);
        mActionImageView = (ImageView) customNav.findViewById(R.id.iv_share);
        mActionDelete = (ImageView) customNav.findViewById(R.id.iv_remove);

        // inflater.inflate(R.menu.menu_cab_propertylistfragment, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // This is to highlight the status bar and distinguish it from the action bar,
            // as the action bar while in the action mode is colored app_green_dark
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColorDark));

        }
        mActivity.getSupportActionBar().hide();
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            /* case R.id.menu_share:
             *//* List<Integer> selectedItemPositions = listAdapter.getSelectedItems();
                int currPos;
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    currPos = selectedItemPositions.get(i);
                    helper = new RRShareHelper(mActivity, listAdapter
                            .getItem(currPos).getPropertyName(), listAdapter.getItem(
                            currPos).getComments(), null, listAdapter.getItem(currPos));
                    wantToSendImage = false;
                    helper.share(wantToSendImage);
                }*//*
                actionMode.finish();
                return true;*/

            default:
                return false;
        }
    }

    private void myToggleSelection(int idx) {
        listAdapter.toggleSelection(idx);
        mActionModeTitle = getString(R.string.selected_count,
                listAdapter.getSelectedItemCount());

        mActionModeTitleTextView.setText(mActionModeTitle);

        if(listAdapter!=null && listAdapter.getSelectedItems()!=null && listAdapter.getSelectedItems().size()>1)
        {
            mActionImageView.setVisibility(View.GONE);
        }
        else
        {
            mActionImageView.setVisibility(View.VISIBLE);
        }

        mActionImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                List<Integer> selectedItemPositions1 = listAdapter.getSelectedItems();
                int currPos = selectedItemPositions1.get(0);


                String strSelected=listAdapter.getItem(currPos).getTitle()+"\n"+ listAdapter.getItem(currPos).getContent();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, strSelected);
                startActivity(Intent.createChooser(intent, "Share with"));

            }
        });
        mActionDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



            }
        });


    }

    @Override
    public void onClick(View view) {
        /*
         * if (view.getId() == R.iond.fab_add) { // fab click addItemToList(); }
         * else
         */
        if (view.getId() == R.id.ll_root) {
            // item click
            int idx = recyclerView.getChildPosition(view);
            if (actionMode != null) {
                myToggleSelection(idx);
                return;
            }
            else
            {
                if(idx>=0) {

                    Note item = listAdapter.getItem(idx);

                    if (item != null) {
                        navigateCreateNote(item);
                    }

                }
            }
            // DemoModel data = adapter.getItem(idx);
            // View innerContainer =
            // view.findViewById(R.id.container_inner_item);
            // innerContainer.setViewName(Constants.NAME_INNER_CONTAINER + "_" +
            // data.id);
            // Intent startIntent = new Intent(this,
            // CardViewDemoActivity.class);
            // startIntent.putExtra(Constants.KEY_ID, data.id);
            // ActivityOptions options = ActivityOptions
            // .makeSceneTransitionAnimation(this, innerContainer,
            // Constants.NAME_INNER_CONTAINER);
            // this.startActivity(startIntent);
        }
    }



    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (listAdapter!=null && listAdapter.getItemCount() == 0) {
                return super.onSingleTapConfirmed(e);
            }

            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            int idx = recyclerView.getChildPosition(view);
            if(idx>=0) {

                if (view != null)
                    onClick(view);

            }
            if (actionMode != null && listAdapter.getSelectedItemCount() == 0) {
                // mActivity.getSupportActionBar().show();
                actionMode.finish();
            }
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            if (listAdapter.getItemCount() == 0) {
                super.onLongPress(e);
                return;
            }
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

            if (actionMode != null) {
                return;
            }
            // Start the CAB using the ActionMode.Callback defined above
            //actionMode = mActivity.startActionMode(PropertyListFragment.this);
            int idx = recyclerView.getChildPosition(view);
            if(idx>=0) {

                actionMode = mActivity.startActionMode(NoteListFragment.this);
                myToggleSelection(idx);


                // mActivity.getSupportActionBar().hide();
                /* Vibrating */
                // vibratePhone(getActivity());
            }
            super.onLongPress(e);
        }
    }

    private void vibratePhone(Context mContext) {
        Vibrator vibe = (Vibrator) mContext
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(20);
    }


    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
        listAdapter.clearSelections();
        mActivity.getSupportActionBar().show();
        // fab.setVisibility(View.VISIBLE);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_note, menu);

        final MenuItem item = menu.findItem(R.id.action_search);


        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
// Do something when collapsed
                        listAdapter.setFilter(notes);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
// Do something when expanded
                        return true; // Return true to expand action view
                    }
                });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onQueryTextChange(String newText) {

        final List<Note> filteredModelList = filter(notes, newText);
        listAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Note> filter(List<Note> models, String query) {
        query = query.toLowerCase();
        final List<Note> filteredModelList = new ArrayList<>();
        for (Note model : models) {
            if (model.getTitle()!= null && !model.getTitle().isEmpty()) {
                final String text = model.getTitle().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==NOTE_CREATE_REQUEST_CODE_FROM_LIST)
        {
            if (dbHelper != null) {
                notes = dbHelper.getNoteList();
                listAdapter.refreshList(notes);

            }

        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==NOTE_CREATE_REQUEST_CODE)
        {
            mFragmentManager.findFragmentByTag(FRAGMENT_LIST_TAG).onActivityResult(requestCode, resultCode, data);
        }
    }*/




}
