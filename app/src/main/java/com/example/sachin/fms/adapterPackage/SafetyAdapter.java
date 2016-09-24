package com.example.sachin.fms.adapterPackage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.sachin.fms.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class SafetyAdapter extends RecyclerView.Adapter<SafetyAdapter.SafetyHolder> {

    List<CheckedTextView> ctv = new ArrayList<>();
    List<String> data = Collections.emptyList();
    List<String> saved = Collections.emptyList();
    HashMap<String, String> code_list = new HashMap<>();
    Context context;
    SetOnItemClick click;
    boolean[] itemChecked;

    public SafetyAdapter(Context context, List<String> data, List<String> saved, HashMap<String, String> code_list) {
        this.code_list = code_list;
        this.saved = saved;
        this.context = context;
        this.data = data;
        itemChecked = new boolean[data.size()];
    }


    @Override
    public SafetyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rules, parent, false);
        SafetyHolder holder = new SafetyHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(SafetyHolder holder, int position) {


        String str = Integer.toString((position + 1));

        holder.no.setText(str);
        holder.checkbox.setText(data.get(position));


        if (!saved.isEmpty()) {
            for (int i = 0; i < saved.size(); i++) {
                if (code_list.get(data.get(position)).equals(saved.get(i))) {

                    holder.checkbox.setChecked(true);
                    holder.checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }

            }
        }


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void SetOnClick(SetOnItemClick click) {
        this.click = click;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface SetOnItemClick {
        void itemClick(View v, int position, boolean checked);
    }

    public class SafetyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckedTextView checkbox;
        TextView no;

        public SafetyHolder(View itemView) {
            super(itemView);

            no = (TextView) itemView.findViewById(R.id.no);
            checkbox = (CheckedTextView) itemView.findViewById(R.id.checkbox);
            checkbox.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {


            if (click != null) {
                if (!checkbox.isChecked()) {
                    checkbox.setChecked(true);
                    click.itemClick(v, getAdapterPosition(), true);
                } else if (checkbox.isChecked()) {
                    checkbox.setChecked(false);
                    click.itemClick(v, getAdapterPosition(), false);

                }

            }

        }
    }
}
