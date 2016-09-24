package com.example.sachin.fms.adapterPackage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.example.sachin.fms.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.Filter_VIew> {


    public Context context;
    public SetOnItemClick click;
    HashMap<String, String> code_list = new HashMap<>();
    List<String> data = Collections.emptyList();

    public FilterAdapter(Context context, HashMap<String, String> list, List<String> data) {
        this.context = context;
        this.code_list = list;
        this.data = data;

    }


    @Override
    public Filter_VIew onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_2, parent, false);
        Filter_VIew holder = new Filter_VIew(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Filter_VIew holder, int position) {

        holder.checkbox.setText(code_list.get(data.get(position)));


    }

    public void SetOnItemClick(SetOnItemClick click) {
        this.click = click;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface SetOnItemClick {
        void itemClick(View v, int position, boolean checked);
    }

    public class Filter_VIew extends RecyclerView.ViewHolder implements View.OnClickListener {


        CheckedTextView checkbox;

        public Filter_VIew(View itemView) {
            super(itemView);
            checkbox = (CheckedTextView) itemView.findViewById(R.id.description);

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
