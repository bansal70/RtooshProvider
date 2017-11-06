package com.rtoosh.provider.views.adapters;

/*
 * Created by rishav on 11/2/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rtoosh.provider.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyWorkAdapter extends RecyclerView.Adapter<MyWorkAdapter.ViewHolder>{
    private Context mContext;
    private List<String> listImages;

    public MyWorkAdapter(Context mContext, List<String> listImages) {
        this.mContext = mContext;
        this.listImages = listImages;
    }

    @Override
    public MyWorkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_my_work, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyWorkAdapter.ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(listImages.get(position))
                .into(holder.imgWork);
    }

    @Override
    public int getItemCount() {
        return listImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.imgWork) ImageView imgWork;
        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
