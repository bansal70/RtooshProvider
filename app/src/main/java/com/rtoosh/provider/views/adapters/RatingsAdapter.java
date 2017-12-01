package com.rtoosh.provider.views.adapters;

/*
 * Created by rishav on 11/21/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.RatingsResponse;
import com.rtoosh.provider.model.custom.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder>{
    private Context mContext;
    private List<RatingsResponse.Data> ratingsList;

    public RatingsAdapter(Context mContext, List<RatingsResponse.Data> ratingsList) {
        this.mContext = mContext;
        this.ratingsList = ratingsList;
    }

    @Override
    public RatingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_ratings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RatingsAdapter.ViewHolder holder, int position) {
        RatingsResponse.Data data = ratingsList.get(position);
        RatingsResponse.Review review = data.review;
        float cleanliness = Float.parseFloat(review.ratingClean);
        float quality = Float.parseFloat(review.ratingQuality);
        float arrival = Float.parseFloat(review.ratingArrival);

        holder.rbCleanliness.setRating(cleanliness);
        holder.rbQuality.setRating(quality);
        holder.rbArrival.setRating(arrival);

        if (!review.message.isEmpty())
            holder.tvFeedback.setText(review.message);
        else
            holder.tvFeedback.setText(mContext.getString(R.string.no_feedback));

        holder.tvReviewDate.setText(DateUtils.getDateFormat(review.modified));
    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rbCleanliness) RatingBar rbCleanliness;
        @BindView(R.id.rbQuality) RatingBar rbQuality;
        @BindView(R.id.rbArrival) RatingBar rbArrival;
        @BindView(R.id.tvFeedback) TextView tvFeedback;
        @BindView(R.id.tvReviewDate) TextView tvReviewDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
