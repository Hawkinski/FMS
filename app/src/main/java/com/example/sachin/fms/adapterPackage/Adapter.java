package com.example.sachin.fms.adapterPackage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sachin.fms.R;
import com.example.sachin.fms.dataSets.AddedData;

import java.util.Collections;
import java.util.List;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.Add_View> {


    public List<AddedData> data = Collections.emptyList();
    public Context context;
    public SetOnItemClick click;

    public Adapter(Context context, List<AddedData> list) {
        this.context = context;
        this.data = list;
    }


    @Override
    public Add_View onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.added_material, parent, false);
        Add_View holder = new Add_View(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Add_View holder, int position) {

        String i = (data.get(position).quantity);
        holder.code.setText(data.get(position).code);
        holder.quantity.setText(i);
        holder.ib.setBackgroundResource(data.get(position).ib);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void SetOnDeleteClick(SetOnItemClick click) {
        this.click = click;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeAt(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
        notifyDataSetChanged();
    }


    public interface SetOnItemClick {
        void onItemClick(View v, int position);
    }

    public class Add_View extends RecyclerView.ViewHolder {

        TextView code, quantity;
        ImageButton ib;

        public Add_View(View itemView) {
            super(itemView);
            code = (TextView) itemView.findViewById(R.id.code);
            quantity = (TextView) itemView.findViewById(R.id.qt);
            ib = (ImageButton) itemView.findViewById(R.id.close);

            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.equals(ib) && click != null) {
                        click.onItemClick(v, getAdapterPosition());
                    }

                }
            });


        }


    }
}
