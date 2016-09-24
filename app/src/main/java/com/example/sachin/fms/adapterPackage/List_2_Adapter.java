package com.example.sachin.fms.adapterPackage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.example.sachin.fms.R;
import com.example.sachin.fms.database.DBHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class List_2_Adapter extends RecyclerView.Adapter<List_2_Adapter.Add_View> {


    public HashMap<String, String> data = new HashMap<>();
    public List<String> code = Collections.emptyList();
    public Context context;
    public OnDescClick click;

    public DBHelper helper;

    public List_2_Adapter(Context context, HashMap<String, String> list, List<String> code, DBHelper helper) {
        this.context = context;
        this.data = list;
        this.code = code;
        this.helper = helper;

    }


    @Override
    public Add_View onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_2, parent, false);
        Add_View holder = new Add_View(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Add_View holder, int position) {


        holder.description.setText(data.get(code.get(position)));


        boolean b = helper.isChecked(code.get(position));

        if (b) {
            holder.description.setChecked(true);

        } else {
            holder.description.setChecked(false);

        }


    }

    public void SetOnDescClick(OnDescClick click) {
        this.click = click;

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface OnDescClick {
        void OnDescClick(View v, int position, boolean checked);
    }

    public class Add_View extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckedTextView description;

        public Add_View(View itemView) {
            super(itemView);
            description = (CheckedTextView) itemView.findViewById(R.id.description);

            description.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {


            if (click != null) {
                if (!description.isChecked()) {
                    description.setChecked(true);
                    click.OnDescClick(v, getAdapterPosition(), true);
                } else if (description.isChecked()) {


                    description.setChecked(false);
                    click.OnDescClick(v, getAdapterPosition(), false);

                }

            }

        }
    }
}
