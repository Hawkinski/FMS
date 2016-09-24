package com.example.sachin.fms.adapterPackage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sachin.fms.R;
import com.example.sachin.fms.dataSets.MaterialData;

import java.util.List;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {


    SetOnItemClick click;
    Context context;
    List<MaterialData> data;
    int c = 0;

    public SearchAdapter(Context context, List<MaterialData> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view, parent, false);
        SearchHolder holder = new SearchHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position) {


        String i = Integer.toString((position + 1));
        String j = (data.get(position).quantity);

        holder.count.setText(i);
        holder.product_code.setText(data.get(position).product_code);
        holder.product_des.setText(data.get(position).product_des);
        holder.quantity.setText(j);
        holder.uom.setText(data.get(position).uom);


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
        void itemClick(View v, int position);
    }

    public class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView count, product_code, product_des, uom, quantity;


        public SearchHolder(View itemView) {
            super(itemView);
            count = (TextView) itemView.findViewById(R.id.count);
            product_code = (TextView) itemView.findViewById(R.id.product_code);
            product_des = (TextView) itemView.findViewById(R.id.product_des);
            uom = (TextView) itemView.findViewById(R.id.uom);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (click != null) {
                click.itemClick(v, getAdapterPosition());
            }
        }
    }
}
