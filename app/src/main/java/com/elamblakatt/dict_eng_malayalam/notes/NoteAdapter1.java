package com.elamblakatt.dict_eng_malayalam.notes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.elamblakatt.dict_eng_malayalam.R;
import com.elamblakatt.dict_eng_malayalam.Utils.Utils;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class NoteAdapter1 extends RecyclerView.Adapter<NoteAdapter1.ViewHolder> {


    private ArrayList<Note> notes;
    private LayoutInflater inflater;
    private Activity mActivity;
    private SparseBooleanArray selectedItems;
    private SharedPreferences sharedPreferences;
    String mode;
    private OnClickListItemListerner listerner;

    public NoteAdapter1(Activity activity, ArrayList<Note> notes, OnClickListItemListerner listerner1) {
        selectedItems = new SparseBooleanArray();
        this.mActivity = activity;
        this.notes = notes;
        this.listerner = listerner1;
        //  expandedView = layout == R.layout.note_layout_expanded;
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_note1, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Note note = notes.get(position);
//        holder.textTitle.setText(""+note.getTitle());
        holder.textContent.setText(""+note.getContent());

        holder.textCreateddate.setText(""+ Utils.getFormattedDisplayDate(note.getCreatedDate()));
        holder.textContent.setTag(position);

        boolean expanded = note.isExpanded();
        // Set the visibility based on state
        holder.linlayExtraOption.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.imgArrow.setImageResource(expanded ? R.drawable.ic_down_arrow : R.drawable.arrow_next);

        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listerner.onItemShareClick(position);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listerner.onItemDeleteClick(position);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listerner.onItemEditClick(position);
            }
        });



        holder.relayMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note = notes.get(position);
//                int position = (int)(view.getTag());
                for( int i = 0; i< notes.size(); i++ ){
                    notes.get(i).setExpanded(false);
                }
                boolean expanded = note.isExpanded();
                note.setExpanded(!expanded);
                holder.linlayExtraOption.setVisibility(View.VISIBLE);
//                notifyItemChanged(position);
                notifyDataSetChanged();
            }
        });



        try
        {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date currentDate = sdf.parse(note.getCreatedDate().toString());
            Calendar calenderDate= Calendar.getInstance();
            calenderDate.setTime(currentDate);
            holder.text_cal_date.setText(""+calenderDate.get(Calendar.DAY_OF_MONTH));
            holder.text_cal_month.setText(""+ Utils.getMonthName(calenderDate.get(Calendar.MONTH)));
            holder.text_cal_year.setText(""+calenderDate.get(Calendar.YEAR));
        }
        catch (Exception ex)
        {

        }


    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textContent,textCreateddate,text_cal_month,text_cal_date,text_cal_year;
        ImageView imgArrow;
        LinearLayout linlayExtraOption;
        Button btnShare,btnDelete, btnEdit;
        RelativeLayout relayMain;
        public ViewHolder(View view) {
            super(view);
//            textTitle=(TextView)view.findViewById(R.id.text_title);
            textContent=(TextView)view.findViewById(R.id.text_content);
//            imgAttachment=(ImageView)view.findViewById(R.id.image_attachment);
            textCreateddate=(TextView)view.findViewById(R.id.text_createddate);
//            text_tag=(TextView)view.findViewById(R.id.text_tag);
            relayMain=(RelativeLayout)view.findViewById(R.id.relayMain);
            text_cal_month =(TextView)view.findViewById(R.id.text_cal_month);
            text_cal_date=(TextView)view.findViewById(R.id.text_cal_date);
            text_cal_year=(TextView)view.findViewById(R.id.text_cal_year);
            linlayExtraOption = (LinearLayout)view.findViewById(R.id.linlayExtraOption);
            btnShare = (Button)view.findViewById(R.id.btnShare);
            btnDelete = (Button)view.findViewById(R.id.btnDelete);
            btnEdit = (Button)view.findViewById(R.id.btnEdit);
            imgArrow= (ImageView)view.findViewById(R.id.imgArrow);
//            textContent.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }
    public void refreshList(ArrayList<Note> notes)
    {
     this.notes=notes;
        notifyDataSetChanged();
    }
    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);

        }
        notifyItemChanged(pos);
    }
    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }
    public int getSelectedItemCount() {
        return selectedItems.size();
    }
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public boolean isItemSelected(int id){
        return selectedItems.get(id, false);
    }
    public void removeItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }
    public Note getItem(int position) {
        return notes.get(position);
    }
    public void setFilter(List<Note> countryModels) {
        notes = new ArrayList<>();
        notes.addAll(countryModels);
        notifyDataSetChanged();
    }
}
