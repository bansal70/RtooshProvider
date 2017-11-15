package com.rtoosh.provider.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Portfolio;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Portfolio> listPortfolio;

    public PortfolioAdapter(Context mContext, ArrayList<Portfolio> listPortfolio) {
        this.mContext = mContext;
        this.listPortfolio = listPortfolio;
    }

    @Override
    public PortfolioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_portfolio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PortfolioAdapter.ViewHolder holder, int position) {
        Portfolio portfolio = listPortfolio.get(position);
        Glide.with(mContext)
                .load(portfolio.getImage())
                .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                        //.override(70, 70)
                        .placeholder(R.mipmap.ic_upload_image).dontTransform())
                .into(holder.imgPortfolio);

        if (portfolio.isSelected()) {
            holder.ivCheck.setVisibility(View.VISIBLE);
            holder.imgPortfolio.setBackgroundResource(R.drawable.custom_stroke_pink);
            holder.imgPortfolio.setPadding(5, 5, 5, 5);
        } else {
            holder.ivCheck.setVisibility(View.GONE);
            holder.imgPortfolio.setBackgroundResource(0);
            holder.imgPortfolio.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return listPortfolio.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.imgPortfolio) ImageView imgPortfolio;
        @BindView(R.id.ivCheck) ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.imgPortfolio)
        public void selectPhoto() {
            Portfolio portfolio = listPortfolio.get(getAdapterPosition());

            if (listPortfolio.get(getAdapterPosition()).isSelected()) {
                portfolio.setSelected(false);
                listPortfolio.set(getAdapterPosition(), portfolio);
                notifyItemChanged(getAdapterPosition());
               // listRemove.remove(portfolio.getId());
            } else {
                portfolio.setSelected(true);
                listPortfolio.set(getAdapterPosition(), portfolio);
                notifyItemChanged(getAdapterPosition());
               // listRemove.add(portfolio.getId());
            }
        }
    }

    public ArrayList<Portfolio> getPortfolio() {
        return listPortfolio;
    }

}
