package com.rtoosh.provider.views.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.AddService;
import com.rtoosh.provider.model.POJO.register.RegisterServiceData;
import com.rtoosh.provider.model.custom.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterServiceAdapter extends RecyclerView.Adapter<RegisterServiceAdapter.ViewHolder> {
    private Context context;
    private Dialog dialogSelection;
    private List<AddService> listAddServices;
    private List<RegisterServiceData> listData;
    private int position;
    private String id, name, image;
    private TextView tvServiceDuration;

    public RegisterServiceAdapter(Context context, List<RegisterServiceData> listData, List<AddService> listAddServices) {
        this.context = context;
        this.listData = listData;
        this.listAddServices = listAddServices;
    }

    @Override
    public RegisterServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_register_services, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RegisterServiceAdapter.ViewHolder holder, int position) {
        RegisterServiceData data = listData.get(position);

        holder.tvService.setText(data.getName());
        Glide.with(context).load(data.getImage()).into(holder.imgCategory);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));

        listAddServices = data.getListAddServices();
        RegisterServiceDetailsAdapter registerServiceDetailsAdapter = new RegisterServiceDetailsAdapter(context,
                data.getListAddServices());
        holder.recyclerView.setAdapter(registerServiceDetailsAdapter);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvService) TextView tvService;
        @BindView(R.id.imgCategory) ImageView imgCategory;
        @BindView(R.id.recyclerServices) RecyclerView recyclerView;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @OnClick(R.id.imgAddService)
        public void addService() {
            position = getAdapterPosition();
            RegisterServiceData data = listData.get(position);
            id = data.getId();
            name = data.getName();
            image = data.getImage();
            listAddServices = data.getListAddServices();
            initSelectionDialog();
        }
    }

    private void initSelectionDialog() {
        dialogSelection = Utils.createDialog(context, R.layout.dialog_add_services);
        final TextView tvServiceTitle = dialogSelection.findViewById(R.id.tvServiceTitle);
        final EditText editServiceName = dialogSelection.findViewById(R.id.editServiceName);
        final EditText editServiceContent = dialogSelection.findViewById(R.id.editServiceContent);
        final EditText editServicePrice = dialogSelection.findViewById(R.id.editServicePrice);
        tvServiceDuration = dialogSelection.findViewById(R.id.editServiceDuration);
       // editServiceDuration.addTextChangedListener(new MaskWatcher("##:##"));
        RegisterServiceData serviceData = listData.get(position);
        tvServiceTitle.setText(String.format("%s %s", serviceData.getName(), context.getString(R.string.selection_new_service)));

        tvServiceDuration.setOnClickListener(v -> initTimeDialog());

        dialogSelection.findViewById(R.id.tvDoneSelection).setOnClickListener(view -> {
            String serviceName = editServiceName.getText().toString().trim();
            String description = editServiceContent.getText().toString().trim();
            String price = editServicePrice.getText().toString().trim();
            String duration = tvServiceDuration.getText().toString().trim();
           /* int hours =0, min = 0;
            if (duration.contains(":")) {
                String[] split = duration.split(":");
                if (split.length > 1) {
                    hours = Integer.parseInt(split[0]);
                    min = Integer.parseInt(split[1]);
                }
            }*/
            if (serviceName.isEmpty() || description.isEmpty() || price.isEmpty() || duration.isEmpty()) {
                Toast.makeText(context, R.string.toast_fill_data, Toast.LENGTH_SHORT).show();
            }/* else if (duration.length() < 5 || !duration.contains(":") ||
                     String.valueOf(hours).length() > 2 || String.valueOf(min).length() > 2) {
                Toast.makeText(context, R.string.error_duration_format,
                        Toast.LENGTH_SHORT).show();
            } else if (hours > 12) {
                Toast.makeText(context, R.string.error_hours_range, Toast.LENGTH_SHORT).show();
            } else if (min > 59) {
                Toast.makeText(context, R.string.error_minutes_range, Toast.LENGTH_SHORT).show();
            }*/
            else {
                dialogSelection.dismiss();
                AddService addService = new AddService(serviceName, description, price, duration);
                listAddServices.add(addService);
                RegisterServiceData data = new RegisterServiceData(id, name, image, listAddServices);
                listData.set(position, data);
                notifyItemChanged(position);

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

        btSet.setOnClickListener(view -> {
            if (editHours.getText().toString().isEmpty() || editMinutes.getText().toString().isEmpty()) {
                tvInvalidTime.setVisibility(View.VISIBLE);
                return;
            }

          //  int hours = Integer.parseInt(editHours.getText().toString());
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
