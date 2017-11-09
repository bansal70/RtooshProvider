package com.rtoosh.provider.views.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtoosh.provider.R;


public class SliderAdapter extends PagerAdapter {

    private Context mContext;

    public SliderAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public Object instantiateItem(ViewGroup collection, final int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_slider, collection, false);
        ImageView ivImage = view.findViewById(R.id.ivImage);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDes = view.findViewById(R.id.tvDes);

        if(position==0){
            ivImage.setImageResource(R.mipmap.event_collection);
            tvTitle.setText(mContext.getString(R.string.events_collection));
            tvDes.setText(mContext.getString(R.string.slider_one_des));
        }else if (position==1){
            ivImage.setImageResource(R.mipmap.connect_follow);
            tvTitle.setText(mContext.getString(R.string.connect_follow));
            tvDes.setText(mContext.getString(R.string.slider_two_des));
        }else {
            ivImage.setImageResource(R.mipmap.book_now);
            tvTitle.setText(R.string.book_share);
            tvDes.setText(R.string.slider_three_des);
        }

        collection.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        arg0.removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
