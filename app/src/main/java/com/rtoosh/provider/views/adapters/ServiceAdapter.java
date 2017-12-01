package com.rtoosh.provider.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Services;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder>{
    private Context mContext;
  //  private List<Services> listServices;
    private SparseArray<Services> servicesArray;

    public ServiceAdapter(Context mContext, SparseArray<Services> servicesArray) {
        this.mContext = mContext;
        this.servicesArray = servicesArray;
    }

    @Override
    public ServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_services, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ServiceAdapter.ViewHolder holder, int position) {
        int key = servicesArray.keyAt(position);
        final Services services = servicesArray.get(key);
        holder.cbServices.setText(services.getName());
        holder.cbServices.setId(position);

        if (services.isSelected()) {
            holder.cbServices.setChecked(true);
        } else {
            holder.cbServices.setChecked(false);
        }

        holder.cbServices.setOnClickListener(view -> {
            if (services.isSelected()) {
                for (int i=0; i<servicesArray.size(); i++) {
                    servicesArray.get(key).setSelected(false);
                }
            }
            else {
                for (int i=0; i<servicesArray.size(); i++) {
                    servicesArray.get(key).setSelected(true);
                }
            }
            //notifyDataSetChanged();


            /*if (holder.getAdapterPosition() == servicesArray.size() - 1) {
                if (services.isSelected()) {
                    for (int i=0; i<servicesArray.size(); i++) {
                        servicesArray.get(i).setSelected(false);
                    }
                }
                else {
                    for (int i=0; i<servicesArray.size(); i++) {
                        servicesArray.get(i).setSelected(true);
                    }
                }
                Toast.makeText(mContext, "" + services.getName(), Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();

            } else {
                if (services.isSelected()) {
                    servicesArray.get(servicesArray.size()-1).setSelected(false);
                    services.setSelected(false);
                }
                else {
                    services.setSelected(true);
                }
                notifyDataSetChanged();
            }*/

        });

    }

    @Override
    public int getItemCount() {
        return servicesArray.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cbServices) CheckBox cbServices;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
