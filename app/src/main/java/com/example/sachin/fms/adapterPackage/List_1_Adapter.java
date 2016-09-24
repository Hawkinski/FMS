package com.example.sachin.fms.adapterPackage;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sachin.fms.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class List_1_Adapter extends RecyclerView.Adapter<List_1_Adapter.Add_View> {


    public List<String> data = Collections.emptyList();
    public Context context;
    public OnTitleClick click;

    public List_1_Adapter(Context context, List<String> list) {
        this.context = context;
        this.data = list;
    }


    @Override
    public Add_View onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_1, parent, false);
        Add_View holder = new Add_View(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Add_View holder, int position) {


        holder.title.setText(data.get(position));


    }

    public void SetOnTitleClick(OnTitleClick click) {
        this.click = click;

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnTitleClick {
        void OnTitleClick(View v, int position);
    }

    public class Add_View extends RecyclerView.ViewHolder {

        TextView title;

        public Add_View(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            CardView cv = (CardView) itemView.findViewById(R.id.click_title);


            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (click != null) {
                        click.OnTitleClick(v, getAdapterPosition());

                    }
                }
            });


        }

        public void removeAt(int position) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, data.size());
            notifyDataSetChanged();
        }
    }
}
