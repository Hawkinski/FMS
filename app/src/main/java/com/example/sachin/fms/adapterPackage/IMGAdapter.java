package com.example.sachin.fms.adapterPackage;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.sachin.fms.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class IMGAdapter extends RecyclerView.Adapter<IMGAdapter.Grid_View_Holder> {

    Context context;
    setOnImageClick imageclick;

    List<Bitmap> images = Collections.emptyList();

    public IMGAdapter(Context context, List<Bitmap> bitmaps) {
        this.images = bitmaps;
        this.context = context;


    }


    @Override
    public Grid_View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_card_view, parent, false);
        Grid_View_Holder holder = new Grid_View_Holder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(Grid_View_Holder holder, int position) {

        holder.images.setImageBitmap(images.get(position));
        animate(holder);

    }

    public void SetOnClick(setOnImageClick click) {
        this.imageclick = click;

    }

    @Override
    public int getItemCount() {
        return images.size();

    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }


    public interface setOnImageClick {
        void setOnImageClick(View v, int position);


    }

    public class Grid_View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;
        ImageView images;


        public Grid_View_Holder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.grid_card_view);
            images = (ImageView) itemView.findViewById(R.id.grid_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (imageclick != null) {
                imageclick.setOnImageClick(v, getAdapterPosition());
            }
        }
    }
}
