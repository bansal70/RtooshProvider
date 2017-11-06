package com.rtoosh.provider.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.AddService;

import java.util.List;

public class RegisterServiceDetailsAdapter extends RecyclerView.Adapter<RegisterServiceDetailsAdapter.ViewHolder> {
    private Context context;
    private List<AddService> listServices;

    public RegisterServiceDetailsAdapter(Context context, List<AddService> listServices) {
        this.context = context;
        this.listServices = listServices;
    }

    @Override
    public RegisterServiceDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_add_services, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RegisterServiceDetailsAdapter.ViewHolder holder, int position) {
        AddService addService = listServices.get(position);
        holder.tvServiceName.setText(addService.getName());
        holder.tvServiceContent.setText(addService.getDescription());
        holder.tvServicePrice.setText(String.format("%s %s", addService.getPrice(), Constants.CURRENCY));
        holder.tvServiceDuration.setText(addService.getDuration());
    }

    @Override
    public int getItemCount() {
        return listServices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvServiceName, tvServiceContent, tvServicePrice, tvServiceDuration;

        private ViewHolder(View itemView) {
            super(itemView);

            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceContent = itemView.findViewById(R.id.tvServiceContent);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            tvServiceDuration = itemView.findViewById(R.id.tvServiceDuration);
        }
    }
}
