package com.rtoosh.provider.views.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private TextView tvServiceDuration;

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
        tvServiceDuration = dialogSelection.findViewById(R.id.editServiceDuration);

        editServiceName.setText(update_selection.getName());
        editServiceContent.setText(update_selection.getDescription());
        editServicePrice.setText(update_selection.getPrice());
        tvServiceDuration.setText(update_selection.getDuration());

        tvServiceDuration.setOnClickListener(v -> initTimeDialog());

        dialogSelection.findViewById(R.id.tvDoneSelection).setOnClickListener(view -> {
            String serviceName = editServiceName.getText().toString().trim();
            String description = editServiceContent.getText().toString().trim();
            String price = editServicePrice.getText().toString().trim();
            String duration = tvServiceDuration.getText().toString().trim();
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

    private void initTimeDialog() {
        Dialog timeDialog = Utils.createDialog(context, R.layout.dialog_time_picker);
        EditText editHours = timeDialog.findViewById(R.id.editHours);
        EditText editMinutes = timeDialog.findViewById(R.id.editMinutes);
        Button btSet = timeDialog.findViewById(R.id.btSet);
        Button btCancel = timeDialog.findViewById(R.id.btCancel);
        TextView tvInvalidTime = timeDialog.findViewById(R.id.tvInvalidTime);

        if (tvServiceDuration.getText().toString().contains(":")) {
            String[] hhMM = tvServiceDuration.getText().toString().split(":");
            editHours.setText(hhMM[0]);
            editMinutes.setText(hhMM[1]);
        }

        btSet.setOnClickListener(view -> {
            if (editHours.getText().toString().isEmpty() || editMinutes.getText().toString().isEmpty()) {
                tvInvalidTime.setVisibility(View.VISIBLE);
                return;
            }

         //   int hours = Integer.parseInt(editHours.getText().toString());
            int minutes = Integer.parseInt(editMinutes.getText().toString());

            if (minutes > 59) {
                tvInvalidTime.setVisibility(View.VISIBLE);
                return;
            }

            timeDialog.dismiss();
            tvServiceDuration.setText(String.format("%s:%s", editHours.getText().toString(), editMinutes.getText().toString()));
        });

        btCancel.setOnClickListener(view -> timeDialog.cancel());

        timeDialog.show();
    }

}
