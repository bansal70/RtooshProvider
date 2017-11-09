package com.rtoosh.provider.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.AddService;
import com.rtoosh.provider.model.custom.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterServiceDetailsAdapter extends RecyclerView.Adapter<RegisterServiceDetailsAdapter.ViewHolder> {
    private Context context;
    private List<AddService> listServices;
    private ItemClickListener clickListener;

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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tvServiceName) TextView tvServiceName;
        @BindView(R.id.tvServiceContent) TextView tvServiceContent;
        @BindView(R.id.tvServicePrice) TextView tvServicePrice;
        @BindView(R.id.tvServiceDuration) TextView tvServiceDuration;
        @BindView(R.id.ivEditService) ImageView ivEditService;
        @BindView(R.id.ivRemoveService) ImageView ivRemoveService;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ivEditService.setOnClickListener(this);
            ivRemoveService.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivEditService:
                    if (clickListener != null)
                        clickListener.onClick(ivEditService, getAdapterPosition());
                    break;

                case R.id.ivRemoveService:
                    if (clickListener != null)
                        clickListener.onClick(ivRemoveService, getAdapterPosition());

                    /*listServices.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), listServices.size());*/
                    break;
            }

        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}
