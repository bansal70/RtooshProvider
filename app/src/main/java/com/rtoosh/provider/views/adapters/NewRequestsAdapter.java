package com.rtoosh.provider.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtoosh.provider.R;

public class NewRequestsAdapter extends RecyclerView.Adapter<NewRequestsAdapter.ViewHolder>{
    private Context context;

    public NewRequestsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public NewRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_new_requests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewRequestsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
