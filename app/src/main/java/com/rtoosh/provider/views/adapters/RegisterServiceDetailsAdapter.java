package com.rtoosh.provider.views.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.AddService;
import com.rtoosh.provider.model.custom.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterServiceDetailsAdapter extends RecyclerView.Adapter<RegisterServiceDetailsAdapter.ViewHolder> {
    private Context context;
    private List<AddService> listServices;
    private Dialog dialogSelection;

    RegisterServiceDetailsAdapter(Context context, List<AddService> listServices) {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvServiceName) TextView tvServiceName;
        @BindView(R.id.tvServiceContent) TextView tvServiceContent;
        @BindView(R.id.tvServicePrice) TextView tvServicePrice;
        @BindView(R.id.tvServiceDuration) TextView tvServiceDuration;
        @BindView(R.id.ivEditService) ImageView ivEditService;
        @BindView(R.id.ivRemoveService) ImageView ivRemoveService;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ivEditService)
        public void editService() {
            initSelectionDialog(listServices.get(getAdapterPosition()), getAdapterPosition());
            //dialogSelection.show();
        }

        @OnClick(R.id.ivRemoveService)
        public void removeService() {
            listServices.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
            notifyItemRangeChanged(getAdapterPosition(), listServices.size());
        }

    }

    private void initSelectionDialog(AddService update_selection, int pos) {
        dialogSelection = Utils.createDialog(context, R.layout.dialog_add_services);
        final EditText editServiceName = dialogSelection.findViewById(R.id.editServiceName);
        final EditText editServiceContent = dialogSelection.findViewById(R.id.editServiceContent);
        final EditText editServicePrice = dialogSelection.findViewById(R.id.editServicePrice);
        final TextView editServiceDuration = dialogSelection.findViewById(R.id.editServiceDuration);

        editServiceDuration.setOnClickListener(v -> Utils.setTimePicker24Hours(context, editServiceDuration));

        editServiceName.setText(update_selection.getName());
        editServiceContent.setText(update_selection.getDescription());
        editServicePrice.setText(update_selection.getPrice());
        editServiceDuration.setText(update_selection.getDuration());

        dialogSelection.findViewById(R.id.tvDoneSelection).setOnClickListener(view -> {
            String serviceName = editServiceName.getText().toString().trim();
            String description = editServiceContent.getText().toString().trim();
            String price = editServicePrice.getText().toString().trim();
            String duration = editServiceDuration.getText().toString().trim();
            if (serviceName.isEmpty() || description.isEmpty() || price.isEmpty() || duration.isEmpty()) {
                Toast.makeText(context, R.string.toast_fill_data, Toast.LENGTH_SHORT).show();
            } else {
                dialogSelection.dismiss();
                AddService addService = new AddService(serviceName, description, price, duration);
                listServices.set(pos, addService);

                notifyItemChanged(pos);
                notifyDataSetChanged();
            }

        });

        dialogSelection.show();
    }

}
